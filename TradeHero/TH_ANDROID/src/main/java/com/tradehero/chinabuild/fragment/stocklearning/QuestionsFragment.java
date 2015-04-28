package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.metrics.Analytics;

import javax.inject.Inject;

/**
 * Stock Questions
 *
 * Created by palmer on 15/3/27.
 */
public class QuestionsFragment extends DashboardFragment {


    private ListView questionsLV;

    @Inject Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_questions, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override public void onResume() {
        super.onResume();
    }

}
