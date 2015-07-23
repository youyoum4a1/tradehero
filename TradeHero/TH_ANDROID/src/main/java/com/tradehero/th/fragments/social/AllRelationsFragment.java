package com.tradehero.th.fragments.social;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnItemClick;
import com.android.internal.util.Predicate;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.th.R;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.PaginatedAllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.social.message.NewPrivateMessageFragment;
import com.tradehero.th.models.social.FollowRequest;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ToastOnErrorAction1;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.AdapterViewUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class AllRelationsFragment extends BaseFragment
        implements HasSelectedItem
{
    @Inject AllowableRecipientPaginatedCacheRx allowableRecipientPaginatedCache;
    @Inject UserMessagingRelationshipCacheRx userMessagingRelationshipCache;
    @Inject protected THBillingInteractorRx userInteractorRx;

    @Bind(R.id.sending_to_header) View sendingToHeader;
    @Bind(R.id.relations_list) ListView relationsListView;

    private RelationsListItemAdapter relationsListItemAdapter;

    List<AllowableRecipientDTO> relationsList;
    private AllowableRecipientDTO selectedItem;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        relationsListItemAdapter = new RelationsListItemAdapter(
                activity,
                R.layout.relations_list_item);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_all_relations, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        relationsListView.setAdapter(relationsListItemAdapter);

        sendingToHeader.setVisibility(isForReturn() ? View.GONE : View.VISIBLE);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(isForReturn() ? R.string.message_pick_relation : R.string.message_center_new_message_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onStart()
    {
        super.onStart();
        downloadRelations();
        onStopSubscriptions.add(relationsListItemAdapter.getFollowRequestObservable()
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

    @Override public void onDestroyView()
    {
        relationsListView.setAdapter(null);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        relationsListItemAdapter = null;
        super.onDetach();
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

    public void downloadRelations()
    {
        final DismissDialogAction0 dismissProgress = new DismissDialogAction0(
                ProgressDialogUtil.create(getActivity(), getString(R.string.downloading_relations)));
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                allowableRecipientPaginatedCache.get(new SearchAllowableRecipientListType(null, null, null)))
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(dismissProgress)
                .doOnUnsubscribe(dismissProgress)
                .subscribe(
                        new Action1<Pair<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO>>()
                        {
                            @Override public void call(
                                    Pair<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO> pair)
                            {
                                dismissProgress.call();
                                AllRelationsFragment.this.onNextRecipients(pair);
                            }
                        },
                        new ToastOnErrorAction1()));
    }

    protected void onNextRecipients(Pair<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO> pair)
    {
        relationsList = pair.second.getData();
        relationsListItemAdapter.setNotifyOnChange(false);
        relationsListItemAdapter.clear();
        relationsListItemAdapter.addAll(relationsList);
        relationsListItemAdapter.setNotifyOnChange(true);
        relationsListItemAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("UnusedParameters")
    @OnItemClick(R.id.relations_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        selectedItem = relationsList.get(position);
        if (isForReturn())
        {
            navigator.get().popFragment();
            return;
        }
        pushPrivateMessageFragment(position);
    }

    protected void pushPrivateMessageFragment(int position)
    {
        Bundle args = new Bundle();
        NewPrivateMessageFragment.putCorrespondentUserBaseKey(args,
                relationsList.get(position).user.getBaseKey());
        navigator.get().pushFragment(NewPrivateMessageFragment.class, args);
    }

    protected void handlePremiumFollowRequested(final UserBaseKey userBaseKey)
    {
        //noinspection unchecked,RedundantCast
        onStopSubscriptions.add(AppObservable.bindFragment(
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
        onStopSubscriptions.add(AppObservable.bindFragment(
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
                            downloadRelations();
                            Timber.e("Strangely, there was no longer the relation in cache");
                        }
                    }

                    @Override public void onError(Throwable e)
                    {
                    }

                    @Override public void onNext(Pair<UserBaseKey, UserMessagingRelationshipDTO> pair)
                    {
                        isEmpty = false;
                        relationsListItemAdapter.updateItem(userFollowed, pair.second);
                        AdapterViewUtils
                                .updateSingleRowWhere(relationsListView, AllowableRecipientDTO.class,
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