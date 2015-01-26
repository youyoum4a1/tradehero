package com.tradehero.th.fragments.settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.R;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.staff.StaffDTO;
import com.tradehero.th.models.staff.StaffDTOFactory;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;

public class AboutFragment extends DashboardFragment
{
    @InjectView(R.id.main_content_wrapper) View mainContentWrapper;
    @InjectView(R.id.staff_list_holder) LinearLayout staffList;
    @InjectView(R.id.about_scroll) ScrollView scrollView;

    @Inject Analytics analytics;
    @Inject StaffDTOFactory staffDTOFactory;
    @Inject @BottomTabs Lazy<DashboardTabHost> dashboardTabHost;

    private ObjectAnimator rotateAnimator;
    private ObjectAnimator scrollAnimator;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.inject(this, view);
        mainContentWrapper.setPadding(mainContentWrapper.getPaddingLeft(),
                mainContentWrapper.getPaddingTop(),
                mainContentWrapper.getPaddingRight(),
                container.getMeasuredHeight());
        initStaffList();
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        scrollView.setOnTouchListener((v, event) -> true);

        PropertyValuesHolder pvRtX = PropertyValuesHolder.ofFloat(View.ROTATION_X, 0f, 30f);
        rotateAnimator = ObjectAnimator.ofPropertyValuesHolder(scrollView, pvRtX);
        rotateAnimator.setDuration(getResources().getInteger(R.integer.about_screen_rotation_duration));
        rotateAnimator.addListener(new AnimatorListenerAdapter()
        {
            @Override public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                scrollToBottom();
            }
        });
        rotateAnimator.setStartDelay(getResources().getInteger(R.integer.about_screen_rotation_delay));
        rotateAnimator.start();
    }

    private void initStaffList()
    {
        staffList.removeAllViews();
        for (StaffDTO staffDTO : staffDTOFactory.getTradeHeroStaffers(getResources()))
        {
            StaffTitleView staffTitleView = (StaffTitleView) getActivity().getLayoutInflater().inflate(R.layout.staff_view, null);
            staffTitleView.setStaffDTO(staffDTO);
            staffList.addView(staffTitleView);
        }
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(getResources().getString(R.string.settings_about_title));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.hide();
        }
    }

    @Override public void onDestroyOptionsMenu()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.show();
        }
        super.onDestroyOptionsMenu();
    }

    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Settings_About));
        dashboardTabHost.get().animateHide();
    }

    @Override public void onPause()
    {
        dashboardTabHost.get().animateShow();
        super.onPause();
    }

    private void scrollToBottom()
    {
        if (scrollView != null)
        {
            scrollAnimator = ObjectAnimator.ofInt(scrollView, "scrollY", 0, staffList.getBottom());
            scrollAnimator.setInterpolator(new LinearInterpolator());
            scrollAnimator.addListener(new AnimatorListenerAdapter()
            {
                @Override public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    navigator.get().popFragment();
                }
            });
            scrollAnimator.setDuration(getResources().getInteger(R.integer.about_screen_scroll_duration));
            scrollAnimator.start();
        }
    }

    @Override public void onDestroyView()
    {
        scrollView.setOnTouchListener(null);
        if (rotateAnimator != null)
        {
            rotateAnimator.removeAllListeners();
            if (rotateAnimator.isRunning())
            {
                rotateAnimator.cancel();
            }
            rotateAnimator = null;
        }
        if (scrollAnimator != null)
        {
            scrollAnimator.removeAllListeners();
            if (scrollAnimator.isRunning())
            {
                scrollAnimator.cancel();
            }
            scrollAnimator = null;
        }
        super.onDestroyView();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (scrollView != null)
        {
            scrollView.setOnTouchListener(null);
        }
        if (rotateAnimator != null)
        {
            rotateAnimator.removeAllListeners();
            rotateAnimator = null;
        }
        if (scrollAnimator != null)
        {
            scrollAnimator.removeAllListeners();
            scrollAnimator = null;
        }
    }
}
