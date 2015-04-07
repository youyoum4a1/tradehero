package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import com.tradehero.chinabuild.data.question.questionUtils.QuestionLoader;
import com.tradehero.chinabuild.data.sp.QuestionsSharePreferenceManager;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Stock Questions
 *
 * Created by palmer on 15/3/27.
 */
public class QuestionsFragment extends DashboardFragment {

    private StockLearningQuestionsAdapter questionsAdapter;

    private ListView questionsLV;

    @Inject Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        questionsAdapter = new StockLearningQuestionsAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_questions, container, false);
        ButterKnife.inject(this, view);
        questionsLV = (ListView) view.findViewById(R.id.listview_questions);
        questionsLV.setAdapter(questionsAdapter);
        questionsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                gotoAnswerQuestions(position);
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.QUESTION_SET_SELECT, String.valueOf(position)));
            }
        });

        return view;
    }

    @Override public void onResume() {
        super.onResume();
        refreshQuestions();
    }

    //Initialize local data
    private void refreshQuestions() {
        ArrayList<StockLearningQuestionsItem> questionsItemDTOs = new ArrayList();
        StockLearningQuestionsItem questionsItemDTOA = new StockLearningQuestionsItem();
        questionsItemDTOA.setTotalNumber(QuestionLoader.TOTAL_NUM_QA);
        questionsItemDTOA.setLastNumber(QuestionsSharePreferenceManager.getLatestAnsweredQuestion(getActivity(), QuestionLoader.getLevelName(0)));
        questionsItemDTOA.setName(getString(R.string.stock_learning_level1));
        questionsItemDTOA.setBgResId(R.drawable.learning_a_bg);
        questionsItemDTOs.add(questionsItemDTOA);

        StockLearningQuestionsItem questionsItemDTOB = new StockLearningQuestionsItem();
        questionsItemDTOB.setTotalNumber(QuestionLoader.TOTAL_NUM_QB);
        questionsItemDTOB.setLastNumber(QuestionsSharePreferenceManager.getLatestAnsweredQuestion(getActivity(), QuestionLoader.getLevelName(1)));
        questionsItemDTOB.setName(getString(R.string.stock_learning_level2));
        questionsItemDTOB.setBgResId(R.drawable.learning_b_bg);
        questionsItemDTOs.add(questionsItemDTOB);

        StockLearningQuestionsItem questionsItemDTOC = new StockLearningQuestionsItem();
        questionsItemDTOC.setTotalNumber(QuestionLoader.TOTAL_NUM_QC);
        questionsItemDTOC.setLastNumber(QuestionsSharePreferenceManager.getLatestAnsweredQuestion(getActivity(), QuestionLoader.getLevelName(2)));
        questionsItemDTOC.setName(getString(R.string.stock_learning_level3));
        questionsItemDTOC.setBgResId(R.drawable.learning_c_bg);
        questionsItemDTOs.add(questionsItemDTOC);

        questionsAdapter.setQuestionsItemDTOs(questionsItemDTOs);
        questionsAdapter.notifyDataSetChanged();
    }

    private void gotoAnswerQuestions(int numOfQuestionSet)
    {
        int lastNumber = QuestionsSharePreferenceManager.getLatestAnsweredQuestion(getActivity(), QuestionLoader.getLevelName(numOfQuestionSet));
        if(lastNumber >= QuestionLoader.getLevelMaxNumber(numOfQuestionSet))
        {
            Bundle bundle = new Bundle();
            bundle.putString(ToAnswerQuestionFragment.KEY_QUESTION_SET_LEVEL, QuestionLoader.getLevelName(numOfQuestionSet));
            gotoDashboard(AnswersSummaryFragment.class, bundle);
        }
        else
        {
            Bundle bundle = new Bundle();
            bundle.putString(ToAnswerQuestionFragment.KEY_QUESTION_SET_TYPE, ToAnswerQuestionFragment.TYPE_QUESTION_SET_NORMAL);
            bundle.putString(ToAnswerQuestionFragment.KEY_QUESTION_SET_LEVEL, QuestionLoader.getLevelName(numOfQuestionSet));
            bundle.putInt(ToAnswerQuestionFragment.KEY_QUESTION_CURRENT_ID, lastNumber);
            gotoDashboard(ToAnswerQuestionFragment.class, bundle);
        }
    }
}
