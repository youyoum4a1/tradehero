package com.tradehero.firmbargain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tradehero.th.R;

/**
 * Created by palmer on 15/7/16.
 */
public class SecurityOptActualSubBuyFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_opt_sub_buysell, container, false);
        return view;
    }
}
