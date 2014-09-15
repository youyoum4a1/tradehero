package com.tradehero.th.fragments.chinabuild.fragment.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th2.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import timber.log.Timber;
/*
   搜索  热门／历史
 */
public class SearchFragment extends DashboardFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //super.onCreateOptionsMenu(menu, inflater);
        //setHeadViewMiddleMain("搜索");
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.search_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
        Timber.d("OnRusme: StockGodList 1 ");
    }

}
