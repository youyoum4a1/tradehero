package com.tradehero.chinabuild.fragment.security;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tradehero.th.R;

/**
 * Sell Page
 *
 * Created by palmer on 15/7/6.
 */
public class SecurityOptMockSubSellFragment extends Fragment {

    private Button buySellBtn;
    private ListView positionsLV;

    //Dialog
    private Dialog sellConfirmDialog;
    private TextView dlgStockNameTV;
    private TextView dlgStockCodeTV;
    private TextView dlgStockPriceTV;
    private TextView dlgStockAmountTV;
    private TextView dlgStockTotalTV;
    private TextView dlgConfirmTV;
    private TextView dlgCancelTV;

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
        buySellBtn.setText(R.string.security_opt_sell);
        buySellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSellConfirmDialog();
            }
        });
        positionsLV = (ListView) view.findViewById(R.id.listview_security_opt_positions);
        if(securityOptMockPositionAdapter==null){
            securityOptMockPositionAdapter = new SecurityOptMockPositionAdapter(getActivity());
        }
        positionsLV.setAdapter(securityOptMockPositionAdapter);
    }

    private void showSellConfirmDialog(){
        if(getActivity() == null){
            return;
        }
        if(sellConfirmDialog == null){
            sellConfirmDialog = new Dialog(getActivity());
            sellConfirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            sellConfirmDialog.setCanceledOnTouchOutside(false);
            sellConfirmDialog.setCancelable(true);
            sellConfirmDialog.setContentView(R.layout.dialog_security_opt_sell);


            dlgCancelTV = (TextView)sellConfirmDialog.findViewById(R.id.dialog_cancel);
            dlgCancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(sellConfirmDialog!=null){
                        sellConfirmDialog.dismiss();
                    }
                }
            });

        }
        sellConfirmDialog.show();
    }
}