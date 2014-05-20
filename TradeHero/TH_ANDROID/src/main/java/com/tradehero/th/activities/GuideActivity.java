package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import com.tradehero.th.R;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class GuideActivity extends Activity
        implements
        ViewPager.OnPageChangeListener,
        View.OnClickListener
{
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.guide1);
        list.add(R.drawable.guide2);
        list.add(R.drawable.guide3);
        list.add(R.drawable.guide4);
        list.add(R.drawable.guide5);

        viewpager.setAdapter(new ListViewPagerAdapter(list));
        viewpager.setOnPageChangeListener(this);

        createShortcut();
    }

    private void createShortcut()
    {
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra("duplicate", false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.launcher);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), SplashActivity.class));
        sendBroadcast(intent);
    }

    @Override public void onClick(View v)
    {
        ActivityHelper.launchAuthentication(this);
    }

    class ListViewPagerAdapter extends PagerAdapter
    {
        private List<Integer> drawableIdList = null;

        public ListViewPagerAdapter(List<Integer> drawableIdList)
        {
            this.drawableIdList = drawableIdList;
        }

        @Override public int getCount()
        {
            return drawableIdList.size();
        }

        private boolean isLast(int position)
        {
            return position == getCount() - 1;
        }

        private boolean isClickable(int position)
        {
            return isLast(position);
        }

        @Override public Object instantiateItem(ViewGroup container, int position)
        {
            ImageView imageView = (ImageView) LayoutInflater.from(GuideActivity.this).inflate(R.layout.guide_layout, null);
            try
            {
                imageView.setBackgroundResource(drawableIdList.get(position));
            }
            catch (OutOfMemoryError e)
            {
                Timber.e(e, "Expanding position %d", position);
            }
            if (isClickable(position))
            {
                imageView.setOnClickListener(GuideActivity.this);
            }
            else
            {
                imageView.setOnClickListener(null);
            }
            container.addView(imageView);
            return imageView;
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((View) object);
            if (object instanceof ImageView)
            {
                ((ImageView) object).setBackgroundResource(0);
            }
        }

        @Override public boolean isViewFromObject(View arg0, Object arg1)
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
