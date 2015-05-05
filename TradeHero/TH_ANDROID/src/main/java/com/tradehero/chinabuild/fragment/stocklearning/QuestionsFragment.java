package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import com.tradehero.chinabuild.data.db.StockLearningDatabaseHelper;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.metrics.Analytics;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Stock Questions
 * <p>
 * Created by palmer on 15/3/27.
 */
public class QuestionsFragment extends DashboardFragment {


    private ExpandableListView questionsLV;

    private final long one_day = 86400000;

    private ArrayList<QuestionGroup> levelAItems = new ArrayList();
    private ArrayList<QuestionGroup> levelBItems = new ArrayList();
    private ArrayList<QuestionGroup> levelCItems = new ArrayList();

    private StockLearningQuestionsAdapter stockLearningQuestionsAdapter;

    @Inject
    Analytics analytics;
    @Inject
    protected UserServiceWrapper userServiceWrapper;
    @Inject
    CurrentUserId currentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stockLearningQuestionsAdapter = new StockLearningQuestionsAdapter(getActivity(), levelAItems, levelBItems, levelCItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_questions, container, false);
        questionsLV = (ExpandableListView) view.findViewById(R.id.listview_stock_learning_category);
        initQuestionsLV();
        RetrieveDataHandler handler = new RetrieveDataHandler();
        handler.sendEmptyMessageDelayed(-1, 200);
        downloadQuestions();
        return view;
    }

    private void initQuestionsLV() {
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
        questionsLV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                if(stockLearningQuestionsAdapter!=null){
                }
                return false;
            }
        });
    }


    private void downloadQuestions() {
        if (getActivity() == null) {
            return;
        }
        long updatedAtTicks = THSharePreferenceManager.getQuestionUpdateTime(getActivity());
        userServiceWrapper.downloadQuestions(updatedAtTicks, new Callback<QuestionDTO>() {
            @Override
            public void success(QuestionDTO questionDTO, Response response) {
                if (questionDTO == null || getActivity() == null) {
                    return;
                }
                QuestionCategory[] categories = questionDTO.categories;
                if (categories == null || categories.length <= 0) {
                    return;
                }
                StockLearningDatabaseHelper dbHelper = new StockLearningDatabaseHelper(getActivity());
                int user_id = currentUserId.get();
                if (categories != null) {
                    long updateTime = THSharePreferenceManager.getQuestionUpdateTime(getActivity());
                    for (QuestionCategory questionCategory : categories) {
                        long currentUpdateTime = -1;
                        if (updateTime < currentUpdateTime) {
                            updateTime = currentUpdateTime;
                        }
                    }
                    THSharePreferenceManager.saveQuestionUpdateTime(getActivity(), updateTime);
                }
                QuestionGroup[] groups = questionDTO.subcategories;
                if (groups != null) {
                    dbHelper.insertOrUpdateQuestionGroups(groups, user_id);
                }
                Question[] questions = questionDTO.questions;
                if (questions != null) {
                    dbHelper.insertQuestions(user_id, questions);
                }
                retrieveDatas();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                THException exception = new THException(retrofitError);
                THLog.d(exception.toString());
            }
        });
    }

    private void retrieveDatas() {
        if (getActivity() == null) {
            return;
        }
        StockLearningDatabaseHelper dbHelper = new StockLearningDatabaseHelper(getActivity());
        int user_id = currentUserId.get();
        ArrayList<QuestionGroup> questionGroups = dbHelper.retrieveQuestionGroup(user_id);
        for (QuestionGroup questionGroup : questionGroups) {
            int categoryId = questionGroup.categoryId;
            switch (categoryId) {
                case 1:
                    levelAItems.add(questionGroup);
                    break;
                case 2:
                    levelBItems.add(questionGroup);
                    break;
                case 3:
                    levelCItems.add(questionGroup);
                    break;
            }
        }
        stockLearningQuestionsAdapter.setItems(levelAItems, levelBItems, levelCItems);
    }

    public class RetrieveDataHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            retrieveDatas();
        }
    }
}
