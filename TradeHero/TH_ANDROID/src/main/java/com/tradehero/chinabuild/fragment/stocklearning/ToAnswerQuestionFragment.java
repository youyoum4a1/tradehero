package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by palmer on 15/3/30.
 */
public class ToAnswerQuestionFragment extends DashboardFragment implements View.OnClickListener{

    public Button nextQuestionBtn;

    public final static String KEY_QUESTION_SET_TYPE = "key_question_set_type";
    public final static String TYPE_QUESTION_SET_NORMAL = "type_question_set_normal";
    public final static String TYPE_QUESTION_SET_FAILED = "type_question_set_failed";
    public final static String TYPE_QUESTION_SET_ONLY_RESULT = "type_question_set_only_result";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_to_answer, container, false);

        nextQuestionBtn = (Button)view.findViewById(R.id.button_next_question);
        nextQuestionBtn.setOnClickListener(this);

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

    private void gotoSummaryPage(){
        if(getActivity()!=null){
            getActivity().finish();
            Bundle bundle = new Bundle();
            gotoDashboard(AnswersSummaryFragment.class, bundle);
        }
    }
}
