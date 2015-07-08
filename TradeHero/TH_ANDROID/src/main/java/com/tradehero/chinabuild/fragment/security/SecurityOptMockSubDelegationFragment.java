package com.tradehero.chinabuild.fragment.security;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;

public class SecurityOptMockSubDelegationFragment extends Fragment implements View.OnClickListener{
    private RelativeLayout mDelegrationButton;
    private ListView mListView;
    private SecurityOptMockDelegationAdapter mListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_opt_sub_delegation, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mDelegrationButton = (RelativeLayout) view.findViewById(R.id.delegration_button);
        mDelegrationButton.setOnClickListener(this);
        mListView = (ListView) view.findViewById(R.id.listview_security_opt_positions);
        if (mListViewAdapter == null) {
            mListViewAdapter = new SecurityOptMockDelegationAdapter(getActivity());
        }
        mListView.setAdapter(mListViewAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delegration_button:
                THToast.show("click delegration_button");
                break;
        }
    }

    @Override
    public void onDestroyView() {
        mDelegrationButton = null;
        mListViewAdapter = null;
        mListView = null;
        super.onDestroyView();
    }
}
