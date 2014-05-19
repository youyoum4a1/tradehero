package com.tradehero.th.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import com.tradehero.th.R;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity
        implements ViewPager.OnPageChangeListener, View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        List<View> list = new ArrayList<>();
        list.add(createGuideImage(R.drawable.guide1));
        list.add(createGuideImage(R.drawable.guide2));
        list.add(createGuideImage(R.drawable.guide3));
        list.add(createGuideImage(R.drawable.guide4));
        ImageView imageView5 = createGuideImage(R.drawable.guide5);
        list.add(imageView5);
        imageView5.setOnClickListener(this);

        viewpager.setAdapter(new ListViewPagerAdapter(list));
        viewpager.setOnPageChangeListener(this);
    }

    private ImageView createGuideImage(int drawableResId)
    {
        ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.guide_layout, null);
        imageView.setBackgroundResource(drawableResId);
        return imageView;
    }

    @Override public void onClick(View v)
    {
        ActivityHelper.launchAuthentication(this);
    }

    class ListViewPagerAdapter extends PagerAdapter
    {
        private List<View> list = null;

        public ListViewPagerAdapter(List<View> list)
        {
            this.list = list;
        }

        @Override
        public int getCount()
        {
            return list.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView(list.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }
    }

    @Override public void onPageScrolled(int i, float v, int i2)
    {
    }

    @Override public void onPageSelected(int i)
    {
    }

    @Override public void onPageScrollStateChanged(int i)
    {
    }
}
