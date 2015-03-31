package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by palmer on 15/3/31.
 */
public class StockLearningHistoryFragment extends DashboardFragment{

    private ListView historyLV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_history, container, false);

        historyLV = (ListView)view.findViewById(R.id.listview_answers_history);
        return view;
    }

}
