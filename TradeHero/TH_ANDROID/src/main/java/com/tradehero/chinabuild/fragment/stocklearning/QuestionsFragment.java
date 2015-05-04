package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.metrics.Analytics;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Stock Questions
 *
 * Created by palmer on 15/3/27.
 */
public class QuestionsFragment extends DashboardFragment {


    private ExpandableListView questionsLV;

    private ArrayList<QuestionGroup> levelAItems = new ArrayList();
    private ArrayList<QuestionGroup> levelBItems = new ArrayList();
    private ArrayList<QuestionGroup> levelCItems = new ArrayList();

    private StockLearningQuestionsAdapter stockLearningQuestionsAdapter;

    @Inject Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAdapater();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_questions, container, false);
        questionsLV = (ExpandableListView)view.findViewById(R.id.listview_stock_learning_category);
        initQuestionsLV();
        return view;
    }

    private void initQuestionsLV(){
        questionsLV.setGroupIndicator(null);
        questionsLV.setAdapter(stockLearningQuestionsAdapter);
        questionsLV.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                if (i == 0) {
                    if (questionsLV.isGroupExpanded(1)) {
                        questionsLV.collapseGroup(1);
                    }
                    if (questionsLV.isGroupExpanded(2)) {
                        questionsLV.collapseGroup(2);
                    }
                }
                if (i == 1) {
                    if (questionsLV.isGroupExpanded(0)) {
                        questionsLV.collapseGroup(0);
                    }
                    if (questionsLV.isGroupExpanded(2)) {
                        questionsLV.collapseGroup(2);
                    }
                }
                if (i == 2) {
                    if (questionsLV.isGroupExpanded(0)) {
                        questionsLV.collapseGroup(0);
                    }
                    if (questionsLV.isGroupExpanded(1)) {
                        questionsLV.collapseGroup(1);
                    }
                }
            }
        });
    }

    private void initAdapater(){
        for(int num=0;num<5;num++){
            QuestionGroup questionGroup = new QuestionGroup();
            questionGroup.name = "Test S";
            levelAItems.add(questionGroup);
        }
        for(int num=0;num<5;num++){
            QuestionGroup questionGroup = new QuestionGroup();
            questionGroup.name = "Test T";
            levelBItems.add(questionGroup);
        }
        for(int num=0;num<5;num++){
            QuestionGroup questionGroup = new QuestionGroup();
            questionGroup.name = "Test X";
            levelCItems.add(questionGroup);
        }
        stockLearningQuestionsAdapter = new StockLearningQuestionsAdapter(getActivity(), levelAItems, levelBItems, levelCItems);
    }


}
