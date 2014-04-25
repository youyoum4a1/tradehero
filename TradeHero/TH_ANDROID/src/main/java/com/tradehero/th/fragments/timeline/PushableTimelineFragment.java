package com.tradehero.th.fragments.timeline;

import android.content.DialogInterface;
import android.view.View;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.localytics.android.LocalyticsSession;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import javax.inject.Inject;

/**
 * This fragment will not be the main, but one that is pushed from elsewhere
 */
public class PushableTimelineFragment extends TimelineFragment
{
    @Inject HeroAlertDialogUtil heroAlertDialogUtil;
    @Inject LocalyticsSession localyticsSession;

    @Override protected void initViews(View view)
    {
        super.initViews(view);
        mIsOtherProfile = true;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.timeline_menu_pushable_other, menu);
        this.actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);

        //followingStamp = menu.findItem(R.id.ic_following);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        Boolean isFollowing = isPurchaserFollowingUserShown();
        updateBottomButton();

        //MenuItem settingsButton = menu.findItem(R.id.menu_settings);
        //if (settingsButton != null)
        //{
        //    settingsButton.setVisible(false);
        //}

        super.onPrepareOptionsMenu(menu);
    }

    @Override protected void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        super.linkWith(userProfileDTO, andDisplay);
        if (andDisplay)
        {
            displayActionBarTitle();
            //displayFollowButton();
        }
    }

    /**
     * Null means unsure.
     */
    protected Boolean isPurchaserFollowingUserShown()
    {
        if (userInteractor != null)
        {
            OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
            if (applicablePortfolioId != null)
            {
                UserBaseKey purchaserKey = applicablePortfolioId.getUserBaseKey();
                if (purchaserKey != null)
                {
                    UserProfileDTO purchaserProfile = userProfileCache.get().get(purchaserKey);
                    if (purchaserProfile != null)
                    {
                        return purchaserProfile.isFollowingUser(shownUserBaseKey);
                    }
                }
            }
        }
        return null;
    }

    private void handleInfoButtonPressed()
    {
        heroAlertDialogUtil.popAlertFollowHero(getActivity(), new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                followUser(shownUserBaseKey);
            }
        });
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
