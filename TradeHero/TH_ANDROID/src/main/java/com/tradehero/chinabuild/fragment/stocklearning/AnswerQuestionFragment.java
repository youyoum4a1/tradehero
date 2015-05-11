package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.chinabuild.data.db.StockLearningDatabaseHelper;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;

import javax.inject.Inject;

import java.util.ArrayList;

/**
 * Created by palmer on 15/4/15.
 */
public class AnswerQuestionFragment extends DashboardFragment implements ViewPager.OnPageChangeListener {

    private ViewPager questionSetVP;

    public final static String KEY_QUESTION_GROUP_TYPE = "key_question_group_type";
    public final static String TYPE_NORMAL = "type_normal";
    public final static String TYPE_ERROR = "type_error";
    public final static String TYPE_ONLY_ONE = "type_only_one";
    public final static String KEY_QUESTION_GROUP = "key_question_group";
    public final static String KEY_QUESTION = "key_question";

    private String type = "";
    private QuestionGroup questionGroup = null;

    private ArrayList<Fragment> questionFragments = new ArrayList();
    private ArrayList<Question> questions = new ArrayList();

    private int currentIndex = 0;

    @Inject CurrentUserId currentUserId;

    private QuestionsViewPagerAdapter adapter;

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }
        view = inflater.inflate(R.layout.stock_learning_question_set, container, false);
        questionSetVP = (ViewPager) view.findViewById(R.id.viewpager_questions);
        initViewPager();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        refreshHeadView(0);
    }

    private void initArguments() {
        Bundle bundle = getArguments();
        if (bundle.containsKey(KEY_QUESTION_GROUP)) {
            questionGroup = (QuestionGroup) bundle.getSerializable(KEY_QUESTION_GROUP);
            if (questionGroup == null || getActivity() == null) {
                popCurrentFragment();
            }
            type = bundle.getString(KEY_QUESTION_GROUP_TYPE);
            if (type.equals("")) {
                popCurrentFragment();
            }
            if (type.equals(TYPE_NORMAL)) {
                StockLearningDatabaseHelper dbHelper = new StockLearningDatabaseHelper(getActivity());
                questions = dbHelper.retrieveQuestions(questionGroup.id);
            }
            if (type.equals(TYPE_ERROR)) {
                questions = StockLearningQuestionManager.getInstance().getReAnswerQuestions();
            }
            if (type.equals(TYPE_ONLY_ONE)) {
                Question question = (Question) bundle.getSerializable(KEY_QUESTION);
                if (question == null) {
                    popCurrentFragment();
                    return;
                }
                questions.clear();
                questions.add(question);
            }
        }
    }

    private void initViewPager() {
        int questionSize = questions.size();
        questionFragments.clear();
        for (int num = 0; num < questionSize; num++) {
            Question question = questions.get(num);
            OneQuestionFragment fragment = new OneQuestionFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(OneQuestionFragment.KEY_ONE_QUESTION, question);
            bundle.putInt(OneQuestionFragment.KEY_USER_ID, currentUserId.get());
            if (num == (questionSize - 1)) {
                bundle.putBoolean(OneQuestionFragment.KEY_FINAL_QUESTION, true);
            } else {
                bundle.putBoolean(OneQuestionFragment.KEY_FINAL_QUESTION, false);
            }
            bundle.putSerializable(OneQuestionFragment.KEY_QUESTION_GROUP, questionGroup);
            bundle.putInt(OneQuestionFragment.KEY_QUESTION_INDEX, num + 1);
            bundle.putString(KEY_QUESTION_GROUP_TYPE, type);
            fragment.setArguments(bundle);
            questionFragments.add(fragment);
        }
        if(adapter==null){
            adapter = new QuestionsViewPagerAdapter(getActivity().getSupportFragmentManager());
        }
        questionSetVP.setAdapter(adapter);
        questionSetVP.setOnPageChangeListener(this);
        if (type.equals(TYPE_ERROR) || type.equals(TYPE_ONLY_ONE)) {
            questionSetVP.setCurrentItem(0);
        }
        if (type.equals(TYPE_NORMAL)) {
            questionSetVP.setCurrentItem(questionGroup.question_group_progress - 1);
        }

    }

    private void refreshHeadView(int index) {
        String menuTitle = getString(R.string.question_percent, index + 1, questions.size());
        setHeadViewMiddleMain(menuTitle);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        if (currentIndex != i) {
            currentIndex = i;
            refreshHeadView(currentIndex);
        }
    }

    @Override
    public void onPageSelected(int i) {
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public class QuestionsViewPagerAdapter extends FragmentStatePagerAdapter {

        public QuestionsViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {

            return questionFragments.get(i);
        }

        @Override
        public int getCount() {
            return questionFragments.size();
        }
    }
}
