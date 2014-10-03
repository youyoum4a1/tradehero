package com.tradehero.th.fragments.settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.R;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.staff.StaffDTO;
import com.tradehero.th.models.staff.StaffDTOFactory;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import javax.inject.Inject;

public class AboutFragment extends DashboardFragment
{
    @InjectView(R.id.main_content_wrapper) View mainContentWrapper;
    @InjectView(R.id.staff_list_holder) LinearLayout staffList;
    @InjectView(R.id.about_scroll) ScrollView scrollView;

    @Inject Analytics analytics;
    @Inject StaffDTOFactory staffDTOFactory;
    @Inject DashboardNavigator navigator;
    @Inject @BottomTabs DashboardTabHost dashboardTabHost;

    private ObjectAnimator rotateAnimator;
    private ObjectAnimator scrollAnimator;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.inject(this, view);
        mainContentWrapper.setPadding(mainContentWrapper.getPaddingLeft(), mainContentWrapper.getPaddingTop(), mainContentWrapper.getPaddingRight(), container.getMeasuredHeight());
        initStaffList();
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        scrollView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        PropertyValuesHolder pvRtX = PropertyValuesHolder.ofFloat(View.ROTATION_X, 0f, 30f);
        rotateAnimator = ObjectAnimator.ofPropertyValuesHolder(scrollView, pvRtX);
        rotateAnimator.setDuration(getResources().getInteger(R.integer.about_screen_rotation_duration));
        rotateAnimator.addListener(new AnimatorListenerAdapter()
        {
            @Override public void onAnimationStart(Animator animation)
            {
                super.onAnimationStart(animation);
            }

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
        getActivity().getActionBar().setTitle(getResources().getString(R.string.settings_about_title));
        getActivity().getActionBar().hide();
    }

    @Override public void onDestroyOptionsMenu()
    {
        FragmentActivity activity = getActivity();
        if (activity != null)
        {
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null)
            {
                actionBar.show();
            }
        }
        super.onDestroyOptionsMenu();
    }

    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Settings_About));
        dashboardTabHost.animateHide();
    }

    @Override public void onPause()
    {
        dashboardTabHost.animateShow();
        super.onPause();
    }

    private void scrollToBottom()
    {
        if(scrollView != null)
        {
            scrollAnimator = ObjectAnimator.ofInt(scrollView, "scrollY", 0, staffList.getBottom());
            scrollAnimator.setInterpolator(new LinearInterpolator());
            scrollAnimator.addListener(new AnimatorListenerAdapter()
            {
                @Override public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    navigator.popFragment();
                }
            });
            scrollAnimator.setDuration(getResources().getInteger(R.integer.about_screen_scroll_duration));
            scrollAnimator.start();
        }
    }

    @Override public void onDestroyView()
    {
        if(rotateAnimator != null)
        {
            rotateAnimator.removeAllListeners();
            rotateAnimator = null;
        }
        if(scrollAnimator != null)
        {
            scrollAnimator.removeAllListeners();
            scrollAnimator = null;
        }
        super.onDestroyView();
    }
}
