package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

import java.util.ArrayList;

/**
 * Created by palmer on 15/4/15.
 */
public class AnswerQuestionFragment extends DashboardFragment implements ViewPager.OnPageChangeListener {

    private ViewPager questionSetVP;

    private ArrayList<Fragment> questionFragments = new ArrayList();
    private ArrayList<Question> arrayListQuestion = new ArrayList();


    public final static String KEY_QUESTION_SET_TYPE = "key_question_set_type";
    public final static String TYPE_QUESTION_SET_NORMAL = "type_question_set_normal";//正常题库答题
    public final static String TYPE_QUESTION_SET_FAILED = "type_question_set_failed";//错题库
    public final static String TYPE_QUESTION_SET_ONLY_RESULT = "type_question_set_only_result";//查看历史单道题
    public final static String KEY_QUESTION = "key_question";

    public final static String KEY_QUESTION_SET_LEVEL = "key_question_set_level";//第几套题 LEVEL1,2,3
    public final static String KEY_QUESTION_CURRENT_ID = "key_question_current_id";//第几道题开始

    private String questionSetType = "";
    private int beginIndex = 0;
    private int currentIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_question_set, container, false);
        questionSetVP = (ViewPager) view.findViewById(R.id.viewpager_questions);
        initViewPager();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        refreshHeadView(beginIndex);
    }

    private void initArguments() {
        Bundle bundle = getArguments();
        if (bundle.containsKey(KEY_QUESTION_SET_TYPE)) {
            questionSetType = bundle.getString(KEY_QUESTION_SET_TYPE);
            if (questionSetType.equals(TYPE_QUESTION_SET_ONLY_RESULT)) {
            }
            if (questionSetType.equals(TYPE_QUESTION_SET_NORMAL)) {
            }
            if (questionSetType.equals(TYPE_QUESTION_SET_FAILED)) {
            }
        } else {
            popCurrentFragment();
        }
        beginIndex = bundle.getInt(KEY_QUESTION_CURRENT_ID, 0);
        currentIndex = beginIndex;
    }

    private void initViewPager(){
        for(Question question : arrayListQuestion){
            OneQuestionFragment fragment = new OneQuestionFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(OneQuestionFragment.KEY_ONE_QUESTION, question);
            fragment.setArguments(bundle);
            questionFragments.add(fragment);
        }
        questionSetVP.setAdapter(new QuestionsViewPagerAdapter(getActivity().getSupportFragmentManager()));
        questionSetVP.setOnPageChangeListener(this);
        questionSetVP.setCurrentItem(beginIndex);
    }

    private void refreshHeadView(int index){
        String menuTitle = getString(R.string.question_percent, index + 1, arrayListQuestion.size());
        setHeadViewMiddleMain(menuTitle);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        if(currentIndex!=i){
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

    public class QuestionsViewPagerAdapter extends FragmentPagerAdapter {

        public QuestionsViewPagerAdapter(FragmentManager fragmentManager){
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
