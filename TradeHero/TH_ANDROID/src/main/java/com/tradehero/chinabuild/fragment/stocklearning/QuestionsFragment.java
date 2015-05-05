package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.metrics.Analytics;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Stock Questions
 *
 * Created by palmer on 15/3/27.
 */
public class QuestionsFragment extends DashboardFragment {


    private ExpandableListView questionsLV;

    private ArrayList<StockLearningSubGroup> levelAItems = new ArrayList();
    private ArrayList<StockLearningSubGroup> levelBItems = new ArrayList();
    private ArrayList<StockLearningSubGroup> levelCItems = new ArrayList();

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
            StockLearningSubGroup stockLearningSubGroup = new StockLearningSubGroup();
            stockLearningSubGroup.setName("Test S");
            stockLearningSubGroup.setLastNumber(10);
            stockLearningSubGroup.setTotalNumber(100);
            levelAItems.add(stockLearningSubGroup);
        }
        for(int num=0;num<5;num++){
            StockLearningSubGroup stockLearningSubGroup = new StockLearningSubGroup();
            stockLearningSubGroup.setName("Test T");
            stockLearningSubGroup.setLastNumber(10);
            stockLearningSubGroup.setTotalNumber(100);
            levelBItems.add(stockLearningSubGroup);
        }
        for(int num=0;num<5;num++){
            StockLearningSubGroup stockLearningSubGroup = new StockLearningSubGroup();
            stockLearningSubGroup.setName("Test X");
            stockLearningSubGroup.setLastNumber(10);
            stockLearningSubGroup.setTotalNumber(100);
            levelCItems.add(stockLearningSubGroup);
        }
        stockLearningQuestionsAdapter = new StockLearningQuestionsAdapter(getActivity(), levelAItems, levelBItems, levelCItems);
    }


}
