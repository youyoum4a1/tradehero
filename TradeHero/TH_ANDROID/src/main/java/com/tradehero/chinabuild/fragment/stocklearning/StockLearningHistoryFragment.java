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
import com.tradehero.chinabuild.data.question.questionUtils.QuestionLoader;
import com.tradehero.chinabuild.data.sp.QuestionsSharePreferenceManager;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import java.util.ArrayList;

/**
 * Created by palmer on 15/3/31.
 */
public class StockLearningHistoryFragment extends DashboardFragment
{
    private String currentQuestionLevel = QuestionLoader.LEVEL_ONE;
    private ListView historyLV;
    private StockLearningAnswersHistoryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initArgument();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getString(R.string.stock_learning_review_history_menu));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.stock_learning_history, container, false);
        refreshHistoryData();
        historyLV = (ListView) view.findViewById(R.id.listview_answers_history);
        historyLV.setAdapter(adapter);
        historyLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long i)
            {
                gotoHistory(adapter.getItem(position));
            }
        });

        return view;
    }

    public void initArgument()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            currentQuestionLevel = bundle.getString(ToAnswerQuestionFragment.KEY_QUESTION_SET_LEVEL, QuestionLoader.LEVEL_ONE);
        }
    }

    //返回历史题数据，包含错误标记
    public ArrayList<Question> getHistoryQuestionList()
    {
        ArrayList<Integer> arrayListErrorId = QuestionsSharePreferenceManager.getWrongQuestions(getActivity(), currentQuestionLevel);
        ArrayList<Question> arrayList = QuestionLoader.getInstance(getActivity()).getQuestionList(currentQuestionLevel);
        //把错题打上isError标记
        if (arrayListErrorId != null && arrayListErrorId.size() > 0)
        {
            for (int i = 0; i < arrayListErrorId.size(); i++)
            {
                arrayList.get(arrayListErrorId.get(i) - 1).setIsError(true);
            }
        }
        return arrayList;
    }

    //返回错误答题数据，不做错误标记
    public ArrayList<Question> getErrorQuestionList()
    {
        ArrayList<Integer> arrayListErrorId = QuestionsSharePreferenceManager.getWrongQuestions(getActivity(), currentQuestionLevel);
        ArrayList<Question> arrayList = QuestionLoader.getInstance(getActivity()).getQuestionList(currentQuestionLevel);
        ArrayList<Question> arrayListError = new ArrayList<Question>();
        if (arrayListErrorId != null && arrayListErrorId.size() > 0)
        {
            for (int i = 0; i < arrayListErrorId.size(); i++)
            {
                Question question = arrayList.get(arrayListErrorId.get(i) - 1);
                arrayListError.add(question);
            }
        }
        return arrayListError;
    }

    //Test Data
    private void refreshHistoryData()
    {
        if (adapter == null)
        {
            adapter = new StockLearningAnswersHistoryAdapter(getActivity());
            adapter.setQuestionItems(getHistoryQuestionList());
        }
    }

    private void gotoHistory(Question question)
    {
        Bundle bundle = new Bundle();
        bundle.putString(ToAnswerQuestionFragment.KEY_QUESTION_SET_TYPE, ToAnswerQuestionFragment.TYPE_QUESTION_SET_ONLY_RESULT);
        bundle.putSerializable(ToAnswerQuestionFragment.KEY_QUESTION, question);
        pushFragment(ToAnswerQuestionFragment.class, bundle);
    }
}
