package com.tradehero.th.fragments.timeline;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import javax.inject.Inject;
import android.support.annotation.NonNull;

/**
 * This fragment will not be the main, but one that is pushed from elsewhere
 */
@Routable({
        "user/:userId"
})
public class PushableTimelineFragment extends TimelineFragment
{
    @Inject HeroAlertDialogUtil heroAlertDialogUtil;

    @Override protected void initViews(View view)
    {
        super.initViews(view);
        mIsOtherProfile = true;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.timeline_menu_pushable_other, menu);
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
                    UserProfileDTO purchaserProfile = userProfileCache.get().getValue(purchaserKey);
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
                premiumFollowUser(shownUserBaseKey);
            }
        });
    }

    @Override public <T extends Fragment> boolean allowNavigateTo(@NonNull Class<T> fragmentClass, Bundle args)
    {
        return !(
                ((Object) this).getClass().isAssignableFrom(fragmentClass) &&
                    shownUserBaseKey != null &&
                    shownUserBaseKey.equals(getUserBaseKey(args)))
                && super.allowNavigateTo(fragmentClass, args);
    }
}
