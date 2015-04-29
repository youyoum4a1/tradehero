package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshExpandableListView;
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


    private PullToRefreshExpandableListView questionsLV;

    private ArrayList<StockLearningQuestionsItem> levelAItems = new ArrayList();
    private ArrayList<StockLearningQuestionsItem> levelBItems = new ArrayList();
    private ArrayList<StockLearningQuestionsItem> levelCItems = new ArrayList();

    private StockLearningQuestionsAdapter stockLearningQuestionsAdapter;

    @Inject Analytics analytics;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_questions, container, false);
        questionsLV = (PullToRefreshExpandableListView)view.findViewById(R.id.listview_stock_learning_category);
        initQuestionsLV();
        return view;
    }

    private void initQuestionsLV(){
        questionsLV.getRefreshableView().setGroupIndicator(null);
        questionsLV.setMode(PullToRefreshBase.Mode.DISABLED);
        for(int num=0;num<5;num++){
            StockLearningQuestionsItem stockLearningQuestionsItem = new StockLearningQuestionsItem();
            stockLearningQuestionsItem.setName("Test S");
            stockLearningQuestionsItem.setLastNumber(10);
            stockLearningQuestionsItem.setTotalNumber(100);
            levelAItems.add(stockLearningQuestionsItem);
        }
        for(int num=0;num<5;num++){
            StockLearningQuestionsItem stockLearningQuestionsItem = new StockLearningQuestionsItem();
            stockLearningQuestionsItem.setName("Test T");
            stockLearningQuestionsItem.setLastNumber(10);
            stockLearningQuestionsItem.setTotalNumber(100);
            levelBItems.add(stockLearningQuestionsItem);
        }
        for(int num=0;num<5;num++){
            StockLearningQuestionsItem stockLearningQuestionsItem = new StockLearningQuestionsItem();
            stockLearningQuestionsItem.setName("Test X");
            stockLearningQuestionsItem.setLastNumber(10);
            stockLearningQuestionsItem.setTotalNumber(100);
            levelCItems.add(stockLearningQuestionsItem);
        }
        stockLearningQuestionsAdapter = new StockLearningQuestionsAdapter(getActivity(), levelAItems, levelBItems, levelCItems);
        questionsLV.getRefreshableView().setAdapter(stockLearningQuestionsAdapter);
        questionsLV.getRefreshableView().setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                if (i == 0) {
                    if (questionsLV.getRefreshableView().isGroupExpanded(1)) {
                        questionsLV.getRefreshableView().collapseGroup(1);
                    }
                    if (questionsLV.getRefreshableView().isGroupExpanded(2)) {
                        questionsLV.getRefreshableView().collapseGroup(2);
                    }
                }
                if (i == 1) {
                    if (questionsLV.getRefreshableView().isGroupExpanded(0)) {
                        questionsLV.getRefreshableView().collapseGroup(0);
                    }
                    if (questionsLV.getRefreshableView().isGroupExpanded(2)) {
                        questionsLV.getRefreshableView().collapseGroup(2);
                    }
                }
                if (i == 2) {
                    if (questionsLV.getRefreshableView().isGroupExpanded(0)) {
                        questionsLV.getRefreshableView().collapseGroup(0);
                    }
                    if (questionsLV.getRefreshableView().isGroupExpanded(1)) {
                        questionsLV.getRefreshableView().collapseGroup(1);
                    }
                }
            }
        });
    }


}
