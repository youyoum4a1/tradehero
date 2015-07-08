package com.tradehero.chinabuild.fragment.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tradehero.th.R;

public class SecurityOptActualFragment extends android.support.v4.app.Fragment implements View.OnClickListener{
    private TextView mGoButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_opt_actual, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mGoButton = (TextView)view.findViewById(R.id.go_button);
        mGoButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.go_button) {
            getActivity().onBackPressed();
//            ((SecurityOptActivity)getActivity()).gotoDashboard(MainTabFragmentStockGod.class.getName(), new Bundle());
            getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        }
    }
}
