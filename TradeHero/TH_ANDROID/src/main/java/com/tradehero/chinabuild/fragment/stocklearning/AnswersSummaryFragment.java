package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.tradehero.chinabuild.data.db.StockLearningDatabaseHelper;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Created by palmer on 15/3/31.
 */
public class AnswersSummaryFragment extends DashboardFragment implements View.OnClickListener {

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

    private QuestionGroup questionGroup;
    private ArrayList<QuestionStatusRecord> questionStatusRecords = new ArrayList();
    private ArrayList<Question> reAnswerQuestions = new ArrayList();
    private ArrayList<Question> questions = new ArrayList();

    public final static String KEY_QUESTION_GROUP = "key_question_group";

    @Inject CurrentUserId currentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArgument();
        initSummaryDescriptionResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_summary, container, false);
        summaryDescTV = (TextView) view.findViewById(R.id.textview_answers_summary_desc);
        resultIV = (ImageView) view.findViewById(R.id.imageview_stocklearning_result);
        historyBtn = (Button) view.findViewById(R.id.button_history);
        historyBtn.setOnClickListener(this);
        errorsBtn = (Button) view.findViewById(R.id.button_errors);
        errorsBtn.setOnClickListener(this);
        errorsBtn.setEnabled(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshSummaryHandler refreshSummaryHandler = new RefreshSummaryHandler();
        refreshSummaryHandler.sendEmptyMessageDelayed(-1, 200);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.question_summary);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.button_errors:
                gotoFails();
                break;
            case R.id.button_history:
                gotoHistory();
                break;
        }
    }

    private void initArgument() {
        Bundle bundle = getArguments();
        questionGroup = (QuestionGroup) bundle.getSerializable(KEY_QUESTION_GROUP);
        if (questionGroup == null) {
            popCurrentFragment();
        } else {
            StockLearningDatabaseHelper stockLearningDatabaseHelper = new StockLearningDatabaseHelper(getActivity());
            questions = stockLearningDatabaseHelper.retrieveQuestions(questionGroup.id);
        }
    }

    private void gotoFails() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(AnswerQuestionFragment.KEY_QUESTION_GROUP, questionGroup);
        bundle.putString(AnswerQuestionFragment.KEY_QUESTION_GROUP_TYPE, AnswerQuestionFragment.TYPE_ERROR);
        pushFragment(AnswerQuestionFragment.class, bundle);
    }

    private void gotoHistory() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(AnswerQuestionFragment.KEY_QUESTION_GROUP, questionGroup);
        pushFragment(StockLearningHistoryFragment.class, bundle);
    }

    private void initSummaryDescriptionResources() {
        descriptionSummaryA = getActivity().getResources().getString(R.string.stock_learning_summary_a);
        descriptionSummaryB = getActivity().getResources().getString(R.string.stock_learning_summary_b);
        descriptionSummaryColorA = getActivity().getResources().getColor(R.color.stock_learning_summary_success_color);
        descriptionSummaryColorB = getActivity().getResources().getColor(R.color.stock_learning_summary_failed_color);
        descSizeA = (int) getActivity().getResources().getDimension(R.dimen.stock_learning_summary_des_size_a);
        descSizeB = (int) getActivity().getResources().getDimension(R.dimen.stock_learning_summary_des_size_b);
    }

    private void refreshSummary(int totalNumber, int failedNum) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        String descAStr = String.format(descriptionSummaryA, totalNumber);
        SpannableString descA = new SpannableString(descAStr);
        descA.setSpan(new ForegroundColorSpan(descriptionSummaryColorA), 0, descAStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        descA.setSpan(new AbsoluteSizeSpan(descSizeA), 0, descAStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String descBStr = String.valueOf(failedNum);
        SpannableString descB = new SpannableString(descBStr);
        descB.setSpan(new ForegroundColorSpan(descriptionSummaryColorB), 0, descBStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        descB.setSpan(new AbsoluteSizeSpan(descSizeB), 0, descBStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String descCStr = descriptionSummaryB;
        SpannableString descC = new SpannableString(descCStr);
        descC.setSpan(new ForegroundColorSpan(descriptionSummaryColorA), 0, descCStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        descC.setSpan(new AbsoluteSizeSpan(descSizeA), 0, descCStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.append(descA).append(descB).append(descC);
        summaryDescTV.setText(spannableStringBuilder);

        if (failedNum > 0) {
            resultIV.setBackgroundResource(R.drawable.stock_learning_question_result_failed);
            errorsBtn.setVisibility(View.VISIBLE);
            errorsBtn.setEnabled(true);
        } else {
            resultIV.setBackgroundResource(R.drawable.stock_learning_question_result_success);
            errorsBtn.setVisibility(View.GONE);
        }
    }

    public class RefreshSummaryHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (getActivity() == null) {
                return;
            }
            StockLearningDatabaseHelper stockLearningDatabaseHelper = new StockLearningDatabaseHelper(getActivity());
            questionStatusRecords = stockLearningDatabaseHelper.retrieveQuestionRecords(currentUserId.get(), questionGroup.id);
            reAnswerQuestions.clear();
            for (Question question : questions) {
                boolean isAnswered = false;
                for (QuestionStatusRecord questionStatusRecord : questionStatusRecords) {
                    if (questionStatusRecord.question_id == question.id) {
                        isAnswered = true;
                        if (!questionStatusRecord.question_choice.toLowerCase().equals(question.answer.toLowerCase())) {
                            reAnswerQuestions.add(question);
                            break;
                        }
                    }
                }
                if (!isAnswered) {
                    reAnswerQuestions.add(question);
                }
            }
            refreshSummary(questions.size(), reAnswerQuestions.size());
            StockLearningQuestionManager.getInstance().clearReAnswerQuestions();
            StockLearningQuestionManager.getInstance().setReAnswerQuestions(reAnswerQuestions);
        }
    }
}
