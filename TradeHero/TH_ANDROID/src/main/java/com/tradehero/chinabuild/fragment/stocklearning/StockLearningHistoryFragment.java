package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.chinabuild.data.db.StockLearningDatabaseHelper;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Created by palmer on 15/3/31.
 */
public class StockLearningHistoryFragment extends DashboardFragment {
    private ListView historyLV;
    private StockLearningAnswersHistoryAdapter adapter;

    private ArrayList<Question> questions = new ArrayList();
    private ArrayList<QuestionStatusRecord> records = new ArrayList();

    @Inject CurrentUserId currentUserId;

    private QuestionGroup questionGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArgument();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getString(R.string.stock_learning_review_history_menu));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_history, container, false);
        refreshHistoryData();
        historyLV = (ListView) view.findViewById(R.id.listview_answers_history);
        historyLV.setAdapter(adapter);
        historyLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long i) {
                gotoHistory(adapter.getItem(position));
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshQuestionHandler handler = new RefreshQuestionHandler();
        handler.sendEmptyMessageDelayed(-1, 200);
    }

    public void initArgument() {
        Bundle bundle = getArguments();
        questionGroup = (QuestionGroup) bundle.getSerializable(AnswersSummaryFragment.KEY_QUESTION_GROUP);
        if (questionGroup == null) {
            popCurrentFragment();
        }
    }

    private void refreshHistoryData() {
        if (adapter == null) {
            adapter = new StockLearningAnswersHistoryAdapter(getActivity());
        }
    }

    private void gotoHistory(Question question) {
        Bundle bundle = new Bundle();
        bundle.putString(AnswerQuestionFragment.KEY_QUESTION_GROUP_TYPE, AnswerQuestionFragment.TYPE_ONLY_ONE);
        bundle.putSerializable(AnswerQuestionFragment.KEY_QUESTION_GROUP, questionGroup);
        bundle.putSerializable(AnswerQuestionFragment.KEY_QUESTION, question);
        pushFragment(AnswerQuestionFragment.class, bundle);
    }

    public class RefreshQuestionHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (getActivity() == null) {
                return;
            }
            StockLearningDatabaseHelper stockLearningDatabaseHelper = new StockLearningDatabaseHelper(getActivity());
            records = stockLearningDatabaseHelper.retrieveQuestionRecords(currentUserId.get(), questionGroup.id);
            questions = stockLearningDatabaseHelper.retrieveQuestions(questionGroup.id);
            if(adapter!=null){
                adapter.setQuestionItems(questions, records);
            }
        }
    }
}
