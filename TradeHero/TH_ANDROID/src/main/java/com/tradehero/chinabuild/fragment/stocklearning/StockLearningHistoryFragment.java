package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import java.util.ArrayList;

/**
 * Created by palmer on 15/3/31.
 */
public class StockLearningHistoryFragment extends DashboardFragment
{
    private ListView historyLV;
    private StockLearningAnswersHistoryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArgument();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getString(R.string.stock_learning_review_history_menu));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_history, container, false);
        refreshHistoryData();
        historyLV = (ListView) view.findViewById(R.id.listview_answers_history);
        historyLV.setAdapter(adapter);
        historyLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long i)
            {
                gotoHistory(adapter.getItem(position));
            }
        });

        return view;
    }

    public void initArgument() {
    }

    //返回历史题数据，包含错误标记
    public ArrayList<Question> getHistoryQuestionList() {
        return null;
    }

    //返回错误答题数据，不做错误标记
    public ArrayList<Question> getErrorQuestionList() {
        return null;
    }

    //Test Data
    private void refreshHistoryData() {
        if (adapter == null)
        {
            adapter = new StockLearningAnswersHistoryAdapter(getActivity());
            adapter.setQuestionItems(getHistoryQuestionList());
        }
    }

    private void gotoHistory(Question question) {
    }
}
