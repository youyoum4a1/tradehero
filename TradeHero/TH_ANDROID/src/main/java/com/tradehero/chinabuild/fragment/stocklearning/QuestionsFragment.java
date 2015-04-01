package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

import java.util.ArrayList;

/**
 * Stock Questions
 *
 * Created by palmer on 15/3/27.
 */
public class QuestionsFragment extends DashboardFragment{

    private StockLearningQuestionsAdapter questionsAdapter;

    private ListView questionsLV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionsAdapter = new StockLearningQuestionsAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_questions, container, false);
        questionsLV = (ListView)view.findViewById(R.id.listview_questions);
        questionsLV.setAdapter(questionsAdapter);
        questionsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                StockLearningQuestionsItem questionsItemDTO = questionsAdapter.getItem(position);
                gotoAnswerQuestions(questionsItemDTO.id);
            }
        });
        refreshQuestions();

        return view;
    }

    //Test
    private void refreshQuestions(){
        ArrayList<StockLearningQuestionsItem> questionsItemDTOs = new ArrayList();
        StockLearningQuestionsItem questionsItemDTOA = new StockLearningQuestionsItem();
        questionsItemDTOA.totalNumber = 20;
        questionsItemDTOA.alreayCompletedNumber = 6;
        questionsItemDTOA.name = "AAAAA";
        questionsItemDTOs.add(questionsItemDTOA);

        StockLearningQuestionsItem questionsItemDTOB = new StockLearningQuestionsItem();
        questionsItemDTOB.totalNumber = 20;
        questionsItemDTOB.alreayCompletedNumber = 6;
        questionsItemDTOB.name = "BBBBB";
        questionsItemDTOs.add(questionsItemDTOB);

        StockLearningQuestionsItem questionsItemDTOC = new StockLearningQuestionsItem();
        questionsItemDTOC.totalNumber = 20;
        questionsItemDTOC.alreayCompletedNumber = 6;
        questionsItemDTOC.name = "CCCCC";
        questionsItemDTOs.add(questionsItemDTOC);

        questionsAdapter.setQuestionsItemDTOs(questionsItemDTOs);
        questionsAdapter.notifyDataSetChanged();
    }

    private void gotoAnswerQuestions(int questionSetId){
        Bundle bundle = new Bundle();
        gotoDashboard(ToAnswerQuestionFragment.class, bundle);
    }

}
