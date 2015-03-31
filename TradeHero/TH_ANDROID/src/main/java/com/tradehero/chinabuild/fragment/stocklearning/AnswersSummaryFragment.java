package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by palmer on 15/3/31.
 */
public class AnswersSummaryFragment extends DashboardFragment {

    private TextView summaryDescTV;

    //Resources
    private String descriptionSummaryA;
    private String descriptionSummaryB;
    private int descriptionSummaryColorA;
    private int descriptionSummaryColorB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSummaryDescriptionResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_summary, container, false);
        summaryDescTV = (TextView)view.findViewById(R.id.textview_answers_summary_desc);

        return view;
    }

    private void initSummaryDescriptionResources(){
        descriptionSummaryA = getActivity().getResources().getString(R.string.stock_learning_summary_a);
        descriptionSummaryB = getActivity().getResources().getString(R.string.stock_learning_summary_b);
        descriptionSummaryColorA = getActivity().getResources().getColor(R.color.stock_learning_summary_desc_color_a);
        descriptionSummaryColorB = getActivity().getResources().getColor(R.color.stock_learning_summary_desc_color_b);
    }

    private void refreshSummary(int totalNumber, int wrongNumber){

    }


}
