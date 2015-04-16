package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.chinabuild.data.question.questionUtils.Question;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

import java.util.ArrayList;

/**
 * Created by palmer on 15/4/15.
 */
public class AnswerQuestionFragment extends DashboardFragment  implements ViewPager.OnPageChangeListener{

    private ViewPager questionSetVP;

    private ArrayList<Fragment> questionFragments = new ArrayList();
    public ArrayList<Question> arrayListQuestion = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_question_set, container, false);
        questionSetVP = (ViewPager)view.findViewById(R.id.viewpager_questions);
        return view;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public class ViewPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return false;
        }
    }
}
