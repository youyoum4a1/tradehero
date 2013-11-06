package com.tradehero.th.fragments.billing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.billing.StoreItemAdapter;
import com.tradehero.th.fragments.base.DashboardFragment;

public class StoreScreenFragment extends DashboardFragment
{
    public static final String TAG = StoreScreenFragment.class.getSimpleName();

    private ListView listView;
    private StoreItemAdapter storeItemAdapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        listView = (ListView) view.findViewById(R.id.store_option_list);
        storeItemAdapter = new StoreItemAdapter(getActivity(), getActivity().getLayoutInflater());
        if (listView != null)
        {
            listView.setAdapter(storeItemAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    handlePositionClicked(position);
                }
            });
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.store_option_menu_title); // Add the changing cute icon
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        storeItemAdapter.notifyDataSetChanged();
    }

    @Override public void onDestroyView()
    {
        if (listView != null)
        {
            listView.setOnItemClickListener(null);
            listView.setAdapter(null);
        }
        listView = null;
        storeItemAdapter = null;
        super.onDestroyView();
    }

    @Override public boolean isTabBarVisible()
    {
        return true;
    }

    private void handlePositionClicked(int position)
    {
        switch (position)
        {
            case StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS:
                THToast.show("Pop buy virtual dollars");
                break;

            default:
                THToast.show("Clicked at position " + position);
                break;
        }
    }

}
