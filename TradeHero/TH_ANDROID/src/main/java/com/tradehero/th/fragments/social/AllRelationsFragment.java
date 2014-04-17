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
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.social.message.PrivateMessageFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCache;
import com.tradehero.th.persistence.user.UserProfileCompactCache;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

public class AllRelationsFragment extends BasePurchaseManagerFragment
        implements AdapterView.OnItemClickListener
{
    List<UserProfileCompactDTO> mRelationsList;
    @Inject UserProfileCompactCache userProfileCompactCache;
    @Inject AllowableRecipientPaginatedCache allowableRecipientPaginatedCache;
    private DTOCache.GetOrFetchTask<SearchAllowableRecipientListType, PaginatedDTO<UserBaseKey>> allowableRecipientCacheTask;

    private RelationsListItemAdapter mRelationsListItemAdapter;
    @InjectView(R.id.relations_list) ListView mRelationsListView;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;

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
        alertDialogUtilLazy.get().showProgressDialog(getActivity());
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
        args.putBundle(PrivateMessageFragment.CORRESPONDENT_USER_BASE_BUNDLE_KEY,
                mRelationsList.get(position).getBaseKey().getArgs());
        getNavigator().pushFragment(PrivateMessageFragment.class, args);
    }

    protected class AllRelationAllowableRecipientCacheListener
        implements DTOCache.Listener<
            SearchAllowableRecipientListType,
            PaginatedDTO<UserBaseKey>>
    {
        @Override public void onDTOReceived(SearchAllowableRecipientListType key,
                PaginatedDTO<UserBaseKey> value, boolean fromCache)
        {
            mRelationsList = userProfileCompactCache.get(value.getData());
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
}