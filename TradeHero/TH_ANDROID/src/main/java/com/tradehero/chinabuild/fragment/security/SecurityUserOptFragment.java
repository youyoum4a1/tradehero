package com.tradehero.chinabuild.fragment.security;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by palmer on 15/6/9.
 */
public class SecurityUserOptFragment extends DashboardFragment{

    private int colorRed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colorRed = getResources().getColor(R.color.color_red);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        setHeadViewMiddleMain("一个股票(666666)" );
        setHeadViewMiddleSubText("999 +10.10% +10.10", colorRed);
    }

}
