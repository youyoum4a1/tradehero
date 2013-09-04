package com.tradehero.th.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.tradehero.th.R;
import com.tradehero.th.activities.TradeHeroTabActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class LeftmenueFragment extends Fragment implements OnClickListener
{

    private LinearLayout mHomeWraper, mTrendingwrapper;
    private RelativeLayout slidingPanel;
    //	private FragmentManager mFragmentmanager;
    //	private FragmentTransaction mFragTrans;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.leftmenue_container, container, false);
        _setView(v);

        return v;
    }

    private void _setView(View v)
    {

        mHomeWraper = (LinearLayout) v.findViewById(R.id.home_wrapper);
        mTrendingwrapper = (LinearLayout) v.findViewById(R.id.trending_wrapper);
        mHomeWraper.setOnClickListener(this);
        mTrendingwrapper.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.home_wrapper:

                ((TradeHeroTabActivity) getActivity()).showSlidingMenue(true);

                break;
            case R.id.trending_wrapper:

                ((TradeHeroTabActivity) getActivity()).showTabContent("Trending");
                ((TradeHeroTabActivity) getActivity()).showSlidingMenue(true);

                break;

            default:
                break;
        }
    }
}
