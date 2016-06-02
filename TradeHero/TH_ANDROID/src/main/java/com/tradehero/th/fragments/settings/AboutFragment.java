package com.tradehero.th.fragments.settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.models.staff.StaffDTO;
import com.tradehero.th.models.staff.StaffDTOFactory;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutFragment extends BaseFragment
{
    @Bind(R.id.main_content_wrapper) View mainContentWrapper;
    @Bind(R.id.staff_list_holder) LinearLayout staffList;
    @Bind(R.id.about_scroll) ScrollView scrollView;

    //TODO Change Analytics
    //@Inject Analytics analytics;

    private ObjectAnimator rotateAnimator;
    private ObjectAnimator scrollAnimator;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
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
        for (StaffDTO staffDTO : StaffDTOFactory.getTradeHeroStaffers(getResources()))
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
        hideSupportActionBar();
    }

    @Override public void onDestroyOptionsMenu()
    {
        showSupportActionBar();
        super.onDestroyOptionsMenu();
    }

    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();
        //TODO Change Analytics
        //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Settings_About));
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
