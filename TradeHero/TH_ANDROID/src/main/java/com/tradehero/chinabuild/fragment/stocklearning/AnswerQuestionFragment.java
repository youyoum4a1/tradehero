package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.RelativeLayout;

import com.tradehero.chinabuild.data.db.THDatabaseHelper;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;

import javax.inject.Inject;

import java.util.ArrayList;

/**
 * Created by palmer on 15/4/15.
 */
public class AnswerQuestionFragment extends DashboardFragment implements ViewPager.OnPageChangeListener {

    private ViewPager questionSetVP;
    private RelativeLayout guideRL;

    public final static String KEY_QUESTION_GROUP_TYPE = "key_question_group_type";
    public final static String TYPE_NORMAL = "type_normal";
    public final static String TYPE_ERROR = "type_error";
    public final static String TYPE_ONLY_ONE = "type_only_one";
    public final static String KEY_QUESTION_GROUP = "key_question_group";
    public final static String KEY_QUESTION = "key_question";
    public final static String KEY_ERROR_QUESTION_SIZE = "key_error_question_size";

    private String type = "";
    private QuestionGroup questionGroup = null;

    private ArrayList<Fragment> questionFragments = new ArrayList();
    private ArrayList<Question> questions = new ArrayList();

    private int currentIndex = 0;

    @Inject CurrentUserId currentUserId;
    @Inject Analytics analytics;

    private QuestionsViewPagerAdapter adapter;

    private View view;

    private int errorQuesTotal = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }
        view = inflater.inflate(R.layout.stock_learning_question_set, container, false);
        initArguments();
        questionSetVP = (ViewPager) view.findViewById(R.id.viewpager_questions);
        guideRL = (RelativeLayout) view.findViewById(R.id.relativelayout_guide_stock_learning_question);
        if (THSharePreferenceManager.isGuideAvailable(getActivity(), THSharePreferenceManager.GUIDE_STOCK_LEARNING_QUESTION)) {
            guideRL.setVisibility(View.VISIBLE);
            guideRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guideRL.setVisibility(View.GONE);
                    THSharePreferenceManager.setGuideShowed(getActivity(), THSharePreferenceManager.GUIDE_STOCK_LEARNING_QUESTION);
                }
            });
        }
        initViewPager();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (type.equals(TYPE_ERROR) || type.equals(TYPE_ONLY_ONE)) {
            refreshHeadView(0);
        }
        if (type.equals(TYPE_NORMAL) && questionGroup != null) {
            refreshHeadView(questionGroup.question_group_progress - 1);
        }
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
                THDatabaseHelper dbHelper = new THDatabaseHelper(getActivity());
                questions = dbHelper.retrieveQuestions(questionGroup.id);
            }
            if (type.equals(TYPE_ERROR)) {
                questions = StockLearningQuestionManager.getInstance().getReAnswerQuestions();
                errorQuesTotal = bundle.getInt(KEY_ERROR_QUESTION_SIZE, 1);
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
        if (adapter == null) {
            adapter = new QuestionsViewPagerAdapter();
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
        int size = 1;
        if (type.equals(TYPE_ERROR)) {
            size = errorQuesTotal;
        } else {
            size = questions.size();
        }
        if (index < 0) {
            index = 0;
        }
        String menuTitle = getString(R.string.question_percent, index + 1, size);
        setHeadViewMiddleMain(menuTitle);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        if (currentIndex != i) {
            currentIndex = i;
            refreshHeadView(currentIndex);
            analytics.addEvent(new MethodEvent(AnalyticsConstants.QUESTION_NEXT_QUESTION, questionGroup.id + ": " + questionGroup.name));
        }
    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public class QuestionsViewPagerAdapter extends FragmentStatePagerAdapter {

        public QuestionsViewPagerAdapter() {
            super(getChildFragmentManager());
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
