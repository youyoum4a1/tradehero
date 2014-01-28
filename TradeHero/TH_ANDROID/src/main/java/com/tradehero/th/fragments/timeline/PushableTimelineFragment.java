package com.tradehero.th.fragments.timeline;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.fragments.billing.THIABUserInteractor;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import javax.inject.Inject;
import retrofit.client.Response;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 10/23/13 Time: 5:41 PM To change this template use File | Settings | File Templates.
 *
 * This fragment will not be the main, but one that is pushed from elsewhere
 */
public class PushableTimelineFragment extends TimelineFragment
{
    public static final String TAG = PushableTimelineFragment.class.getSimpleName();

    private ActionBar actionBar;
    private MenuItem btnFollow;
    private MenuItem followingStamp;
    @Inject protected HeroAlertDialogUtil heroAlertDialogUtil;

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.timeline_menu_pushable_other, menu);
        this.actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        //super.onCreateOptionsMenu(menu, inflater);
        btnFollow = menu.findItem(R.id.btn_follow_this_user);
        followingStamp = menu.findItem(R.id.ic_following);
        displayActionBarTitle();
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        Boolean isFollowing = isPurchaserFollowingUserShown();
        if (btnFollow != null)
        {
            btnFollow.setVisible(isFollowing != null && !isFollowing);
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
        this.actionBar = null;
        this.btnFollow = null;
        this.followingStamp = null;
        super.onDestroyOptionsMenu();
    }

    @Override protected void createUserInteractor()
    {
        userInteractor = new PushableTimelineTHIABUserInteractor(getActivity(), getBillingActor(), getView().getHandler());
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

    public void displayActionBarTitle()
    {
        if (actionBar != null)
        {
            if (shownProfile != null)
            {
                actionBar.setTitle(UserBaseDTOUtil.getLongDisplayName(getActivity(), shownProfile));
            }
            else
            {
                actionBar.setTitle(R.string.loading_loading);
            }
        }
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

        public PushableTimelineTHIABUserInteractor(Activity activity, THIABActor billingActor, Handler handler)
        {
            super(activity, billingActor, handler);
        }

        @Override protected void handleShowSkuDetailsMilestoneComplete()
        {
            super.handleShowSkuDetailsMilestoneComplete();
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
