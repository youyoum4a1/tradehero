package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by palmer on 15/3/30.
 */
public class ToAnswerQuestionFragment extends DashboardFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_to_answer, container, false);
        return view;
    }

}
