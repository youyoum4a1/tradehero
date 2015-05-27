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

@Routable({
        "user/:" + PushableTimelineFragment.ROUTER_USER_ID,
        "user/:" + PushableTimelineFragment.ROUTER_HERO_ID_FREE + "/follow/free",
        "user/:" + PushableTimelineFragment.ROUTER_HERO_ID_PREMIUM + "/follow/premium",
})
public class PushableTimelineFragment extends TimelineFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @RouteProperty(ROUTER_USER_ID) Integer userId;
    @RouteProperty(ROUTER_HERO_ID_FREE) Integer freeFollowHeroId;
    @RouteProperty(ROUTER_HERO_ID_PREMIUM) Integer premiumFollowHeroId;

    public static final String ROUTER_USER_ID = "userId";
    public static final String ROUTER_HERO_ID_FREE = "heroIdFree";
    public static final String ROUTER_HERO_ID_PREMIUM = "heroIdPremium";

    @Nullable @Override protected UserBaseKey getShownUserBaseKey()
    {
        if (userId != null)
        {
            return new UserBaseKey(userId);
        }
        else if (freeFollowHeroId != null)
        {
            return new UserBaseKey(freeFollowHeroId);
        }
        else if (premiumFollowHeroId != null)
        {
            return new UserBaseKey(premiumFollowHeroId);
        }
        return super.getShownUserBaseKey();
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
