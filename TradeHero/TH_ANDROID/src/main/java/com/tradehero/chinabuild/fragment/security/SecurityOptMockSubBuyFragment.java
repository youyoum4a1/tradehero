package com.tradehero.chinabuild.fragment.security;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.tradehero.th.R;

/**
 * Buy Page
 *
 * Created by palmer on 15/7/6.
 */
public class SecurityOptMockSubBuyFragment extends Fragment{

    private Button buySellBtn;
    private ListView positionsLV;

    private SecurityOptMockPositionAdapter securityOptMockPositionAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        securityOptMockPositionAdapter = new SecurityOptMockPositionAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_opt_sub_buysell, container, false);
        initViews(view);

        return view;
    }


    private void initViews(View view) {
        buySellBtn = (Button) view.findViewById(R.id.button_security_opt_buy_sell);
        buySellBtn.setText(R.string.security_opt_buy);
        positionsLV = (ListView) view.findViewById(R.id.listview_security_opt_positions);
        if(securityOptMockPositionAdapter==null){
            securityOptMockPositionAdapter = new SecurityOptMockPositionAdapter(getActivity());
        }
        positionsLV.setAdapter(securityOptMockPositionAdapter);
    }
}
