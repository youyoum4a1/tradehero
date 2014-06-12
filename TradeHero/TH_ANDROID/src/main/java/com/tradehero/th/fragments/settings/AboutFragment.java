package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.staff.StaffDTO;
import com.tradehero.th.models.staff.StaffDTOFactory;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import javax.inject.Inject;

public class AboutFragment extends DashboardFragment
{
    @InjectView(R.id.main_content_wrapper) View mainContentWrapper;
    @InjectView(R.id.staff_list_holder) LinearLayout staffList;

    @Inject THLocalyticsSession localyticsSession;
    @Inject StaffDTOFactory staffDTOFactory;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.inject(this, view);

        initStaffList();
        return view;
    }

    private void initStaffList()
    {
        staffList.removeAllViews();
        for (StaffDTO staffDTO: staffDTOFactory.getTradeHeroStaffers(getResources()))
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
        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        getSherlockActivity().getSupportActionBar().setTitle(getResources().getString(R.string.settings_about_title));
        getSherlockActivity().getSupportActionBar().hide();
    }

    @Override public void onDestroyOptionsMenu()
    {
        getSherlockActivity().getSupportActionBar().show();
        super.onDestroyOptionsMenu();
    }

    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.Settings_About);

        AnimationSet set = new AnimationSet(false);

        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1.5f);
        set.addAnimation(translateAnimation);

        Rotate3dAnimation rotateAnimation = new Rotate3dAnimation(0f, 10f, 0f, 0f, 0f, 0f);
        rotateAnimation.setDuration(getResources().getInteger(R.integer.duration_shrink_inflate));
        set.addAnimation(rotateAnimation);

        set.setDuration(getResources().getInteger(R.integer.duration_shrink_inflate));
        set.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation)
            {
            }

            @Override public void onAnimationEnd(Animation animation)
            {
                getNavigator().popFragment();
            }

            @Override public void onAnimationRepeat(Animation animation)
            {
            }
        });
        set.setStartOffset(3000);

        mainContentWrapper.startAnimation(set);
    }

    @Override public void onDestroyView()
    {
        mainContentWrapper.setAnimation(null);
        super.onDestroyView();
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
