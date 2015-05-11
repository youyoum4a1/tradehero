package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.chinabuild.data.db.StockLearningDatabaseHelper;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;

import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by palmer on 15/4/24.
 */
public class OneQuestionFragment extends Fragment implements View.OnClickListener {

    @InjectView(R.id.textview_question_question) TextView questionTitleTV;
    @InjectView(R.id.linearlayout_question_choice_a) LinearLayout aLL;
    @InjectView(R.id.imageview_choice_a) ImageView aIV;
    @InjectView(R.id.textview_choice_a) TextView aTV;
    @InjectView(R.id.linearlayout_question_choice_b) LinearLayout bLL;
    @InjectView(R.id.imageview_choice_b) ImageView bIV;
    @InjectView(R.id.textview_choice_b) TextView bTV;
    @InjectView(R.id.linearlayout_question_choice_c) LinearLayout cLL;
    @InjectView(R.id.imageview_choice_c) ImageView cIV;
    @InjectView(R.id.textview_choice_c) TextView cTV;
    @InjectView(R.id.linearlayout_question_choice_d) LinearLayout dLL;
    @InjectView(R.id.imageview_choice_d) ImageView dIV;
    @InjectView(R.id.textview_choice_d) TextView dTV;

    @InjectView(R.id.button_answer_question) Button answerBtn;

    private Question question;
    private QuestionStatusRecord questionStatusRecord;
    private QuestionGroup questionGroup;
    private int user_id = -1;
    private boolean isFinalQuestion = false;
    private int choiceType;
    private int index;

    public static String KEY_ONE_QUESTION = "key_one_question";
    public static String KEY_USER_ID = "key_user_id";
    public static String KEY_FINAL_QUESTION = "key_final_question";
    public static String KEY_QUESTION_GROUP = "key_question_group";
    public static String KEY_QUESTION_INDEX = "key_question_index";

    private boolean isASelected = false;
    private boolean isBSelected = false;
    private boolean isCSelected = false;
    private boolean isDSelected = false;

    private String type = "";

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }
        view = inflater.inflate(R.layout.stock_learning_to_answer, container, false);
        ButterKnife.inject(this, view);
        refreshView();
        aLL.setOnClickListener(this);
        bLL.setOnClickListener(this);
        cLL.setOnClickListener(this);
        dLL.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAnswerViews();
    }


    private void refreshView() {
        if (question == null) {
            aLL.setVisibility(View.GONE);
            bLL.setVisibility(View.GONE);
            cLL.setVisibility(View.GONE);
            dLL.setVisibility(View.GONE);
        }

        aLL.setClickable(false);
        bLL.setClickable(false);
        cLL.setClickable(false);
        dLL.setClickable(false);

        questionTitleTV.setText(question.content);
        aTV.setText(question.option1);
        bTV.setText(question.option2);
        choiceType = question.getChoiceType();
        if (choiceType == Question.MULTICHOISE) {
            cLL.setVisibility(View.VISIBLE);
            dLL.setVisibility(View.VISIBLE);
            cTV.setText(question.option3);
            dTV.setText(question.option4);
        } else if (choiceType == Question.JUDGECHOISE) {
            cLL.setVisibility(View.GONE);
            dLL.setVisibility(View.GONE);
        } else if (choiceType == Question.ONECHOICE) {
            cLL.setVisibility(View.VISIBLE);
            dLL.setVisibility(View.VISIBLE);
            cTV.setText(question.option3);
            dTV.setText(question.option4);
        }
        answerBtn.setVisibility(View.GONE);
        answerBtn.setText(R.string.question_status_show_answer);
    }

    private void initArguments() {
        Bundle bundle = getArguments();
        user_id = bundle.getInt(KEY_USER_ID);
        question = (Question) bundle.getSerializable(KEY_ONE_QUESTION);
        isFinalQuestion = bundle.getBoolean(KEY_FINAL_QUESTION, false);
        questionGroup = (QuestionGroup) bundle.getSerializable(KEY_QUESTION_GROUP);
        index = bundle.getInt(KEY_QUESTION_INDEX, 1);
        type = bundle.getString(AnswerQuestionFragment.KEY_QUESTION_GROUP_TYPE, "");
        if (type.equals("")) {
            getActivity().finish();
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.linearlayout_question_choice_a:
                selectOneChoice(1);
                break;
            case R.id.linearlayout_question_choice_b:
                selectOneChoice(2);
                break;
            case R.id.linearlayout_question_choice_c:
                selectOneChoice(3);
                break;
            case R.id.linearlayout_question_choice_d:
                selectOneChoice(4);
                break;
            case R.id.button_answer_question:
                submitAnswer();
                break;
        }
    }

    private void submitAnswer() {
        String answer = "";
        if (isASelected) {
            answer = answer + "a";
        }
        if (isBSelected) {
            answer = answer + "b";
        }
        if (isCSelected) {
            answer = answer + "c";
        }
        if (isDSelected) {
            answer = answer + "d";
        }
        if (answer.equals("")) {
            return;
        }
        StockLearningDatabaseHelper stockLearningDatabaseHelper = new StockLearningDatabaseHelper(getActivity());
        QuestionStatusRecord questionStatusRecord = new QuestionStatusRecord();
        questionStatusRecord.question_choice = answer;
        questionStatusRecord.question_id = question.id;
        questionStatusRecord.question_group_id = question.subcategory;
        questionStatusRecord.user_id = user_id;
        stockLearningDatabaseHelper.insertQuestionRecord(questionStatusRecord);
        if (type.equals(AnswerQuestionFragment.TYPE_NORMAL)) {
            if (questionGroup != null) {
                questionGroup.question_group_progress = index;
                stockLearningDatabaseHelper.insertOrUpdateQuestionGroup(questionGroup, user_id);
            }
        }
        if (type.equals(AnswerQuestionFragment.TYPE_ERROR)) {
            StockLearningQuestionManager.getInstance().removeReAnswerQuestion(question.id);
        }
        refreshAnswerViews();
        if (isFinalQuestion) {
            jumpToSummaryFragment();
        }
    }

    private void selectOneChoice(int index) {
        if (choiceType == Question.JUDGECHOISE || choiceType == Question.ONECHOICE) {
            switch (index) {
                case 1:
                    if (isASelected) {
                        isASelected = false;
                    } else {
                        isASelected = true;
                        isBSelected = false;
                        isCSelected = false;
                        isDSelected = false;
                    }
                    break;
                case 2:
                    if (isBSelected) {
                        isBSelected = false;
                    } else {
                        isASelected = false;
                        isBSelected = true;
                        isCSelected = false;
                        isDSelected = false;
                    }
                    break;
                case 3:
                    if (isCSelected) {
                        isCSelected = false;
                    } else {
                        isASelected = false;
                        isBSelected = false;
                        isCSelected = true;
                        isDSelected = false;
                    }
                    break;
                case 4:
                    if (isDSelected) {
                        isDSelected = false;
                    } else {
                        isASelected = false;
                        isBSelected = false;
                        isCSelected = false;
                        isDSelected = true;
                    }
                    break;
            }
        } else if (choiceType == Question.MULTICHOISE) {
            switch (index) {
                case 1:
                    if (isASelected) {
                        isASelected = false;
                    } else {
                        isASelected = true;
                    }
                    break;
                case 2:
                    if (isBSelected) {
                        isBSelected = false;
                    } else {
                        isBSelected = true;
                    }
                    break;
                case 3:
                    if (isCSelected) {
                        isCSelected = false;
                    } else {
                        isCSelected = true;
                    }
                    break;
                case 4:
                    if (isDSelected) {
                        isDSelected = false;
                    } else {
                        isDSelected = true;
                    }
                    break;
            }
        }
        if (isASelected) {
            aIV.setBackgroundResource(R.drawable.question_item_selected);
        } else {
            aIV.setBackgroundResource(R.drawable.question_item_default_choice_tag);
        }
        if (isBSelected) {
            bIV.setBackgroundResource(R.drawable.question_item_selected);
        } else {
            bIV.setBackgroundResource(R.drawable.question_item_default_choice_tag);
        }
        if (isCSelected) {
            cIV.setBackgroundResource(R.drawable.question_item_selected);
        } else {
            cIV.setBackgroundResource(R.drawable.question_item_default_choice_tag);
        }
        if (isDSelected) {
            dIV.setBackgroundResource(R.drawable.question_item_selected);
        } else {
            dIV.setBackgroundResource(R.drawable.question_item_default_choice_tag);
        }
        refreshAnswerBtn();
    }

    private void refreshAnswerBtn() {
        if (isASelected || isBSelected || isCSelected || isDSelected) {
            answerBtn.setEnabled(true);

        } else {
            answerBtn.setEnabled(false);
        }
    }

    private void refreshAnswerViews() {
        if (question != null) {
            StockLearningDatabaseHelper stockLearningDatabaseHelper = new StockLearningDatabaseHelper(getActivity());
            questionStatusRecord = stockLearningDatabaseHelper.retrieveQuestionRecord(question.id, user_id, question.subcategory);
            if (type.equals(AnswerQuestionFragment.TYPE_ERROR)) {
                if (StockLearningQuestionManager.getInstance().isNeedReAnswer(question.id)) {
                    aLL.setClickable(true);
                    bLL.setClickable(true);
                    cLL.setClickable(true);
                    dLL.setClickable(true);
                    answerBtn.setVisibility(View.VISIBLE);
                    answerBtn.setEnabled(true);
                } else {
                    if (questionStatusRecord == null && getActivity() != null) {
                        getActivity().finish();
                    } else {
                        displayResult();
                    }
                }
                return;
            }
            if (questionStatusRecord == null && !type.equals(AnswerQuestionFragment.TYPE_ONLY_ONE)) {
                aLL.setClickable(true);
                bLL.setClickable(true);
                cLL.setClickable(true);
                dLL.setClickable(true);
                answerBtn.setVisibility(View.VISIBLE);
                answerBtn.setEnabled(false);
            } else {
                displayResult();
            }
        }
    }

    private void displayResult() {
        aLL.setClickable(false);
        bLL.setClickable(false);
        cLL.setClickable(false);
        dLL.setClickable(false);
        answerBtn.setVisibility(View.GONE);
        String userAnswer = "";
        if(questionStatusRecord==null){
            userAnswer = "";
        }else {
            userAnswer = questionStatusRecord.question_choice.toLowerCase();
        }
        String rightAnswer = question.answer.toLowerCase();
        if (userAnswer.contains("a")) {
            aIV.setBackgroundResource(R.drawable.question_item_wrong);
        }
        if (userAnswer.contains("b")) {
            bIV.setBackgroundResource(R.drawable.question_item_wrong);
        }
        if (userAnswer.contains("c")) {
            cIV.setBackgroundResource(R.drawable.question_item_wrong);
        }
        if (userAnswer.contains("d")) {
            dIV.setBackgroundResource(R.drawable.question_item_wrong);
        }
        if (rightAnswer.contains("a")) {
            aIV.setBackgroundResource(R.drawable.question_item_right);
        }
        if (rightAnswer.contains("b")) {
            bIV.setBackgroundResource(R.drawable.question_item_right);
        }
        if (rightAnswer.contains("c")) {
            cIV.setBackgroundResource(R.drawable.question_item_right);
        }
        if (rightAnswer.contains("d")) {
            dIV.setBackgroundResource(R.drawable.question_item_right);
        }
    }

    private void jumpToSummaryFragment() {
        Bundle bundle = new Bundle();
        if (getActivity() != null) {
            getActivity().finish();
        }
        bundle.putSerializable(AnswersSummaryFragment.KEY_QUESTION_GROUP, questionGroup);
        gotoDashboard(AnswersSummaryFragment.class.getName(), bundle);
    }

    public void gotoDashboard(String strFragment, Bundle bundle) {
        Bundle args = new Bundle();
        args.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, strFragment);
        args.putAll(bundle);
        ActivityHelper.launchDashboard(this.getActivity(), args);
    }

}
