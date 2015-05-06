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

/**
 * Created by palmer on 15/4/24.
 */
public class OneQuestionFragment extends Fragment implements View.OnClickListener {

    private Question question;
    private QuestionStatusRecord questionStatusRecord;
    private int user_id = -1;
    private boolean isOnlyResult = false;
    private boolean isReAnswerCompleted = false;

    @InjectView(R.id.textview_question_question)
    TextView questionTitleTV;
    @InjectView(R.id.linearlayout_question_choice_a)
    LinearLayout aLL;
    @InjectView(R.id.imageview_choice_a)
    ImageView aIV;
    @InjectView(R.id.textview_choice_a)
    TextView aTV;
    @InjectView(R.id.linearlayout_question_choice_b)
    LinearLayout bLL;
    @InjectView(R.id.imageview_choice_b)
    ImageView bIV;
    @InjectView(R.id.textview_choice_b)
    TextView bTV;
    @InjectView(R.id.linearlayout_question_choice_c)
    LinearLayout cLL;
    @InjectView(R.id.imageview_choice_c)
    ImageView cIV;
    @InjectView(R.id.textview_choice_c)
    TextView cTV;
    @InjectView(R.id.linearlayout_question_choice_d)
    LinearLayout dLL;
    @InjectView(R.id.imageview_choice_d)
    ImageView dIV;
    @InjectView(R.id.textview_choice_d)
    TextView dTV;

    @InjectView(R.id.button_answer_question)
    Button answerBtn;


    public static String KEY_ONE_QUESTION = "key_one_question";
    public static String KEY_ONLY_RESULT = "key_only_result";
    public static String KEY_USER_ID = "key_user_id";

    private String checkAnswerStr;
    private String submitAnswerStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments();
        checkAnswerStr = getActivity().getResources().getString(R.string.question_status_show_answer);
        submitAnswerStr = getActivity().getResources().getString(R.string.question_status_complete);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_to_answer, container, false);
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
        int choiceType = question.getChoiceType();
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
    }

    private void initArguments() {
        Bundle bundle = getArguments();
        user_id = bundle.getInt(KEY_USER_ID);
        question = (Question) bundle.getSerializable(KEY_ONE_QUESTION);
        isOnlyResult = bundle.getBoolean(KEY_ONLY_RESULT, false);
    }

    @Override
    public void onClick(View view) {
    }

    private void refreshAnswerViews() {
        if (question != null) {
            StockLearningDatabaseHelper stockLearningDatabaseHelper = new StockLearningDatabaseHelper(getActivity());
            questionStatusRecord = stockLearningDatabaseHelper.retrieveQuestionRecord(question.id, user_id, question.subcategory);
            if (questionStatusRecord == null) {
                aLL.setClickable(true);
                bLL.setClickable(true);
                cLL.setClickable(true);
                dLL.setClickable(true);
            } else {
                aLL.setClickable(false);
                bLL.setClickable(false);
                cLL.setClickable(false);
                dLL.setClickable(false);
                String userAnswer = questionStatusRecord.question_choice;
                String rightAnswer = question.answer;

            }
        }
    }
}
