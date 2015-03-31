package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by palmer on 15/3/31.
 */
public class AnswersSummaryFragment extends DashboardFragment implements View.OnClickListener{

    private TextView summaryDescTV;
    private Button historyBtn;
    private Button errorsBtn;

    //Resources
    private String descriptionSummaryA;
    private String descriptionSummaryB;
    private int descriptionSummaryColorA;
    private int descriptionSummaryColorB;
    private int descSizeA;
    private int descSizeB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSummaryDescriptionResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_summary, container, false);
        summaryDescTV = (TextView)view.findViewById(R.id.textview_answers_summary_desc);
        historyBtn = (Button)view.findViewById(R.id.button_history);
        errorsBtn = (Button)view.findViewById(R.id.button_errors);
        refreshSummary(80,6);
        return view;
    }

    private void initSummaryDescriptionResources(){
        descriptionSummaryA = getActivity().getResources().getString(R.string.stock_learning_summary_a);
        descriptionSummaryB = getActivity().getResources().getString(R.string.stock_learning_summary_b);
        descriptionSummaryColorA = getActivity().getResources().getColor(R.color.stock_learning_summary_desc_color_a);
        descriptionSummaryColorB = getActivity().getResources().getColor(R.color.stock_learning_summary_desc_color_b);
        descSizeA = (int)getActivity().getResources().getDimension(R.dimen.stock_learning_summary_des_size_a);
        descSizeB = (int)getActivity().getResources().getDimension(R.dimen.stock_learning_summary_des_size_b);
    }

    private void refreshSummary(int totalNumber, int wrongNumber){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        String descAStr = String.format(descriptionSummaryA, totalNumber);
        SpannableString descA = new SpannableString(descAStr);
        descA.setSpan(new ForegroundColorSpan(descriptionSummaryColorA), 0, descAStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        descA.setSpan(new AbsoluteSizeSpan(descSizeA), 0, descAStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

        String descBStr = String.valueOf(wrongNumber);
        SpannableString descB = new SpannableString(descBStr);
        descB.setSpan(new ForegroundColorSpan(descriptionSummaryColorB), 0, descBStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        descB.setSpan(new AbsoluteSizeSpan(descSizeB), 0, descBStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

        String descCStr = descriptionSummaryB;
        SpannableString descC = new SpannableString(descCStr);
        descC.setSpan(new ForegroundColorSpan(descriptionSummaryColorA), 0, descCStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        descC.setSpan(new AbsoluteSizeSpan(descSizeA), 0, descCStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

        spannableStringBuilder.append(descA).append(descB).append(descC);
        summaryDescTV.setText(spannableStringBuilder);
    }


    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.button_errors:
                break;
            case R.id.button_history:
                gotoHistory();
                break;
        }
    }

    private void gotoHistory(){
        Bundle bundle = new Bundle();
        pushFragment(StockLearningHistoryFragment.class, bundle);
    }

}
