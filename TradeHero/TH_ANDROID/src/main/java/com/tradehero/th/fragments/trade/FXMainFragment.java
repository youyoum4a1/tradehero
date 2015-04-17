package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOUtil;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionFragment;
import com.tradehero.th.fragments.position.SecurityPositionListFragment;
import com.tradehero.th.models.number.THSignedFXRate;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.THColorUtils;
import javax.inject.Inject;
import rx.Observer;
import timber.log.Timber;

@Routable({
        "securityFx/:securityRawInfo",
        "fxSecurity/:exchange/:symbol"
})
//TODO need refactor by alex
public class FXMainFragment extends BuySellFragment
{
    private final static String BUNDLE_KEY_CLOSE_UNITS_BUNDLE = FXMainFragment.class.getName() + ".units";
    private final static long MILLISECOND_FX_QUOTE_REFRESH = 5000;
    @ColorRes private static final int DEFAULT_BUTTON_TEXT_COLOR = R.color.text_primary_inverse;

    @InjectView(R.id.pager) ViewPager tabViewPager;
    @InjectView(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;

    @Inject CurrentUserId currentUserId;

    private int closeUnits;
    private QuoteDTO oldQuoteDTO;
    private BuySellBottomFXPagerAdapter buySellBottomFXPagerAdapter;
    @Nullable private Observer<PortfolioCompactDTO> portfolioCompactDTOObserver;

    @RouteProperty("exchange") String exchange;
    @RouteProperty("symbol") String symbol;

    private static int getCloseAttribute(@NonNull Bundle args)
    {
        return args.getInt(BUNDLE_KEY_CLOSE_UNITS_BUNDLE, 0);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (securityId == null)
        {
            securityId = new SecurityId(exchange, symbol);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_fx_main, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        closeUnits = getCloseAttribute(getArguments());

        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
    }

    @Nullable @Override protected PortfolioCompactDTO getPreferredApplicablePortfolio(@NonNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        return portfolioCompactDTOs.getDefaultFxPortfolio();
    }

    private void showCloseDialog()
    {
        if (closeUnits != 0
                && quoteDTO != null
                && securityCompactDTO != null)
        {
            isTransactionTypeBuy = closeUnits < 0;
            showBuySellDialog(Math.abs(closeUnits));
            closeUnits = 0;
        }
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
        this.oldQuoteDTO = null;
        buySellBottomFXPagerAdapter = null;
    }

    @Override protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId, boolean andDisplay)
    {
        super.linkWithApplicable(purchaseApplicablePortfolioId, andDisplay);
        if (buySellBottomFXPagerAdapter == null)
        {
            buySellBottomFXPagerAdapter = new BuySellBottomFXPagerAdapter(
                    this.getChildFragmentManager(),
                    getActivity(),
                    purchaseApplicablePortfolioId,
                    securityId,
                    currentUserId.toUserBaseKey());
            tabViewPager.setAdapter(buySellBottomFXPagerAdapter);
            if (!Constants.RELEASE)
            {
                tabViewPager.setOffscreenPageLimit(0);
            }
            pagerSlidingTabStrip.setViewPager(tabViewPager);
            pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
            {
                @Override public void onPageScrolled(int i, float v, int i2)
                {

                }

                @Override public void onPageSelected(int i)
                {
                    mSelectedPortfolioContainer.setVisibility(i == 0 ? View.VISIBLE : View.GONE);
                }

                @Override public void onPageScrollStateChanged(int i)
                {

                }
            });
        }
    }

    @Override protected void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        MenuOwnedPortfolioId currentMenu = mSelectedPortfolioContainer.getCurrentMenu();
        if (currentMenu != null && portfolioCompactDTO.id == currentMenu.portfolioId)
        {
            this.portfolioCompactDTO = portfolioCompactDTO;
            if (portfolioCompactDTOObserver != null)
            {
                portfolioCompactDTOObserver.onNext(portfolioCompactDTO);
            }
        }
        super.linkWith(portfolioCompactDTO);
    }

    @Override protected long getMillisecondQuoteRefresh()
    {
        return MILLISECOND_FX_QUOTE_REFRESH;
    }

    //<editor-fold desc="Display Methods"> //hide switch portfolios for temp
    @Override public void displayStockName()
    {
        super.displayStockName();
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            FxPairSecurityId fxPairSecurityId = ((FxSecurityCompactDTO) securityCompactDTO).getFxPair();
            setActionBarTitle(String.format("%s/%s", fxPairSecurityId.left, fxPairSecurityId.right));
            setActionBarSubtitle(null);
        }
    }

    @Override public void displayBuySellPrice()
    {
        if (mBuyBtn != null && mSellBtn != null && quoteDTO != null)
        {
            int precision = 0;
            if (quoteDTO.ask != null && quoteDTO.bid != null)
            {
                precision = SecurityCompactDTOUtil.getExpectedPrecision(quoteDTO.ask, quoteDTO.bid);
            }

            if (quoteDTO.ask == null)
            {
                mBuyBtn.setText(R.string.buy_sell_ask_price_not_available);
            }
            else
            {
                double diff = (oldQuoteDTO != null && oldQuoteDTO.ask != null) ? quoteDTO.ask - oldQuoteDTO.ask : 0.0;
                formatButtonText(quoteDTO.ask, diff, precision, mBuyBtn, getString(R.string.fx_buy));
            }

            if (quoteDTO.bid == null)
            {
                mSellBtn.setText(R.string.buy_sell_bid_price_not_available);
            }
            else
            {
                double diff = (oldQuoteDTO != null && oldQuoteDTO.bid != null) ? quoteDTO.bid - oldQuoteDTO.bid : 0.0;
                formatButtonText(quoteDTO.bid, diff, precision, mSellBtn, getString(R.string.fx_sell));
            }
        }
    }

    protected void formatButtonText(double value, double diff, int precision, Button btn, String format)
    {
        THSignedFXRate.builder(value)
                .signTypeArrow()
                .withSignValue(diff)
                .enhanceTo((int) (btn.getTextSize() + 15))
                .enhanceWithColor(THColorUtils.getColorResourceIdForNumber(diff, DEFAULT_BUTTON_TEXT_COLOR))
                .withValueColor(DEFAULT_BUTTON_TEXT_COLOR)
                .relevantDigitCount(SecurityCompactDTOUtil.DEFAULT_RELEVANT_DIGITS)
                .expectedPrecision(precision)
                .withDefaultColor()
                .withFallbackColor(R.color.text_primary_inverse)
                .format(format)
                .build()
                .into(btn);
    }

    @Override public boolean isBuySellReady()
    {
        return quoteDTO != null && positionDTOList != null && applicableOwnedPortfolioIds != null;
    }

    @Override public void displayBuySellContainer()
    {
        if (isBuySellReady() && mBuySellBtnContainer.getVisibility() == View.GONE && tabViewPager.getCurrentItem() == 0)
        {
            Animation slideIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_bottom);
            slideIn.setFillAfter(true);
            mBuySellBtnContainer.setVisibility(View.VISIBLE);
            mBuySellBtnContainer.startAnimation(slideIn);
        }
    }

    @Override protected boolean getSupportSell()
    {
        return true;
    }
    //</editor-fold>

    protected void linkWith(@NonNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        PortfolioCompactDTO defaultFxPortfolio = portfolioCompactDTOs.getDefaultFxPortfolio();
        if (defaultFxPortfolio != null)
        {
            mSelectedPortfolioContainer.addMenuOwnedPortfolioId(new MenuOwnedPortfolioId(currentUserId.toUserBaseKey(), defaultFxPortfolio));
        }
        setInitialSellQuantityIfCan();
        showCloseDialog();
    }

    @Override
    protected void conditionalDisplayPortfolioChanged(boolean isPortfolioChanged)
    {
    }

    @Override public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        super.linkWith(securityCompactDTO, andDisplay);
        showCloseDialog();
    }

    @Override protected void linkWith(QuoteDTO quoteDTO)
    {
        this.oldQuoteDTO = this.quoteDTO;
        super.linkWith(quoteDTO);
        showCloseDialog();
    }

    private class BuySellBottomFXPagerAdapter extends FragmentPagerAdapter
    {
        public static final int FRAGMENT_ID_INFO = 0;
        public static final int FRAGMENT_ID_DISCUSS = 1;
        public static final int FRAGMENT_ID_HISTORY = 2;

        @NonNull private final Context context;
        @NonNull private final OwnedPortfolioId applicablePortfolioId;
        @NonNull private final SecurityId securityId;
        @NonNull private final UserBaseKey shownUser;

        public BuySellBottomFXPagerAdapter(
                @NonNull FragmentManager fragmentManager,
                @NonNull Context context,
                @NonNull OwnedPortfolioId applicablePortfolioId,
                @NonNull SecurityId securityId,
                @NonNull UserBaseKey shownUser)
        {
            super(fragmentManager);
            this.context = context;
            this.applicablePortfolioId = applicablePortfolioId;
            this.securityId = securityId;
            this.shownUser = shownUser;
        }

        @Override public Fragment getItem(int position)
        {
            Fragment fragment;
            Bundle args = new Bundle();
            ActionBarOwnerMixin.putKeyShowHomeAsUp(args, true);

            switch (position)
            {
                case FRAGMENT_ID_INFO:
                    fragment = new FXInfoFragment();
                    portfolioCompactDTOObserver = ((FXInfoFragment) fragment).getPortfolioObserver();
                    FXInfoFragment.putSecurityId(args, securityId);
                    FXInfoFragment.putApplicablePortfolioId(args, applicablePortfolioId);
                    break;

                case FRAGMENT_ID_DISCUSS:
                    fragment = new SecurityDiscussionFragment();
                    SecurityDiscussionFragment.setHasOptionMenu(args, false);
                    SecurityDiscussionFragment.putSecurityId(args, securityId);
                    break;

                case FRAGMENT_ID_HISTORY:
                    fragment = new SecurityPositionListFragment();
                    SecurityPositionListFragment.setHasOptionMenu(args, false);
                    SecurityPositionListFragment.putShownUser(args, shownUser);
                    SecurityPositionListFragment.putSecurityId(args, securityId);
                    SecurityPositionListFragment.putApplicablePortfolioId(args, applicablePortfolioId);
                    break;

                default:
                    Timber.w("Not supported index " + position);
                    throw new UnsupportedOperationException("Not implemented");
            }
            fragment.setArguments(args);
            fragment.setRetainInstance(false);
            return fragment;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case FRAGMENT_ID_INFO:
                    return context.getString(R.string.security_info);
                case FRAGMENT_ID_DISCUSS:
                    return context.getString(R.string.discovery_discussions);
                case FRAGMENT_ID_HISTORY:
                    return context.getString(R.string.security_history);
            }
            return super.getPageTitle(position);
        }

        @Override public int getCount()
        {
            return 3;
        }
    }
}
