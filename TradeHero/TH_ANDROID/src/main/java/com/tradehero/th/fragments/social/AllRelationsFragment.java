package com.tradehero.th.fragments.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.social.message.NewPrivateMessageFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.social.OnPremiumFollowRequestedListener;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCache;
import com.tradehero.th.persistence.user.UserProfileCompactCache;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

public class AllRelationsFragment extends BasePurchaseManagerFragment
        implements AdapterView.OnItemClickListener
{
    List<AllowableRecipientDTO> mRelationsList;
    @Inject UserProfileCompactCache userProfileCompactCache;
    @Inject AllowableRecipientPaginatedCache allowableRecipientPaginatedCache;
    private DTOCache.GetOrFetchTask<SearchAllowableRecipientListType, PaginatedDTO<AllowableRecipientDTO>> allowableRecipientCacheTask;

    private RelationsListItemAdapter mRelationsListItemAdapter;
    @InjectView(R.id.relations_list) ListView mRelationsListView;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;

    @Override protected PremiumFollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
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
        mRelationsListItemAdapter = new RelationsListItemAdapter(getActivity(),
                getActivity().getLayoutInflater(), R.layout.relations_list_item);
        mRelationsListItemAdapter.setPremiumFollowRequestedListener(createFollowRequestedListener());
        mRelationsListView.setAdapter(mRelationsListItemAdapter);
        mRelationsListView.setOnItemClickListener(this);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle(R.string.message_center_new_message_title);
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
        mRelationsListItemAdapter.setItems(null);
        mRelationsListItemAdapter.setPremiumFollowRequestedListener(null);
        mRelationsListItemAdapter = null;
        mRelationsList = null;
        super.onDestroyView();
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    public void downloadRelations()
    {
        alertDialogUtilLazy.get().showProgressDialog(getActivity(), getString(R.string.downloading_relations));
        detachAllowableRecipientTask();

        allowableRecipientCacheTask = allowableRecipientPaginatedCache
                .getOrFetch(new SearchAllowableRecipientListType(null, null, null),
                        new AllRelationAllowableRecipientCacheListener());
        allowableRecipientCacheTask.execute();
    }

    private void detachAllowableRecipientTask()
    {
        if (allowableRecipientCacheTask != null)
        {
            allowableRecipientCacheTask.setListener(null);
        }
        allowableRecipientCacheTask = null;
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        pushPrivateMessageFragment(position);
    }

    protected void pushPrivateMessageFragment(int position)
    {
        Bundle args = new Bundle();
        NewPrivateMessageFragment.putCorrespondentUserBaseKey(args, mRelationsList.get(position).user.getBaseKey());
        getNavigator().pushFragment(NewPrivateMessageFragment.class, args);
    }

    protected void handleFollowRequested(UserBaseKey userBaseKey)
    {
        premiumFollowUser(userBaseKey);
    }

    protected class AllRelationAllowableRecipientCacheListener
        implements DTOCache.Listener<
            SearchAllowableRecipientListType,
            PaginatedDTO<AllowableRecipientDTO>>
    {
        @Override public void onDTOReceived(SearchAllowableRecipientListType key,
                PaginatedDTO<AllowableRecipientDTO> value, boolean fromCache)
        {
            //mRelationsList = userProfileCompactCache.get(value.getData());
            mRelationsList = value.getData();
            alertDialogUtilLazy.get().dismissProgressDialog();
            mRelationsListItemAdapter.setItems(mRelationsList);
            mRelationsListItemAdapter.notifyDataSetChanged();
        }

        @Override public void onErrorThrown(SearchAllowableRecipientListType key, Throwable error)
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
        @Override public void premiumFollowRequested(UserBaseKey userBaseKey)
        {
            handleFollowRequested(userBaseKey);
        }
    }

    protected class AllRelationsPremiumUserFollowedListener
            extends BasePurchaseManagerPremiumUserFollowedListener
    {
        @Override public void onUserFollowSuccess(UserBaseKey userFollowed,
                UserProfileDTO currentUserProfileDTO)
        {
            super.onUserFollowSuccess(userFollowed, currentUserProfileDTO);
            downloadRelations();
        }
    }
}