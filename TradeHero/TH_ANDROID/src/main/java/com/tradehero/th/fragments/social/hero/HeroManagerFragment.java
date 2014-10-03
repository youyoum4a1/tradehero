package com.tradehero.th.fragments.social.hero;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.R;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.THBasePurchaseActionInteractor;
import com.tradehero.th.billing.THPurchaseReporter;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.models.social.follower.AllHeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.FreeHeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.models.social.follower.PremiumHeroTypeResourceDTO;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.widget.THTabView;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
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
    @Inject protected GraphicUtil graphicUtil;
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

    @Override protected FollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
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

        List<HeroTypeResourceDTO> resourceDTOs = heroTypeResourceDTOFactory.getListOfHeroType();
        tabSpecList = new ArrayList<>(resourceDTOs.size());
        for (HeroTypeResourceDTO resourceDTO : resourceDTOs)
        {
            addTab(resourceDTO);
        }
        mTabHost.getTabWidget().setBackgroundColor(Color.WHITE);
        return mTabHost;
    }

    private void addTab(HeroTypeResourceDTO resourceDTO)
    {
        Bundle args = new Bundle();
        HeroesTabContentFragment.putFollowerId(args, getFollowerId(getArguments()));

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

    @Override protected THBasePurchaseActionInteractor.Builder createPurchaseActionInteractorBuilder()
    {
        return super.createPurchaseActionInteractorBuilder()
                .setPurchaseReportedListener(new HeroManagerOnPurchaseReportedListener());
    }

    private boolean isCurrentUser()
    {
        UserBaseKey followerId = getFollowerId(getArguments());
        if (followerId != null && followerId.key != null && currentUserId != null)
        {
            return (followerId.key.intValue() == currentUserId.toUserBaseKey().key.intValue());
        }
        return false;
    }

    private int getTitle()
    {
        if (isCurrentUser())
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
    private void changeTabTitle(HeroTypeResourceDTO resourceDTO, int number)
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

    @Override public void onHerosLoaded(HeroTypeResourceDTO resourceDTO, HeroDTOExtWrapper value)
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

    protected class HeroManagerPremiumUserFollowedListener
            implements FollowUserAssistant.OnUserFollowedListener
    {
        @Override public void onUserFollowSuccess(
                @NotNull UserBaseKey userFollowed,
                @NotNull UserProfileDTO currentUserProfileDTO)
        {
            // TODO
        }

        @Override public void onUserFollowFailed(@NotNull UserBaseKey userFollowed, @NotNull Throwable error)
        {
            // nothing for now
        }
    }

    protected class HeroManagerOnPurchaseReportedListener
            implements THPurchaseReporter.OnPurchaseReportedListener
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

