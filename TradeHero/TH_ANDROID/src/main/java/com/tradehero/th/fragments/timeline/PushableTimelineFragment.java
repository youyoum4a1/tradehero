package com.tradehero.th.fragments.timeline;

import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.localytics.android.LocalyticsSession;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABUserInteractor;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.utils.LocalyticsConstants;
import javax.inject.Inject;
import retrofit.client.Response;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 10/23/13 Time: 5:41 PM To change this template use File | Settings | File Templates.
 *
 * This fragment will not be the main, but one that is pushed from elsewhere
 */
public class PushableTimelineFragment extends TimelineFragment
{
    @Inject HeroAlertDialogUtil heroAlertDialogUtil;
    @Inject LocalyticsSession localyticsSession;

    private MenuItem menuFollow;
    private MenuItem followingStamp;
    private TextView followButton;

    @Override protected void createUserInteractor()
    {
        userInteractor = new PushableTimelineTHIABUserInteractor();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.timeline_menu_pushable_other, menu);
        this.actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        //super.onCreateOptionsMenu(menu, inflater);
        menuFollow = menu.findItem(R.id.btn_follow_this_user);
        followButton = (TextView) menuFollow.getActionView().findViewById(R.id.follow_button);
        if (followButton != null)
        {
            followButton.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    localyticsSession.tagEvent(LocalyticsConstants.Proﬁle_Follow);
                    handleInfoButtonPressed();
                }
            });
        }

        followingStamp = menu.findItem(R.id.ic_following);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        Boolean isFollowing = isPurchaserFollowingUserShown();
        if (menuFollow != null)
        {
            menuFollow.setVisible(isFollowing != null && !isFollowing);
        }
        if (followingStamp != null)
        {
            followingStamp.setVisible(isFollowing != null && isFollowing);
        }

        MenuItem settingsButton = menu.findItem(R.id.menu_settings);
        if (settingsButton != null)
        {
            settingsButton.setVisible(false);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_follow_this_user:
                handleInfoButtonPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyOptionsMenu()
    {
        this.menuFollow = null;
        this.followingStamp = null;
        super.onDestroyOptionsMenu();
    }

    @Override protected void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        super.linkWith(userProfileDTO, andDisplay);
        if (andDisplay)
        {
            displayActionBarTitle();
            displayFollowButton();
        }
    }

    /**
     * Null means unsure.
     * @return
     */
    protected Boolean isPurchaserFollowingUserShown()
    {
        if (userInteractor != null)
        {
            OwnedPortfolioId applicablePortfolioId = userInteractor.getApplicablePortfolioId();
            if (applicablePortfolioId != null)
            {
                UserBaseKey purchaserKey = applicablePortfolioId.getUserBaseKey();
                if (purchaserKey != null)
                {
                    UserProfileDTO purchaserProfile = userProfileCache.get(purchaserKey);
                    if (purchaserProfile != null)
                    {
                        return purchaserProfile.isFollowingUser(shownUserBaseKey);
                    }
                }
            }
        }
        return null;
    }

    public void displayFollowButton()
    {
        getActivity().supportInvalidateOptionsMenu();
    }

    private void handleInfoButtonPressed()
    {
        heroAlertDialogUtil.popAlertFollowHero(getActivity(), new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                userInteractor.followHero(shownUserBaseKey);
            }
        });
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    public class PushableTimelineTHIABUserInteractor extends THIABUserInteractor
    {
        public final String TAG = PushableTimelineTHIABUserInteractor.class.getName();

        public PushableTimelineTHIABUserInteractor()
        {
            super();
        }

        @Override protected void handleShowProductDetailsMilestoneComplete()
        {
            super.handleShowProductDetailsMilestoneComplete();
            displayFollowButton();
        }

        @Override protected void handlePurchaseReportSuccess(THIABPurchase reportedPurchase, UserProfileDTO updatedUserProfile)
        {
            super.handlePurchaseReportSuccess(reportedPurchase, updatedUserProfile);
            displayFollowButton();
        }

        @Override protected void createFollowCallback()
        {
            this.followCallback = new UserInteractorFollowHeroCallback(heroListCache.get(), userProfileCache.get())
            {
                @Override public void success(UserProfileDTO userProfileDTO, Response response)
                {
                    super.success(userProfileDTO, response);
                    displayFollowButton();
                }
            };
        }
    }
}
