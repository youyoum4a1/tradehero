package com.tradehero.th.fragments.position;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.tradehero.route.InjectRoute;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.GetPositionsDTOKeyFactory;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;

/**
 * Created by liangyx on 2/27/15.
 */
public class TabbedPositionListFragment extends BasePurchaseManagerFragment
{
    private static final String BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE = TabbedPositionListFragment.class.getName() + ".showPositionDtoKey";
    private static final String BUNDLE_KEY_SHOWN_USER_ID_BUNDLE = TabbedPositionListFragment.class.getName() + ".userBaseKey";
    private static final String BUNDLE_KEY_IS_FX = TabbedPositionListFragment.class.getName() + "isFX";
    private static final String BUNDLE_KEY_PROVIDER_ID = TabbedPositionListFragment.class + ".providerId";

    @Inject THRouter thRouter;
    @InjectRoute UserBaseKey injectedUserBaseKey;
    @InjectRoute PortfolioId injectedPortfolioId;
    @InjectView(R.id.pager) ViewPager tabViewPager;
    @InjectView(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;

    @NonNull protected GetPositionsDTOKey getPositionsDTOKey;
    protected PortfolioDTO portfolioDTO;
    protected UserBaseKey shownUser;
    @Nullable protected UserProfileDTO userProfileDTO;

    boolean mIsFX;

    ProviderId mProviderID;

    /* STOCK_TYPES and STOCK_TYPE_TITLE_IDS should have the same size and in same order */
    private static int[] STOCK_TYPES = new int[]{
            PositionItemAdapter.VIEW_TYPE_OPEN_LONG,
            PositionItemAdapter.VIEW_TYPE_CLOSED,
    };

    private static int[] STOCK_TYPE_TITLE_IDS = {
            R.string.position_list_header_open_unsure,
            R.string.position_list_header_closed_unsure,
    };

    /* FX_TYPES and FX_TYPE_TITLE_IDS should have the same size and in same order */
    private static int[] FX_TYPES = new int[]{
            PositionItemAdapter.VIEW_TYPE_OPEN_LONG,
            PositionItemAdapter.VIEW_TYPE_OPEN_SHORT,
            PositionItemAdapter.VIEW_TYPE_CLOSED,
    };
    private static int[] FX_TYPE_TITLE_IDS = {
            R.string.position_list_header_open_long_unsure,
            R.string.position_list_header_open_short_unsure,
            R.string.position_list_header_closed_unsure,
    };

    public static void putGetPositionsDTOKey(@NonNull Bundle args, @NonNull GetPositionsDTOKey getPositionsDTOKey)
    {
        args.putBundle(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE, getPositionsDTOKey.getArgs());
    }

    public static void putShownUser(@NonNull Bundle args, @NonNull UserBaseKey shownUser)
    {
        args.putBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE, shownUser.getArgs());
    }

    public static void putIsFX(@NonNull Bundle args, AssetClass assetClass) {
        if (assetClass == null) {
            args.putBoolean(BUNDLE_KEY_IS_FX, false);
        }
        args.putBoolean(BUNDLE_KEY_IS_FX, assetClass == AssetClass.FX);
    }

    private boolean isFX(@NonNull Bundle args) {
        return args.getBoolean(BUNDLE_KEY_IS_FX, false);
    }


    @NonNull private UserBaseKey getUserBaseKey(@NonNull Bundle args)
    {
        return new UserBaseKey(args.getBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE));
    }

    @Nullable private GetPositionsDTOKey getGetPositionsDTOKey(@NonNull Bundle args)
    {
        return GetPositionsDTOKeyFactory.createFrom(args.getBundle(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE));
    }

    public static void putProviderId(Bundle args, ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    private ProviderId getProviderId(Bundle args)
    {
        Bundle bundle = args.getBundle(BUNDLE_KEY_PROVIDER_ID);
        if (bundle == null) {
            return null;
        }
        return new ProviderId(bundle);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        Bundle args = getArguments();
        if (args.containsKey(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE))
        {
            shownUser = getUserBaseKey(args);
        }
        else
        {
            shownUser = injectedUserBaseKey;
        }
        if (args.containsKey(BUNDLE_KEY_SHOW_POSITION_DTO_KEY_BUNDLE))
        {
            getPositionsDTOKey = getGetPositionsDTOKey(args);
        }
        else
        {
            getPositionsDTOKey = new OwnedPortfolioId(injectedUserBaseKey.key, injectedPortfolioId.key);
        }
        mIsFX = isFX(args);
        mProviderID = getProviderId(args);
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.tabbed_position_fragment, container, false);
        ButterKnife.inject(this, view);
        initViews();
        return view;
    }

    private void initViews()
    {
        tabViewPager.setAdapter(new TabbedPositionPageAdapter(getChildFragmentManager()));
        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabStrip.setViewPager(tabViewPager);
    }

    private class TabbedPositionPageAdapter extends FragmentPagerAdapter
    {
        public TabbedPositionPageAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = new Bundle();

            PositionListFragment.putApplicablePortfolioId(args, purchaseApplicableOwnedPortfolioId);
            PositionListFragment.putGetPositionsDTOKey(args, purchaseApplicableOwnedPortfolioId);
            PositionListFragment.putShownUser(args, purchaseApplicableOwnedPortfolioId.getUserBaseKey());
            int positionType;
            if (mIsFX) {
                positionType = FX_TYPES[position];
            } else {
                positionType = STOCK_TYPES[position];
            }
            PositionListFragment.putPositionType(args, positionType);
            if (mProviderID != null) {
                CompetitionLeaderboardPositionListFragment.putProviderId(args, mProviderID);
                return Fragment.instantiate(getActivity(), CompetitionLeaderboardPositionListFragment.class.getName(), args);
            }
            return Fragment.instantiate(getActivity(), PositionListFragment.class.getName(), args);
        }

        @Override public int getCount()
        {
            if (mIsFX) {
                return FX_TYPES.length;
            } else {
                return STOCK_TYPES.length;
            }

        }

        @Override public CharSequence getPageTitle(int position)
        {
            if (mIsFX) {
                return getString(FX_TYPE_TITLE_IDS[position]);
            } else {
                return getString(STOCK_TYPE_TITLE_IDS[position]);
            }
        }
    }
}
