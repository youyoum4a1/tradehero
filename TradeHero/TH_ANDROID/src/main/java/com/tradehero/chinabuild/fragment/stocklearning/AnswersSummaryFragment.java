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
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by palmer on 15/3/31.
 */
public class AnswersSummaryFragment extends DashboardFragment implements View.OnClickListener
{

    private TextView summaryDescTV;
    private Button historyBtn;
    private Button errorsBtn;
    private ImageView resultIV;

    //Resources
    private String descriptionSummaryA;
    private String descriptionSummaryB;
    private int descriptionSummaryColorA;
    private int descriptionSummaryColorB;
    private int descSizeA;
    private int descSizeB;

    private int totalNum = 80;
    private int failedNum = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initArgument();
        initSummaryDescriptionResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.stock_learning_summary, container, false);
        summaryDescTV = (TextView) view.findViewById(R.id.textview_answers_summary_desc);
        resultIV = (ImageView) view.findViewById(R.id.imageview_stocklearning_result);
        historyBtn = (Button) view.findViewById(R.id.button_history);
        historyBtn.setOnClickListener(this);
        errorsBtn = (Button) view.findViewById(R.id.button_errors);
        errorsBtn.setOnClickListener(this);
        refreshSummary(totalNum, failedNum);
        return view;
    }

    private void initArgument()
    {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.question_summary);
    }

    @Override
    public void onClick(View view)
    {
        int viewId = view.getId();
        switch (viewId)
        {
            case R.id.button_errors:
                gotoFails();
                break;
            case R.id.button_history:
                gotoHistory();
                break;
        }
    }

    private void gotoFails()
    {
    }

    private void gotoHistory()
    {
    }

    private void initSummaryDescriptionResources()
    {
        descriptionSummaryA = getActivity().getResources().getString(R.string.stock_learning_summary_a);
        descriptionSummaryB = getActivity().getResources().getString(R.string.stock_learning_summary_b);
        descriptionSummaryColorA = getActivity().getResources().getColor(R.color.stock_learning_summary_success_color);
        descriptionSummaryColorB = getActivity().getResources().getColor(R.color.stock_learning_summary_failed_color);
        descSizeA = (int) getActivity().getResources().getDimension(R.dimen.stock_learning_summary_des_size_a);
        descSizeB = (int) getActivity().getResources().getDimension(R.dimen.stock_learning_summary_des_size_b);
    }

    private void refreshSummary(int totalNumber, int wrongNumber)
    {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        String descAStr = String.format(descriptionSummaryA, totalNumber);
        SpannableString descA = new SpannableString(descAStr);
        descA.setSpan(new ForegroundColorSpan(descriptionSummaryColorA), 0, descAStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        descA.setSpan(new AbsoluteSizeSpan(descSizeA), 0, descAStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String descBStr = String.valueOf(wrongNumber);
        SpannableString descB = new SpannableString(descBStr);
        descB.setSpan(new ForegroundColorSpan(descriptionSummaryColorB), 0, descBStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        descB.setSpan(new AbsoluteSizeSpan(descSizeB), 0, descBStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String descCStr = descriptionSummaryB;
        SpannableString descC = new SpannableString(descCStr);
        descC.setSpan(new ForegroundColorSpan(descriptionSummaryColorA), 0, descCStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        descC.setSpan(new AbsoluteSizeSpan(descSizeA), 0, descCStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.append(descA).append(descB).append(descC);
        summaryDescTV.setText(spannableStringBuilder);

        if (failedNum > 0)
        {
            resultIV.setBackgroundResource(R.drawable.stock_learning_question_result_failed);
            errorsBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            resultIV.setBackgroundResource(R.drawable.stock_learning_question_result_success);
            errorsBtn.setVisibility(View.GONE);
        }
    }
}
