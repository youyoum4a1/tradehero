package com.tradehero.th.fragments.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import android.view.Menu;
import android.view.MenuInflater;
import com.android.internal.util.Predicate;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.PaginatedAllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.social.message.NewPrivateMessageFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.social.OnPremiumFollowRequestedListener;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCompactCache;
import com.tradehero.th.utils.AdapterViewUtils;
import com.tradehero.th.utils.AlertDialogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.Lazy;
import timber.log.Timber;

public class AllRelationsFragment extends BasePurchaseManagerFragment
        implements AdapterView.OnItemClickListener
{
    List<AllowableRecipientDTO> mRelationsList;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject UserProfileCompactCache userProfileCompactCache;
    @Inject AllowableRecipientPaginatedCache allowableRecipientPaginatedCache;
    @Inject UserMessagingRelationshipCache userMessagingRelationshipCache;
    @Inject Lazy<AdapterViewUtils> adapterViewUtils;

    private
    DTOCacheNew.Listener<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO>
            allowableRecipientCacheListener;

    private RelationsListItemAdapter mRelationsListItemAdapter;
    @InjectView(R.id.relations_list) ListView mRelationsListView;
    @Inject DashboardNavigator navigator;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        allowableRecipientCacheListener = createAllowableRecipientListener();
    }

    @Override
    protected FollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new AllRelationsPremiumUserFollowedListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_all_relations, container, false);
        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        mRelationsListItemAdapter = new RelationsListItemAdapter(
                getActivity(),
                R.layout.relations_list_item);
        mRelationsListItemAdapter.setPremiumFollowRequestedListener(
                createFollowRequestedListener());
        mRelationsListView.setAdapter(mRelationsListItemAdapter);
        mRelationsListView.setOnItemClickListener(this);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(R.string.message_center_new_message_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        downloadRelations();
    }

    @Override public void onPause()
    {
        alertDialogUtilLazy.get().dismissProgressDialog();
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        detachAllowableRecipientTask();
        mRelationsListView.setAdapter(null);
        mRelationsListView.setOnItemClickListener(null);
        mRelationsListView = null;
        mRelationsListItemAdapter.clear();
        mRelationsListItemAdapter.setPremiumFollowRequestedListener(null);
        mRelationsListItemAdapter = null;
        mRelationsList = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        allowableRecipientCacheListener = null;
        super.onDestroy();
    }

    public void downloadRelations()
    {
        alertDialogUtilLazy.get()
                .showProgressDialog(getActivity(), getString(R.string.downloading_relations));
        detachAllowableRecipientTask();
        allowableRecipientPaginatedCache.register(new SearchAllowableRecipientListType(null, null, null), allowableRecipientCacheListener);
        allowableRecipientPaginatedCache.getOrFetchAsync(new SearchAllowableRecipientListType(null, null, null));
    }

    private void detachAllowableRecipientTask()
    {
        allowableRecipientPaginatedCache.unregister(allowableRecipientCacheListener);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        pushPrivateMessageFragment(position);
    }

    protected void pushPrivateMessageFragment(int position)
    {
        Bundle args = new Bundle();
        NewPrivateMessageFragment.putCorrespondentUserBaseKey(args,
                mRelationsList.get(position).user.getBaseKey());
        navigator.pushFragment(NewPrivateMessageFragment.class, args);
    }

    protected void handleFollowRequested(UserBaseKey userBaseKey)
    {
        premiumFollowUser(userBaseKey);
    }

    protected DTOCacheNew.Listener<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO>
        createAllowableRecipientListener()
    {
        return new AllRelationAllowableRecipientCacheListener();
    }

    protected class AllRelationAllowableRecipientCacheListener
            implements DTOCacheNew.Listener<
            SearchAllowableRecipientListType,
            PaginatedAllowableRecipientDTO>
    {
        @Override public void onDTOReceived(@NotNull SearchAllowableRecipientListType key,
                @NotNull PaginatedAllowableRecipientDTO value)
        {
            //mRelationsList = userProfileCompactCache.get(value.getData());
            mRelationsList = value.getData();
            alertDialogUtilLazy.get().dismissProgressDialog();
            mRelationsListItemAdapter.addAll(mRelationsList);
            mRelationsListItemAdapter.notifyDataSetChanged();
        }

        @Override public void onErrorThrown(@NotNull SearchAllowableRecipientListType key, @NotNull Throwable error)
        {
            THToast.show(new THException(error));
            alertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    protected OnPremiumFollowRequestedListener createFollowRequestedListener()
    {
        return new AllRelationsFollowRequestedListener();
    }

    protected class AllRelationsFollowRequestedListener implements OnPremiumFollowRequestedListener
    {
        @Override public void premiumFollowRequested(@NotNull UserBaseKey userBaseKey)
        {
            handleFollowRequested(userBaseKey);
        }
    }

    protected class AllRelationsPremiumUserFollowedListener
            implements FollowUserAssistant.OnUserFollowedListener
    {
        @Override public void onUserFollowSuccess(
                @NotNull UserBaseKey userFollowed,
                @NotNull UserProfileDTO currentUserProfileDTO)
        {
            forceUpdateLook(userFollowed);
        }

        @Override public void onUserFollowFailed(@NotNull UserBaseKey userFollowed, @NotNull Throwable error)
        {
            // nothing for now
        }
    }

    protected void forceUpdateLook(@NotNull final UserBaseKey userFollowed)
    {
        final UserMessagingRelationshipDTO newRelationship = userMessagingRelationshipCache.get(userFollowed);
        if (newRelationship != null)
        {
            mRelationsListItemAdapter.updateItem(userFollowed, newRelationship);
            adapterViewUtils.get().updateSingleRowWhere(mRelationsListView, AllowableRecipientDTO.class, new Predicate<AllowableRecipientDTO>()
            {
                @Override public boolean apply(AllowableRecipientDTO allowableRecipientDTO)
                {
                    return allowableRecipientDTO != null
                            && allowableRecipientDTO.user.getBaseKey().equals(userFollowed);
                }
            });
        }
        else
        {
            allowableRecipientPaginatedCache.invalidateAll();
            downloadRelations();
            Timber.e("Strangely, there was no longer the relation in cache");
        }
    }
}