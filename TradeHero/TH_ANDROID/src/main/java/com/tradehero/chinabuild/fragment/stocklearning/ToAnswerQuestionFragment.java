package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.chinabuild.fragment.stocklearning.question.questionUtils.Question;
import com.tradehero.chinabuild.fragment.stocklearning.question.questionUtils.QuestionLoader;
import com.tradehero.chinabuild.data.sp.QuestionsSharePreferenceManager;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Created by palmer on 15/3/30.
 */
public class ToAnswerQuestionFragment extends DashboardFragment implements View.OnClickListener
{

    @InjectView(R.id.button_next_question) Button nextQuestionBtn;
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

    private String type = "";
    public final static String KEY_QUESTION_SET_TYPE = "key_question_set_type";
    public final static String TYPE_QUESTION_SET_NORMAL = "type_question_set_normal";//正常题库答题
    public final static String TYPE_QUESTION_SET_FAILED = "type_question_set_failed";//错题库
    public final static String TYPE_QUESTION_SET_ONLY_RESULT = "type_question_set_only_result";//查看历史单道题
    public final static String KEY_QUESTION = "key_question";

    public final static String KEY_QUESTION_SET_LEVEL = "key_question_set_level";//第几套题 LEVEL1,2,3
    public final static String KEY_QUESTION_CURRENT_ID = "key_question_current_id";//第几道题开始
    public ArrayList<Question> arrayListQuestion = null;//LEVEL题集

    private String currentQuestionLevel = QuestionLoader.LEVEL_ONE;
    private int currentQuestionIndex = -1;
    private Question currentQuestion = null;

    public static final int STATUS_SHOW_ANSWER = 0;
    public static final int STATUS_NEXT_QUESTION = 1;
    public static final int STATUS_COMPLETE = 2;

    private int currentStatus = STATUS_SHOW_ANSWER;

    public boolean[] answers = {false, false, false, false};
    private String strTitle = "";

    @Inject Analytics analytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_to_answer, container, false);
        ButterKnife.inject(this, view);
        getBundleParameters();
        nextQuestionBtn.setOnClickListener(this);
        aLL.setOnClickListener(this);
        bLL.setOnClickListener(this);
        cLL.setOnClickListener(this);
        dLL.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view)
    {
        int viewId = view.getId();
        switch (viewId)
        {
            case R.id.button_next_question:
                doNextButton();
                break;

            case R.id.linearlayout_question_choice_a:
                setCheck(0);
                break;
            case R.id.linearlayout_question_choice_b:
                setCheck(1);
                break;
            case R.id.linearlayout_question_choice_c:
                setCheck(2);
                break;
            case R.id.linearlayout_question_choice_d:
                setCheck(3);
                break;
        }
    }

    private void refreshNextButtonBg()
    {
        nextQuestionBtn.setEnabled(isAnswerSelected());
    }

    private void setCheck(int index)
    {
        if (type == TYPE_QUESTION_SET_ONLY_RESULT) return;//历史回顾过程 不可点击按键
        if (currentStatus != STATUS_SHOW_ANSWER) return;//在显示答案之前 选项按键是有效的

        if (currentQuestion.getChoiceType() == Question.ONECHOICE || currentQuestion.getChoiceType() == Question.JUDGECHOISE)
        {
            for (int i = 0; i < answers.length; i++)
            {
                if (index == i)
                {
                    answers[index] = !answers[index];
                }
                else
                {
                    answers[i] = false;
                }
            }
        }
        else if (currentQuestion.getChoiceType() == Question.MULTICHOISE)
        {
            answers[index] = !answers[index];
        }

        setImageViewAnswersSelected();

        refreshNextButtonBg();
    }

    private void clearImageViewAnswers()
    {
        answers[0] = answers[1] = answers[2] = answers[3] = false;
        setImageViewAnswersSelected();
        refreshNextButtonBg();
    }

    //显示选中答案
    private void setImageViewAnswersSelected()
    {
        aIV.setBackgroundResource(answers[0] ? R.drawable.question_item_selected : R.drawable.question_item_default_choice_tag);
        bIV.setBackgroundResource(answers[1] ? R.drawable.question_item_selected : R.drawable.question_item_default_choice_tag);
        cIV.setBackgroundResource(answers[2] ? R.drawable.question_item_selected : R.drawable.question_item_default_choice_tag);
        dIV.setBackgroundResource(answers[3] ? R.drawable.question_item_selected : R.drawable.question_item_default_choice_tag);
        aTV.setTextColor(answers[0] ? 0xFF1789d5 : 0xFF000000);
        bTV.setTextColor(answers[1] ? 0xFF1789d5 : 0xFF000000);
        cTV.setTextColor(answers[2] ? 0xFF1789d5 : 0xFF000000);
        dTV.setTextColor(answers[3] ? 0xFF1789d5 : 0xFF000000);
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(strTitle);
    }

    private void doNextButton()
    {
        switch (currentStatus)
        {
            case STATUS_SHOW_ANSWER:
                if (isAnswerSelected())
                {
                    setButtonStatus(STATUS_NEXT_QUESTION);
                    calcAnswer();
                    showCorrectAnswer();
                }
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,AnalyticsConstants.QUESTION_CHECK_QUESTION_RESULT));
                break;
            case STATUS_NEXT_QUESTION:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,AnalyticsConstants.QUESTION_NEXT_QUESTION));
                getNextQuestion();
                break;
            case STATUS_COMPLETE:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,AnalyticsConstants.QUESTION_COMPLETED));
                gotoSummaryPage();
                break;
        }
    }

    private boolean isAnswerSelected()
    {
        return answers[0] || answers[1] || answers[2] || answers[3];
    }

    //计算答题结果
    private boolean calcAnswer()
    {
        String answer = "";
        if (answers[0])
        {
            answer += "A";
        }
        if (answers[1])
        {
            answer += "B";
        }
        if (answers[2])
        {
            answer += "C";
        }
        if (answers[3])
        {
            answer += "D";
        }
        boolean result = currentQuestion.isAnswerCorrect(answer);

        saveQuestionResult(result);
        return result;
    }

    private void saveQuestionResult(boolean result)
    {
        if (result)//答对了从错题库里消除
        {
            QuestionsSharePreferenceManager.removeOneWrongAnswer(getActivity(), QuestionLoader.getCurrentSharePrefLevelName(currentQuestionLevel),
                    Integer.valueOf(currentQuestion.getQid()));
        }
        else//如果答错了进错题库
        {
            QuestionsSharePreferenceManager.updateOneWrongAnswer(getActivity(), QuestionLoader.getCurrentSharePrefLevelName(currentQuestionLevel),
                    Integer.valueOf(currentQuestion.getQid()));
        }

        //纪录对应Level最后道题id
        recordLevelLastQuestionNumber();
    }

    //纪录对应Level最后道题id
    private void recordLevelLastQuestionNumber()
    {
        QuestionsSharePreferenceManager.setLatestAnsweredQuestion(getActivity(), currentQuestionLevel, Integer.valueOf(currentQuestion.getQid()));
    }

    private void showCorrectAnswer()
    {
        if (answers[0] || currentQuestion.isAnswerA())
        {
            aIV.setBackgroundResource(currentQuestion.isAnswerA() ? R.drawable.question_item_right : R.drawable.question_item_wrong);
        }
        if (answers[1] || currentQuestion.isAnswerB())
        {
            bIV.setBackgroundResource(currentQuestion.isAnswerB() ? R.drawable.question_item_right : R.drawable.question_item_wrong);
        }
        if (answers[2] || currentQuestion.isAnswerC())
        {
            cIV.setBackgroundResource(currentQuestion.isAnswerC() ? R.drawable.question_item_right : R.drawable.question_item_wrong);
        }
        if (answers[3] || currentQuestion.isAnswerD())
        {
            dIV.setBackgroundResource(currentQuestion.isAnswerD() ? R.drawable.question_item_right : R.drawable.question_item_wrong);
        }

        if ((type != TYPE_QUESTION_SET_ONLY_RESULT) && currentQuestionIndex == (arrayListQuestion.size() - 1))
        {
            setButtonStatus(STATUS_COMPLETE);
        }
    }

    private void getBundleParameters()
    {
        Bundle bundle = getArguments();
        if (bundle.containsKey(KEY_QUESTION_SET_TYPE))
        {
            type = bundle.getString(KEY_QUESTION_SET_TYPE);
            if (type.equals(TYPE_QUESTION_SET_ONLY_RESULT))
            {
                currentQuestion = (Question) bundle.getSerializable(KEY_QUESTION);
                refreshQuestion();
                showCorrectAnswer();
            }
            else if (type.equals(TYPE_QUESTION_SET_NORMAL))
            {
                currentQuestionLevel = bundle.getString(KEY_QUESTION_SET_LEVEL, QuestionLoader.LEVEL_ONE);
                currentQuestionIndex = bundle.getInt(KEY_QUESTION_CURRENT_ID, 0);
                getCurrentQuestion();
            }
            else if (type.equals(TYPE_QUESTION_SET_FAILED))
            {
                currentQuestionLevel = bundle.getString(KEY_QUESTION_SET_LEVEL, QuestionLoader.LEVEL_ONE_FAILS);
                currentQuestionIndex = bundle.getInt(KEY_QUESTION_CURRENT_ID, 0);
                getCurrentQuestion();
            }
        }
    }

    private void gotoSummaryPage()
    {
        if (getActivity() != null)
        {
            getActivity().finish();
            Bundle bundle = new Bundle();
            bundle.putString(ToAnswerQuestionFragment.KEY_QUESTION_SET_LEVEL, QuestionLoader.getCurrentSharePrefLevelName(currentQuestionLevel));
            gotoDashboard(AnswersSummaryFragment.class, bundle);
        }
    }

    private void refreshQuestion()
    {
        clearImageViewAnswers();

        if (currentQuestion != null)
        {
            questionTitleTV.setText(currentQuestion.getQTitle());
            aTV.setText(currentQuestion.getQAnswerOne());
            bTV.setText(currentQuestion.getQAnswerTwo());
        }
        int choiceType = currentQuestion.getChoiceType();
        if (choiceType == Question.MULTICHOISE)
        {
            cLL.setVisibility(View.VISIBLE);
            dLL.setVisibility(View.VISIBLE);
            cTV.setText(currentQuestion.getQAnswerThree());
            dTV.setText(currentQuestion.getQAnswerFour());
        }
        else if (choiceType == Question.JUDGECHOISE)
        {
            cLL.setVisibility(View.GONE);
            dLL.setVisibility(View.GONE);
        }
        else if (choiceType == Question.ONECHOICE)
        {
            cLL.setVisibility(View.VISIBLE);
            dLL.setVisibility(View.VISIBLE);
            cTV.setText(currentQuestion.getQAnswerThree());
            dTV.setText(currentQuestion.getQAnswerFour());
        }

        if (type == TYPE_QUESTION_SET_ONLY_RESULT)
        {
            nextQuestionBtn.setVisibility(View.GONE);
        }
        else if (type == TYPE_QUESTION_SET_NORMAL)
        {
            nextQuestionBtn.setVisibility(View.VISIBLE);
        }

        setButtonStatus(STATUS_SHOW_ANSWER);
    }

    private void setButtonStatus(int status)
    {
        currentStatus = status;
        showNextQuestionBtnText();
    }

    private void showNextQuestionBtnText()
    {
        switch (currentStatus)
        {
            case STATUS_SHOW_ANSWER:
                nextQuestionBtn.setText(getString(R.string.question_status_show_answer));
                break;

            case STATUS_NEXT_QUESTION:
                nextQuestionBtn.setText(getString(R.string.question_status_next_question));
                break;

            case STATUS_COMPLETE:
                nextQuestionBtn.setText(getString(R.string.question_status_complete));
                break;
        }
    }

    public void updateFragmentTitle()
    {
        if (currentQuestion == null) return;
        if(type.equals(TYPE_QUESTION_SET_ONLY_RESULT))
        {
            setTitleString(getString(R.string.stock_learning_review_history));
            return;
        }
        if (currentQuestionLevel.equals(QuestionLoader.LEVEL_ONE) || currentQuestionLevel.equals(QuestionLoader.LEVEL_ONE_FAILS))
        {
            setTitleString(getString(R.string.question_percent, currentQuestionIndex + 1, arrayListQuestion.size()));
        }
        else if (currentQuestionLevel.equals(QuestionLoader.LEVEL_TWO) || currentQuestionLevel.equals(QuestionLoader.LEVEL_TWO_FAILS))
        {
            setTitleString(getString(R.string.question_percent, currentQuestionIndex + 1, arrayListQuestion.size()));
        }
        else if (currentQuestionLevel.equals(QuestionLoader.LEVEL_THREE) || currentQuestionLevel.equals(QuestionLoader.LEVEL_THREE_FAILS))
        {
            setTitleString(getString(R.string.question_percent, currentQuestionIndex + 1, arrayListQuestion.size()));
        }
    }

    public void setTitleString(String titleString)
    {
        strTitle = titleString;
        setHeadViewMiddleMain(strTitle);
    }

    private Question getNextQuestion()
    {
        currentQuestionIndex += 1;
        getCurrentQuestion();
        return null;
    }

    private void getCurrentQuestionList()
    {
        if (currentQuestionLevel != null && arrayListQuestion == null)
        {
            if (type.equals(TYPE_QUESTION_SET_FAILED))
            {
                arrayListQuestion = QuestionLoader.getInstance(getActivity()).getQuestionFailsList(currentQuestionLevel);
            }
            else
            {
                arrayListQuestion = QuestionLoader.getInstance(getActivity()).getQuestionList(currentQuestionLevel);
            }
        }
    }

    private void getCurrentQuestion()
    {
        if (arrayListQuestion == null)
        {
            getCurrentQuestionList();
        }

        if (arrayListQuestion != null && currentQuestionIndex >= 0 && currentQuestionIndex < arrayListQuestion.size())
        {
            currentQuestion = arrayListQuestion.get(currentQuestionIndex);
            refreshQuestion();
            updateFragmentTitle();
        }
    }
}
