package com.tradehero.th.fragments.social;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import com.android.internal.util.Predicate;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedDTOAdapter;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.AllowableRecipientDTOList;
import com.tradehero.th.api.users.PaginatedAllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.BasePagedListRxFragment;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.social.message.AbstractPrivateMessageFragment;
import com.tradehero.th.fragments.social.message.NewPrivateMessageFragment;
import com.tradehero.th.fragments.social.message.ReplyPrivateMessageFragment;
import com.tradehero.th.models.social.FollowRequest;
import com.tradehero.th.persistence.message.MessageThreadHeaderCacheRx;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ToastOnErrorAction1;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.AdapterViewUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import retrofit.RetrofitError;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class AllRelationsFragment extends BasePagedListRxFragment<
        SearchAllowableRecipientListType,
        AllowableRecipientDTO,
        AllowableRecipientDTOList,
        PaginatedAllowableRecipientDTO>
        implements HasSelectedItem
{
    @Inject AllowableRecipientPaginatedCacheRx allowableRecipientPaginatedCache;
    @Inject UserMessagingRelationshipCacheRx userMessagingRelationshipCache;
    @Inject protected THBillingInteractorRx userInteractorRx;
    @Inject MessageThreadHeaderCacheRx messageThreadHeaderCache;

    @Bind(R.id.sending_to_header) View sendingToHeader;

    private AllowableRecipientDTO selectedItem;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_all_relations, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        sendingToHeader.setVisibility(isForReturn() ? View.GONE : View.VISIBLE);
        requestDtos();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(isForReturn() ? R.string.message_pick_relation : R.string.message_center_new_message_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onStart()
    {
        super.onStart();
        onStopSubscriptions.add(((RelationsListItemAdapter) itemViewAdapter)
                .getFollowRequestObservable()
                .subscribe(
                        new Action1<FollowRequest>()
                        {
                            @Override public void call(FollowRequest request)
                            {
                                AllRelationsFragment.this.handlePremiumFollowRequested(request.heroId);
                            }
                        },
                        new EmptyAction1<Throwable>()
                ));
    }

    @Override public void onResume()
    {
        super.onResume();
        nearEndScrollListener.lowerEndFlag();
        nearEndScrollListener.activateEnd();
    }

    @NonNull @Override protected PagedDTOAdapter<AllowableRecipientDTO> createItemViewAdapter()
    {
        return new RelationsListItemAdapter(getActivity(), R.layout.relations_list_item);
    }

    @NonNull @Override protected DTOCacheRx<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO> getCache()
    {
        return allowableRecipientPaginatedCache;
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return true;
    }

    @NonNull @Override public SearchAllowableRecipientListType makePagedDtoKey(int page)
    {
        return new SearchAllowableRecipientListType(null, page, null);
    }

    @Nullable @Override public AllowableRecipientDTO getSelectedItem()
    {
        return selectedItem;
    }

    private boolean isForReturn()
    {
        Bundle args = getArguments();
        return args != null && DashboardNavigator.getReturnFragment(args) != null;
    }

    @Override protected void handleDtoClicked(AllowableRecipientDTO clicked)
    {
        super.handleDtoClicked(clicked);
        selectedItem = clicked;
        if (isForReturn())
        {
            navigator.get().popFragment();
            return;
        }
        pushPrivateMessageFragment(selectedItem.user.getBaseKey());
    }

    protected void pushPrivateMessageFragment(final UserBaseKey clickedUser)
    {
        final DismissDialogAction0 dismissProgress = new DismissDialogAction0(
                ProgressDialogUtil.create(getActivity(), getString(R.string.loading_loading)));
        onDestroyViewSubscriptions.add(
                messageThreadHeaderCache.getOne(clickedUser)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnUnsubscribe(dismissProgress)
                        .subscribe(
                                new Action1<Pair<UserBaseKey, MessageHeaderDTO>>()
                                {
                                    @Override public void call(Pair<UserBaseKey, MessageHeaderDTO> pair)
                                    {
                                        pushPrivateMessageFragment(pair.first, pair.second);
                                    }
                                },
                                new Action1<Throwable>()
                                {
                                    @Override public void call(Throwable throwable)
                                    {
                                        if (throwable instanceof RetrofitError
                                                && ((RetrofitError) throwable).getResponse() != null
                                                && ((RetrofitError) throwable).getResponse().getStatus() == 404)
                                        {
                                            pushPrivateMessageFragment(clickedUser, null);
                                        }
                                        else
                                        {
                                            Timber.e(throwable, "Failed to listen to message thread header cache in all relations");
                                        }
                                    }
                                }));
    }

    protected void pushPrivateMessageFragment(@NonNull UserBaseKey clickedUser, @Nullable MessageHeaderDTO messageHeader)
    {
        Bundle args = new Bundle();
        AbstractPrivateMessageFragment.putCorrespondentUserBaseKey(args, clickedUser);
        final Class<? extends AbstractPrivateMessageFragment> fragmentClass;
        if (messageHeader == null)
        {
            fragmentClass = NewPrivateMessageFragment.class;
        }
        else
        {
            ReplyPrivateMessageFragment.putDiscussionKey(args, DiscussionKeyFactory.create(messageHeader));
            fragmentClass = ReplyPrivateMessageFragment.class;
        }
        navigator.get().pushFragment(fragmentClass, args);

    }

    protected void handlePremiumFollowRequested(final UserBaseKey userBaseKey)
    {
        //noinspection unchecked,RedundantCast
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userInteractorRx.purchaseAndPremiumFollowAndClear(userBaseKey))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1()
                        {
                            @Override public void call(Object result)
                            {
                                AllRelationsFragment.this.forceUpdateLook(userBaseKey);
                            }
                        },
                        new ToastOnErrorAction1()
                ));
    }

    protected void forceUpdateLook(@NonNull final UserBaseKey userFollowed)
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userMessagingRelationshipCache.get(userFollowed))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<UserBaseKey, UserMessagingRelationshipDTO>>()
                {
                    private boolean isEmpty = true;

                    @Override public void onCompleted()
                    {
                        if (isEmpty)
                        {
                            allowableRecipientPaginatedCache.invalidateAll();
                            Timber.e("Strangely, there was no longer the relation in cache");
                        }
                    }

                    @Override public void onError(Throwable e)
                    {
                    }

                    @Override public void onNext(Pair<UserBaseKey, UserMessagingRelationshipDTO> pair)
                    {
                        isEmpty = false;
                        ((RelationsListItemAdapter) itemViewAdapter).updateItem(userFollowed, pair.second);
                        AdapterViewUtils
                                .updateSingleRowWhere(listView, AllowableRecipientDTO.class,
                                        new Predicate<AllowableRecipientDTO>()
                                        {
                                            @Override public boolean apply(AllowableRecipientDTO allowableRecipientDTO)
                                            {
                                                return allowableRecipientDTO != null
                                                        && allowableRecipientDTO.user.getBaseKey().equals(userFollowed);
                                            }
                                        });
                    }
                }));
    }
}