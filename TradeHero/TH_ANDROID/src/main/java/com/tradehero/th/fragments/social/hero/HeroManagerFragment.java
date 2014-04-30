package com.tradehero.th.fragments.social.hero;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.models.social.follower.AllHeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.FreeHeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.models.social.follower.PremiumHeroTypeResourceDTO;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import java.util.List;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.api.social.HeroIdExtWrapper;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.inject.Inject;
import timber.log.Timber;

public class HeroManagerFragment extends BasePurchaseManagerFragment
        implements OnHeroesLoadedListener
{
    /**
     * We are showing the heroes of this follower
     */
    private static final String BUNDLE_KEY_FOLLOWER_ID = HeroManagerFragment.class.getName() + ".followerId";

    // TODO change it into something like R.id.... to help with identifying its unicity
    static final int FRAGMENT_LAYOUT_ID = 9999;

    @Inject protected HeroTypeResourceDTOFactory heroTypeResourceDTOFactory;
    FragmentTabHost mTabHost;
    List<TabHost.TabSpec> tabSpecList;

    public static void putFollowerId(Bundle args, UserBaseKey followerId)
    {
        args.putBundle(BUNDLE_KEY_FOLLOWER_ID, followerId.getArgs());
    }

    public static UserBaseKey getFollowerId(Bundle args)
    {
        return new UserBaseKey(args.getBundle(BUNDLE_KEY_FOLLOWER_ID));
    }

    @Override protected PremiumFollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new HeroManagerPremiumUserFollowedListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView");
        return addTabs();
    }

    @Override protected void initViews(View view)
    {
        // Nothing to do
    }

    private View addTabs()
    {
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), ((Fragment) this).getChildFragmentManager(), FRAGMENT_LAYOUT_ID);
        mTabHost.setOnTabChangedListener(new HeroManagerOnTabChangeListener());

        List<HeroTypeResourceDTO> resourceDTOs = heroTypeResourceDTOFactory.getListOfHeroType();
        tabSpecList = new ArrayList<>(resourceDTOs.size());
        for (HeroTypeResourceDTO resourceDTO : resourceDTOs)
        {
            addTab(resourceDTO);
        }

        return mTabHost;
    }

    private void addTab(HeroTypeResourceDTO resourceDTO)
    {
        Bundle args = new Bundle();
        HeroesTabContentFragment.putFollowerId(args, getFollowerId(getArguments()));

        String title = MessageFormat.format(getString(resourceDTO.heroTabTitleRes), 0);

        TabHost.TabSpec tabSpec = mTabHost.newTabSpec(title).setIndicator(title);
        tabSpecList.add(tabSpec);
        mTabHost.addTab(tabSpec, resourceDTO.heroContentFragmentClass, args);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setTitle(R.string.social_heroes);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                //localyticsSession.tagEvent(LocalyticsConstants.Leaderboard_Back);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleBuyMoreClicked()
    {
        showProductDetailListForPurchase(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS);
    }

    @Override public THUIBillingRequest getShowProductDetailRequest(ProductIdentifierDomain domain)
    {
        THUIBillingRequest request = super.getShowProductDetailRequest(domain);
        request.purchaseReportedListener = new HeroManagerOnPurchaseReportedListener();
        return request;
    }

    /**
     * change the number of tab
     */
    private void changeTabTitle(HeroTypeResourceDTO resourceDTO, int number)
    {
        if (mTabHost == null)
        {
            return;
        }
        TabHost.TabSpec tabSpec = tabSpecList.get(resourceDTO.heroTabIndex);
        int titleRes = resourceDTO.heroTabTitleRes;
        String title = MessageFormat.format(getString(titleRes), number);
        tabSpec.setIndicator(title);

        TextView tv = (TextView) mTabHost.getTabWidget()
                .getChildAt(resourceDTO.heroTabIndex)
                .findViewById(android.R.id.title);
        tv.setText(title);
    }

    @Override public void onHerosLoaded(HeroTypeResourceDTO resourceDTO, HeroIdExtWrapper value)
    {
        if (!isDetached())
        {
            int premiumCount = value.getActivePremiumHeroesCount();
            int freeCount = value.getActiveFreeHeroesCount();
            changeTabTitle(new PremiumHeroTypeResourceDTO(), premiumCount);
            changeTabTitle(new FreeHeroTypeResourceDTO(), freeCount);
            changeTabTitle(new AllHeroTypeResourceDTO(), premiumCount + freeCount);
        }
    }

    private void handleFollowSuccess(UserProfileDTO currentUserProfileDTO)
    {
        // TODO
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    protected class HeroManagerOnTabChangeListener implements TabHost.OnTabChangeListener
    {
        @Override public void onTabChanged(String tabId)
        {
            Fragment fragment = getFragmentManager().findFragmentByTag(tabId);
            Fragment f = ((Fragment) HeroManagerFragment.this).getChildFragmentManager()
                    .findFragmentByTag(tabId);
        }
    }

    protected class HeroManagerPremiumUserFollowedListener
            extends BasePurchaseManagerPremiumUserFollowedListener
    {
        @Override public void onUserFollowSuccess(UserBaseKey userFollowed,
                UserProfileDTO currentUserProfileDTO)
        {
            super.onUserFollowSuccess(userFollowed, currentUserProfileDTO);
            handleFollowSuccess(currentUserProfileDTO);
        }
    }

    protected class HeroManagerOnPurchaseReportedListener
            implements PurchaseReporter.OnPurchaseReportedListener
    {
        @Override public void onPurchaseReported(int requestCode, ProductPurchase reportedPurchase,
                UserProfileDTO updatedUserPortfolio)
        {
            //display(updatedUserPortfolio);
        }

        @Override public void onPurchaseReportFailed(int requestCode,
                ProductPurchase reportedPurchase, BillingException error)
        {
            // Anything to report?
        }
    }
}


