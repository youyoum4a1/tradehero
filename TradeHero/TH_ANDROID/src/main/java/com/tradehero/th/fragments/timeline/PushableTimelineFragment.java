package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import javax.inject.Inject;

/**
 * This fragment will not be the main, but one that is pushed from elsewhere
 */
@Routable({
        "user/:userId",
        "user/:heroIdFree/follow/free",
        "user/:heroIdPremium/follow/premium",
})
public class PushableTimelineFragment extends TimelineFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @RouteProperty(ROUTER_HERO_ID_FREE) Integer freeFollowHeroId;
    @RouteProperty(ROUTER_HERO_ID_PREMIUM) Integer premiumFollowHeroId;

    public static final String ROUTER_HERO_ID_FREE = "heroIdFree";
    public static final String ROUTER_HERO_ID_PREMIUM = "heroIdPremium";

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (freeFollowHeroId != null)
        {
            shownUserBaseKey = new UserBaseKey(freeFollowHeroId);
        }
        else if (premiumFollowHeroId != null)
        {
            shownUserBaseKey = new UserBaseKey(premiumFollowHeroId);
        }
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mIsOtherProfile = true;
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        updateBottomButton();
        super.onPrepareOptionsMenu(menu);
    }

    @Override public void onResume()
    {
        super.onResume();

        if (freeFollowHeroId != null)
        {
            freeFollow(shownUserBaseKey);
            freeFollowHeroId = null;
        }
        else if (premiumFollowHeroId != null)
        {
            handleFollowRequested();
            premiumFollowHeroId = null;
        }
    }

    @Override protected void linkWith(@NonNull UserProfileDTO userProfileDTO)
    {
        super.linkWith(userProfileDTO);
        displayActionBarTitle();
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
