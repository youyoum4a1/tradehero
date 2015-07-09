package com.tradehero.chinabuild.fragment.security;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.tradehero.th.R;

/**
 * Created by palmer on 15/7/6.
 */
public class SecurityOptMockSubQueryFragment extends Fragment implements View.OnClickListener{
    private TextView mClickShowMore;
    private ListView mListView1;
    private ListView mListView2;
    private SecurityOptMockQueryTradeAdapter mListViewAdapter1;
    private SecurityOptMockQueryDelegationAdapter mListViewAdapter2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_opt_sub_query, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mClickShowMore = (TextView) view.findViewById(R.id.click_show_more);
        mClickShowMore.setOnClickListener(this);
        mListView1 = (ListView) view.findViewById(R.id.list_1);
        mListView2 = (ListView) view.findViewById(R.id.list_2);
        if (mListViewAdapter1 == null) {
            mListViewAdapter1 = new SecurityOptMockQueryTradeAdapter(getActivity());
        }
        mListView1.setAdapter(mListViewAdapter1);
        if (mListViewAdapter2 == null) {
            mListViewAdapter2 = new SecurityOptMockQueryDelegationAdapter(getActivity());
        }
        mListView2.setAdapter(mListViewAdapter2);
    }

    @Override
    public void onClick(View v) {

    }
}