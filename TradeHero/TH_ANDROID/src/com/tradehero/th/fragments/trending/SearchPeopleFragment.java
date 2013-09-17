package com.tradehero.th.fragments.trending;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.SearchPeopleAdapter;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.models.User;
import com.tradehero.th.network.CallbackWithSpecificNotifiers;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.UserService;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchPeopleFragment extends AbstractTrendingFragment
{
    private final static String TAG = SearchPeopleFragment.class.getSimpleName();

    private ListView mSearchListView;
    private ProgressBar mProgressSpinner;
    private View actionBar;

    private SearchPeopleAdapter searchPeopleAdapter;

    private UserService userService;
    private List<UserSearchResultDTO> userDTOList;

    @Override protected String getLogTag()
    {
        return TAG;
    }

    @Override protected int getLayoutResourceId()
    {
        return R.layout.fragment_trending;
    }

    @Override public void onAttach(Activity activity)
    {
        THLog.i(TAG, "Attached to activity");
        super.onAttach(activity);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override protected void initViews(View view)
    {
        super.initViews(view);
        mSearchListView = (ListView) view.findViewById(R.id.trending_listview);
        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (userDTOList != null && userDTOList.size() > 0)
        {
            setDataAdapterToListView(userDTOList);
        }

        mSearchListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                User securityCompactDTO = (User) parent.getItemAtPosition(position);

                THToast.show("Disabled for now");
                // TODO put back in
            }
        });

        refreshGridView();
    }

    @Override public void onDetach()
    {
        THLog.i(TAG, "Detached from activity");
        super.onDetach();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        THLog.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setDataAdapterToListView(List<UserSearchResultDTO> users)
    {
        this.userDTOList = users;

        if (searchPeopleAdapter == null)
        {
            searchPeopleAdapter = new SearchPeopleAdapter(getActivity(), users);
        }
        else
        {
            searchPeopleAdapter.clear();
            searchPeopleAdapter.addAll(users);
            // TODO implement loader pattern
        }
        mSearchListView.setAdapter(searchPeopleAdapter);
    }

    @Override protected void refreshGridView()
    {
        if (userService == null)
        {
            userService = NetworkEngine.createService(UserService.class);
        }
        userService.searchUsers(getActionBarStatus().searchText, getPage(), getPerPage(), createCallbackForTrending());
    }

    private CallbackWithSpecificNotifiers<List<UserSearchResultDTO>> createCallbackForTrending ()
    {
        return new CallbackWithSpecificNotifiers<List<UserSearchResultDTO>>()
        {
            @Override public void notifyIsQuerying(boolean isQuerying)
            {
            }

            @Override public void success(List<UserSearchResultDTO> returned, Response response)
            {
                super.success(returned, response);
                setDataAdapterToListView(returned);
            }

            @Override public void failure(RetrofitError retrofitError)
            {
                super.failure(retrofitError);
            }
        };
    }

    @Override public boolean isRequiredToAct()
    {
        return getActionBarStatus() != null &&
                getActionBarStatus().searchType == TrendingSearchType.PEOPLE &&
                getActionBarStatus().searchText != null;
    }
}
