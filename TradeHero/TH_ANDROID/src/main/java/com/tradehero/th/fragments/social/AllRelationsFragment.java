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
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserRelationsDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.social.message.PrivateMessageFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class AllRelationsFragment extends BasePurchaseManagerFragment
        implements AdapterView.OnItemClickListener
{

    List<UserBaseDTO> mRelationsList;
    private MiddleCallback<UserRelationsDTO> relationsMiddleCallback;
    private RelationsListItemAdapter mRelationsListItemAdapter;

    @InjectView(R.id.relations_list) ListView mRelationsListView;

    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;

    public AllRelationsFragment()
    {
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
        detachRelationsMiddleCallback();
        relationsMiddleCallback = userServiceWrapperLazy.get()
                .getRelations(new RelationsCallback());
    }

    private void detachRelationsMiddleCallback()
    {
        if (relationsMiddleCallback != null)
        {
            relationsMiddleCallback.setPrimaryCallback(null);
        }
        relationsMiddleCallback = null;
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

    public class RelationsCallback implements Callback<UserRelationsDTO>
    {
        @Override public void success(UserRelationsDTO list, Response response)
        {
            mRelationsList = list.data;
            alertDialogUtilLazy.get().dismissProgressDialog();
            mRelationsListItemAdapter.setItems(list.data);
            mRelationsListItemAdapter.notifyDataSetChanged();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            alertDialogUtilLazy.get().dismissProgressDialog();
        }
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
        detachRelationsMiddleCallback();
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        mRelationsListView.setAdapter(null);
        mRelationsListView.setOnItemClickListener(null);
        mRelationsListView = null;
        mRelationsListItemAdapter.setItems(null);
        mRelationsListItemAdapter = null;
        mRelationsList = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }
}