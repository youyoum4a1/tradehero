package com.tradehero.chinabuild;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.chinabuild.fragment.stocklearning.PublicClassFragment;
import com.tradehero.chinabuild.fragment.stocklearning.QuestionsFragment;
import com.tradehero.th.R;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Stock Learning
 *
 * Created by palmer on 15/3/25.
 */
public class MainTabFragmentLearning extends AbsBaseFragment {

    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) TabPageIndicator indicator;
    private FragmentPagerAdapter adapter;

    private static final String[] CONTENT = new String[] {"入学宝典"
            , "公开课"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_tab_fragment_learning_layout, container, false);
        ButterKnife.inject(this, view);

        initView();

        return view;
    }

    private void initView() {
        adapter = new CustomAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        indicator.setViewPager(pager);
    }

    class CustomAdapter extends FragmentPagerAdapter
    {
        public CustomAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    return new QuestionsFragment();
                case 1:
                    return new PublicClassFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount()
        {
            return CONTENT.length;
        }
    }
}
