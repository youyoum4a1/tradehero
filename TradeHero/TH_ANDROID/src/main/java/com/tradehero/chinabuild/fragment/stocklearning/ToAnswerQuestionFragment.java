package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.chinabuild.data.question.questionUtils.Question;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by palmer on 15/3/30.
 */
public class ToAnswerQuestionFragment extends DashboardFragment implements View.OnClickListener{

    private Button nextQuestionBtn;
    private LinearLayout aLL;
    private ImageView aIV;
    private TextView aTV;
    private LinearLayout bLL;
    private ImageView bIV;
    private TextView bTV;
    private LinearLayout cLL;
    private ImageView cIV;
    private TextView cTV;
    private LinearLayout dLL;
    private ImageView dIV;
    private TextView dTV;



    private String type = TYPE_QUESTION_SET_ONLY_RESULT;
    public final static String KEY_QUESTION_SET_TYPE = "key_question_set_type";
    public final static String TYPE_QUESTION_SET_NORMAL = "type_question_set_normal";
    public final static String TYPE_QUESTION_SET_FAILED = "type_question_set_failed";
    public final static String TYPE_QUESTION_SET_ONLY_RESULT = "type_question_set_only_result";
    public final static String KEY_QUESTION = "key_question";

    private int onechoice = 0;
    private int multichoice = 1;
    private int rightwrongchoice = 2;

    private int choiceType = -1;

    //For only result type
    private Question question;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getBundleParameters();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_to_answer, container, false);

        nextQuestionBtn = (Button)view.findViewById(R.id.button_next_question);
        nextQuestionBtn.setOnClickListener(this);
        aLL = (LinearLayout)view.findViewById(R.id.linearlayout_question_choice_a);
        aIV = (ImageView)view.findViewById(R.id.imageview_choice_a);
        aTV = (TextView)view.findViewById(R.id.textview_choice_a);
        bLL = (LinearLayout)view.findViewById(R.id.linearlayout_question_choice_b);
        bIV = (ImageView)view.findViewById(R.id.imageview_choice_b);
        bTV = (TextView)view.findViewById(R.id.textview_choice_b);
        cLL = (LinearLayout)view.findViewById(R.id.linearlayout_question_choice_c);
        cIV = (ImageView)view.findViewById(R.id.imageview_choice_c);
        cTV = (TextView)view.findViewById(R.id.textview_choice_c);
        dLL = (LinearLayout)view.findViewById(R.id.linearlayout_question_choice_d);
        dIV = (ImageView)view.findViewById(R.id.imageview_choice_d);
        dTV = (TextView)view.findViewById(R.id.textview_choice_d);
        aLL.setOnClickListener(this);
        bLL.setOnClickListener(this);
        cLL.setOnClickListener(this);
        dLL.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.button_next_question:
                gotoSummaryPage();
                break;
        }
    }

    private void getBundleParameters(){
        Bundle bundle = getArguments();
        if(bundle.containsKey(KEY_QUESTION_SET_TYPE)){
            type = bundle.getString(KEY_QUESTION_SET_TYPE);
            if(type.equals(TYPE_QUESTION_SET_ONLY_RESULT)){
                question = (Question)bundle.getSerializable(KEY_QUESTION);
            }
        }
    }

    private void gotoSummaryPage(){
        if(getActivity()!=null){
            getActivity().finish();
            Bundle bundle = new Bundle();
            gotoDashboard(AnswersSummaryFragment.class, bundle);
        }
    }

    private void refreshView(){
        initChoiceType();
    }

    private void initChoiceType(){
        if(question == null){
            return;
        }
        if(question.getQAnswerCorrect().length()>1){
            choiceType = multichoice;
            return;
        }
        if(question.getQAnswerThree()==null || question.getQAnswerThree().equals("")){
            choiceType = rightwrongchoice;
            return;
        }
        choiceType = onechoice;
    }
}
