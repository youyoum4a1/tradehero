package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

public class LiveBuySellFragment extends DashboardFragment
{
    @Bind(R.id.btn_buy) Button buyBtn;
    @Bind(R.id.btn_sell) Button sellBtn;

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle("Facebook Inc");
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_live_buysell, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.btn_buy) public void buyBtnOnClicked()
    {
        Bundle args = new Bundle();
        navigator.get().pushFragment(LiveTransactionFragment.class, args);
    }

    @OnClick(R.id.btn_sell) public void sellBtnOnClicked()
    {
        Bundle args = new Bundle();
        navigator.get().pushFragment(LiveTransactionFragment.class, args);
    }
}
