package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_history, container, false);

        historyLV = (ListView)view.findViewById(R.id.listview_answers_history);
        historyLV.setAdapter(adapter);
        refreshHistoryData();
        return view;
    }

    //Test Data
    private void refreshHistoryData(){
        ArrayList<Question> questions = new ArrayList();
        Question questionA = new Question();
        questionA.setQid("1");
        questionA.setQTitle("AAAAAAAAAAAAAAAAAA");
        questions.add(questionA);


        Question questionB = new Question();
        questionB.setQid("2");
        questionB.setQTitle("BBBBBBBBBBBBBB--------------------------------------------");
        questions.add(questionB);

        Question questionC = new Question();
        questionC.setQid("3");
        questionC.setQTitle("ccccccc");
        questions.add(questionC);

        ArrayList<Integer> failedQuestions = new ArrayList();
        failedQuestions.add(new Integer(2));

        adapter.setQuestionItems(questions, failedQuestions);
        adapter.notifyDataSetChanged();
    }

}
