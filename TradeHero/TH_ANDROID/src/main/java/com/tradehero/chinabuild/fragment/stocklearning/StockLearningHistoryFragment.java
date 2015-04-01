package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.chinabuild.data.question.questionUtils.Question;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

import java.util.ArrayList;

/**
 * Created by palmer on 15/3/31.
 */
public class StockLearningHistoryFragment extends DashboardFragment{

    private ListView historyLV;
    private StockLearningAnswersHistoryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new StockLearningAnswersHistoryAdapter(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getString(R.string.stock_learning_review_history_menu));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_history, container, false);

        historyLV = (ListView)view.findViewById(R.id.listview_answers_history);
        historyLV.setAdapter(adapter);
        historyLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long i) {
                gotoHistory(adapter.getItem(position));
            }
        });
        refreshHistoryData();
        return view;
    }

    //Test Data
    private void refreshHistoryData(){
        ArrayList<Question> questions = new ArrayList();
        Question questionA = new Question();
        questionA.setQid("1");
        questionA.setQTitle("AAAAAAAAAAAAAAAAAA");
        questionA.setQAnswerOne("A");
        questionA.setQAnswerTwo("B");
        questionA.setQAnswerThree("C");
        questionA.setQAnswerFour("D");
        questionA.setQAnswerCorrect("ACD");
        questions.add(questionA);


        Question questionB = new Question();
        questionB.setQid("2");
        questionB.setQTitle("BBBBBBBBBBBBBB--------------------------------------------");
        questionB.setQAnswerOne("A");
        questionB.setQAnswerTwo("B");
        questionB.setQAnswerCorrect("A");
        questions.add(questionB);

        Question questionC = new Question();
        questionC.setQid("3");
        questionC.setQTitle("ccccccc");
        questionC.setQAnswerOne("A");
        questionC.setQAnswerTwo("B");
        questionC.setQAnswerThree("C");
        questionC.setQAnswerFour("D");
        questionC.setQAnswerCorrect("C");
        questions.add(questionC);

        ArrayList<Integer> failedQuestions = new ArrayList();
        failedQuestions.add(new Integer(2));

        adapter.setQuestionItems(questions, failedQuestions);
        adapter.notifyDataSetChanged();
    }

    private void gotoHistory(Question question){
        Bundle bundle = new Bundle();
        bundle.putString(ToAnswerQuestionFragment.KEY_QUESTION_SET_TYPE, ToAnswerQuestionFragment.TYPE_QUESTION_SET_ONLY_RESULT);
        bundle.putSerializable(ToAnswerQuestionFragment.KEY_QUESTION, question);
        pushFragment(ToAnswerQuestionFragment.class, bundle);
    }

}
