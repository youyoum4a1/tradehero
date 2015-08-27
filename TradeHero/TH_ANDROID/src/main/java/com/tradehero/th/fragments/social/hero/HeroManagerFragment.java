package com.tradehero.th.fragments.social.hero;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.social.follower.AllHeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.THTabView;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

@Routable({
        "user/me/heroes",
        "user/me/heroes/tab-index/:tabIndexMe",
        "user/id/:followerId/heroes",
        "user/id/:followerIdWithTab/heroes/tab-index/:tabIndex",
        "user/id/:followerIdForAll/heroes/all",
        "user/id/:followerIdForFree/heroes/free",
        "user/id/:followerIdForPremium/heroes/premium",
})
public class HeroManagerFragment extends DashboardFragment
        implements OnHeroesLoadedListener
{
    private static final String BUNDLE_KEY_FOLLOWER_ID = HeroManagerFragment.class.getName() + ".followerId";

    // TODO change it into something like R.id.... to help with identifying its unicity
    static final int FRAGMENT_LAYOUT_ID = 9999;

    @Inject THRouter router;
    @Inject CurrentUserId currentUserId;

    @RouteProperty("tabIndexMe") Integer routedTabIndexMe;
    @RouteProperty("followerId") Integer routedFollowerId;
    @RouteProperty("followerIdWithTab") Integer routedFollowerIdWithTab;
    @RouteProperty("followerIdForAll") Integer routedFollowerIdForAll;
    @RouteProperty("followerIdForFree") Integer routedFollowerIdForFree;
    @RouteProperty("followerIdForPremium") Integer routedFollowerIdForPremium;
    @RouteProperty("tabIndex") Integer routedTabIndex;

    UserBaseKey followerId;
    FragmentTabHost mTabHost;
    List<TabHost.TabSpec> tabSpecList;

    public static void putFollowerId(@NonNull Bundle args, @NonNull UserBaseKey followerId)
    {
        args.putBundle(BUNDLE_KEY_FOLLOWER_ID, followerId.getArgs());
    }

    @Nullable public static UserBaseKey getFollowerId(@NonNull Bundle args)
    {
        Bundle followerBundle = args.getBundle(BUNDLE_KEY_FOLLOWER_ID);
        if (followerBundle != null)
        {
            return new UserBaseKey(followerBundle);
        }
        return null;
    }

    public static void registerAliases(@NonNull THRouter router)
    {
        router.registerAlias("user/me/heroes/all", "user/me/heroes/tab-index/" + new AllHeroTypeResourceDTO().heroTabIndex);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        router.inject(this);
        UserBaseKey followerId = getFollowerId(getArguments());
        if (followerId == null)
        {
            if (routedFollowerId != null)
            {
                followerId = new UserBaseKey(routedFollowerId);
            }
            else if (routedFollowerIdWithTab != null)
            {
                followerId = new UserBaseKey(routedFollowerIdWithTab);
            }
            else if (routedFollowerIdForAll != null)
            {
                followerId = new UserBaseKey(routedFollowerIdForAll);
            }
            else if (routedFollowerIdForFree != null)
            {
                followerId = new UserBaseKey(routedFollowerIdForFree);
            }
            else if (routedFollowerIdForPremium != null)
            {
                followerId = new UserBaseKey(routedFollowerIdForPremium);
            }
            else
            {
                followerId = currentUserId.toUserBaseKey();
            }
        }
        this.followerId = followerId;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), FRAGMENT_LAYOUT_ID);

        List<HeroTypeResourceDTO> resourceDTOs = HeroTypeResourceDTOFactory.getListOfHeroType();
        tabSpecList = new ArrayList<>(resourceDTOs.size());
        for (HeroTypeResourceDTO resourceDTO : resourceDTOs)
        {
            addTab(resourceDTO);
        }
        GraphicUtil.setBackgroundColorFromAttribute(mTabHost.getTabWidget(), R.attr.slidingTabBackground);
        if (routedTabIndex != null)
        {
            mTabHost.setCurrentTab(routedTabIndex);
            routedTabIndex = null;
        }
        else if (routedTabIndexMe != null)
        {
            mTabHost.setCurrentTab(routedTabIndexMe);
            routedTabIndexMe = null;
        }
        else if (routedFollowerIdForAll != null)
        {
            mTabHost.setCurrentTab(new AllHeroTypeResourceDTO().heroTabIndex);
        }
        else if (routedFollowerIdForFree != null)
        {
            //mTabHost.setCurrentTab(new FreeHeroTypeResourceDTO().heroTabIndex);
        }
        else if (routedFollowerIdForPremium != null)
        {
            //mTabHost.setCurrentTab(new PremiumHeroTypeResourceDTO().heroTabIndex);
        }
        return mTabHost;
    }

    private void addTab(@NonNull HeroTypeResourceDTO resourceDTO)
    {
        Bundle args = new Bundle();
        HeroesTabContentFragment.putFollowerId(args, followerId);

        String title = MessageFormat.format(getString(resourceDTO.heroTabTitleRes), 0);

        THTabView tabIndicator =
                (THTabView) LayoutInflater.from(getActivity()).inflate(R.layout.th_tab_indicator, mTabHost.getTabWidget(), false);
        tabIndicator.setTitle(title);

        TabHost.TabSpec tabSpec = mTabHost.newTabSpec(title).setIndicator(tabIndicator);
        tabSpecList.add(tabSpec);
        mTabHost.addTab(tabSpec, resourceDTO.heroContentFragmentClass, args);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(getTitle());
    }

    private int getTitle()
    {
        if (followerId.equals(currentUserId.toUserBaseKey()))
        {
            return R.string.manage_my_heroes_title;
        }
        else
        {
            return R.string.manage_heroes_title;
        }
    }

    /**
     * change the number of tab
     */
    private void changeTabTitle(@NonNull HeroTypeResourceDTO resourceDTO, int number)
    {
        if (mTabHost == null)
        {
            return;
        }
        int titleRes = resourceDTO.heroTabTitleRes;
        String title = MessageFormat.format(getString(titleRes), number);

        THTabView tv = (THTabView) mTabHost.getTabWidget()
                .getChildAt(resourceDTO.heroTabIndex);

        tv.setTitle(title);
    }

    @Override public void onHeroesLoaded(HeroTypeResourceDTO resourceDTO, HeroDTOExtWrapper value)
    {
        if (!isDetached())
        {
            int premiumCount = value.getActivePremiumHeroesCount();
            int freeCount = value.getActiveFreeHeroesCount();
            changeTabTitle(new AllHeroTypeResourceDTO(), premiumCount + freeCount);
        }
    }
}

