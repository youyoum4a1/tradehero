package com.tradehero.th.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.R;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class GuideActivity extends Activity
        implements
        ViewPager.OnPageChangeListener,
        View.OnClickListener
{
    private static final int CLOSE_IMAGE_ID = 0x88888;
    @Inject Lazy<THLocalyticsSession> localyticsSession;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.guide1);
        list.add(R.drawable.guide2);
        list.add(R.drawable.guide3);
        //list.add(R.drawable.guide4);
        //list.add(R.drawable.guide5);

        viewpager.setAdapter(new ListViewPagerAdapter(list));
        viewpager.setOnPageChangeListener(this);

        try
        {
            if (isInstallShortcut())
            {
                removeShortcut();
            }
            createShortcut();
        }
        catch (SecurityException e)
        {
            Timber.e(e, null);
        }

        localyticsSession.get().open();
        localyticsSession.get().tagScreen(LocalyticsConstants.Splash);
    }

    @Override protected void onPause()
    {
        localyticsSession.get().close();
        localyticsSession.get().upload();
        super.onPause();
    }

    private void createShortcut()
    {
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra("duplicate", false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.launcher);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, createLaunchIntent());
        sendBroadcast(intent);
    }

    private Intent createLaunchIntent()
    {
        Intent launchIntent = new Intent(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launchIntent.setComponent(new ComponentName(getPackageName(), SplashActivity.class.getName()));
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return launchIntent;
    }

    private void printShortcutName(Cursor c)
    {
        if (!BuildConfig.DEBUG)
        {
            return;
        }

        if (c == null || c.getCount() <= 0)
        {
            return;
        }

        String name = "title";
        c.moveToFirst();
        int index = c.getColumnIndex(name);
        if (index == -1)
        {
            return;
        }
        c.moveToPrevious();
        while (c.moveToNext())
        {
            String title = c.getString(index);
            Timber.d("Shortcut title:%s",title);
        }
    }

    private boolean isInstallShortcut()
    {
        String name = getString(R.string.app_name);
        boolean  isInstallShortcut = false;
        final ContentResolver cr = getContentResolver();
        String AUTHORITY = "com.android.launcher.settings";
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");

        Cursor c = cr.query(CONTENT_URI, new String[] { "title", "iconResource" },
                "title=?", new String[]{name}, null);

        if (c != null && c.getCount() > 0)
        {
            isInstallShortcut = true;
        }
        printShortcutName(c);

        if (c != null)
        {
            c.close();
        }

        if (isInstallShortcut)
        {
            return isInstallShortcut;
        }

        AUTHORITY = "com.android.launcher2.settings";
        CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        c = cr.query(CONTENT_URI, new String[] { "title", "iconResource" },
                "title=?", new String[]{name}, null);

        if (c != null && c.getCount() > 0) {
            isInstallShortcut = true;
        }
        printShortcutName(c);

        return isInstallShortcut;
    }

    private void removeShortcut()
    {

        Intent shortcutIntent = new Intent(getApplicationContext(), SplashActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));

        addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }

    @Override public void onClick(View v)
    {
        localyticsSession.get().tagEvent(LocalyticsConstants.SplashScreenCancel);
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
            View view;
            ImageView imageView = (ImageView) LayoutInflater.from(GuideActivity.this).inflate(R.layout.guide_layout, null);
            if (position == getCount() - 1) {
                RelativeLayout rl = new RelativeLayout(GuideActivity.this);
                rl.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                rl.addView(imageView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                ImageView closeIv = new ImageView(GuideActivity.this);
                closeIv.setId(CLOSE_IMAGE_ID);
                closeIv.setImageResource(R.drawable.cross_red);
                closeIv.setOnClickListener(GuideActivity.this);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT | RelativeLayout.ALIGN_PARENT_TOP);
                lp.rightMargin = (int)GuideActivity.this.getResources().getDimension(R.dimen.guide_close_margin);
                lp.topMargin = lp.rightMargin;
                rl.addView(closeIv, lp);

                view = rl;
            }
            else
            {
                view = imageView;
            }
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
            container.addView(view);
            localyticsSession.get().tagEventMethod(LocalyticsConstants.SplashScreen,
                    LocalyticsConstants.Screen + String.valueOf(position));
            return view;
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
