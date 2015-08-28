package com.tradehero.th.fragments.social;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.picasso.Picasso;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedRecyclerAdapter;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.BasePagedRecyclerRxFragment;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.social.message.AbstractPrivateMessageFragment;
import com.tradehero.th.fragments.social.message.NewPrivateMessageFragment;
import com.tradehero.th.fragments.social.message.ReplyPrivateMessageFragment;
import com.tradehero.th.persistence.message.MessageThreadHeaderCacheRx;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCacheRx;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import retrofit.RetrofitError;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class AllRelationsRecyclerFragment extends BasePagedRecyclerRxFragment<
        SearchAllowableRecipientListType,
        RelationItemDisplayDTO,
        RelationItemDisplayDTO.DTOList<RelationItemDisplayDTO>,
        RelationItemDisplayDTO.DTOList<RelationItemDisplayDTO>>
        implements HasSelectedItem
{
    private static final int PER_PAGE = 50;

    @Inject AllowableRecipientPaginatedCacheRx allowableRecipientPaginatedCache;
    @Inject Picasso picasso;
    @Inject DashboardNavigator navigator;
    @Inject MessageThreadHeaderCacheRx messageThreadHeaderCache;

    private AllowableRecipientDTO selectedItem;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setActionBarTitle(isForReturn() ? R.string.message_pick_relation : R.string.message_center_new_message_title);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_all_relations, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.addItemDecoration(new TypedRecyclerAdapter.DividerItemDecoration(getActivity()));
        recyclerView.setHasFixedSize(true);
    }

    @NonNull @Override protected PagedRecyclerAdapter<RelationItemDisplayDTO> createItemViewAdapter()
    {
        AllRelationsRecyclerAdapter recyclerAdapter = new AllRelationsRecyclerAdapter(picasso);
        recyclerAdapter.setOnItemClickedListener(new TypedRecyclerAdapter.OnItemClickedListener<RelationItemDisplayDTO>()
        {
            @Override
            public void onItemClicked(int position, TypedRecyclerAdapter.TypedViewHolder<RelationItemDisplayDTO> viewHolder,
                    RelationItemDisplayDTO object)
            {
                if (isForReturn())
                {
                    selectedItem = object.allowableRecipientDTO;
                    navigator.popFragment();
                }
                else
                {
                    pushPrivateMessageFragment(object.allowableRecipientDTO.user.getBaseKey());
                }
            }
        });
        return recyclerAdapter;
    }

    private void pushPrivateMessageFragment(final UserBaseKey baseKey)
    {
        final DismissDialogAction0 dismissProgress = new DismissDialogAction0(
                ProgressDialogUtil.create(getActivity(), getString(R.string.loading_loading)));
        onDestroyViewSubscriptions.add(
                messageThreadHeaderCache.getOne(baseKey)
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
                                            pushPrivateMessageFragment(baseKey, null);
                                        }
                                        else
                                        {
                                            Timber.e(throwable, "Failed to listen to message thread header cache in all relations");
                                        }
                                    }
                                }));

    }

    private void pushPrivateMessageFragment(UserBaseKey user, MessageHeaderDTO header)
    {
        Bundle args = new Bundle();
        AbstractPrivateMessageFragment.putCorrespondentUserBaseKey(args, user);
        final Class<? extends AbstractPrivateMessageFragment> fragmentClass;
        if (header == null)
        {
            fragmentClass = NewPrivateMessageFragment.class;
        }
        else
        {
            ReplyPrivateMessageFragment.putDiscussionKey(args, DiscussionKeyFactory.create(header));
            fragmentClass = ReplyPrivateMessageFragment.class;
        }
        navigator.pushFragment(fragmentClass, args);

    }

    @NonNull @Override protected DTOCacheRx<SearchAllowableRecipientListType, RelationItemDisplayDTO.DTOList<RelationItemDisplayDTO>> getCache()
    {
        return new RelationItemDisplayPaginatedCache(allowableRecipientPaginatedCache);
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return true;
    }

    @NonNull @Override public SearchAllowableRecipientListType makePagedDtoKey(int page)
    {
        return new SearchAllowableRecipientListType(null, page, PER_PAGE);
    }

    private boolean isForReturn()
    {
        Bundle args = getArguments();
        return args != null && DashboardNavigator.getReturnFragment(args) != null;
    }

    @Override public void onStart()
    {
        super.onStart();
        scheduleRequestData();
    }

    @Nullable @Override public AllowableRecipientDTO getSelectedItem()
    {
        return this.selectedItem;
    }
}