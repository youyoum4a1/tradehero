package com.tradehero.chinabuild.fragment.stocklearning;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.chinabuild.fragment.stocklearning.question.questionUtils.Question;
import com.tradehero.th.R;
import com.tradehero.th.utils.DaggerUtils;

/**
 * Created by palmer on 15/4/24.
 */
public class OneQuestionFragment extends Fragment implements View.OnClickListener{

    private Question question;

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


    public static String KEY_ONE_QUESTION = "key_one_question";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments();
    }


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_to_answer, container, false);
        ButterKnife.inject(this, view);
        aLL.setOnClickListener(this);
        bLL.setOnClickListener(this);
        cLL.setOnClickListener(this);
        dLL.setOnClickListener(this);
        refreshView();
        return view;
    }


    private void refreshView(){
        if(question==null){
            aLL.setVisibility(View.GONE);
            bLL.setVisibility(View.GONE);
            cLL.setVisibility(View.GONE);
            dLL.setVisibility(View.GONE);

        }
        questionTitleTV.setText(question.getQTitle());
        aTV.setText(question.getQAnswerOne());
        bTV.setText(question.getQAnswerTwo());
        int choiceType = question.getChoiceType();
        if (choiceType == Question.MULTICHOISE)
        {
            cLL.setVisibility(View.VISIBLE);
            dLL.setVisibility(View.VISIBLE);
            cTV.setText(question.getQAnswerThree());
            dTV.setText(question.getQAnswerFour());
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
            cTV.setText(question.getQAnswerThree());
            dTV.setText(question.getQAnswerFour());
        }
    }

    private void initArguments(){
        question = (Question)getArguments().getSerializable(KEY_ONE_QUESTION);
    }

    @Override
    public void onClick(View view) {
    }
}
