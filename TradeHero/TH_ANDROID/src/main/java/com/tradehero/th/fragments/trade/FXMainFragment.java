package com.tradehero.th.fragments.trade;

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
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOUtil;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.position.OldPositionListFragment;
import com.tradehero.th.models.number.THSignedFXRate;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.THColorUtils;
import javax.inject.Inject;

@Routable("securityFx/:securityRawInfo")
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
    private DiscoveryPagerAdapter discoveryPagerAdapter;

    private static int getCloseAttribute(@NonNull Bundle args)
    {
        return args.getInt(BUNDLE_KEY_CLOSE_UNITS_BUNDLE, 0);
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

        discoveryPagerAdapter = new DiscoveryPagerAdapter(this.getChildFragmentManager());
        discoveryPagerAdapter.setBundle(getArguments());
        tabViewPager.setAdapter(discoveryPagerAdapter);
        if (!Constants.RELEASE)
        {
            tabViewPager.setOffscreenPageLimit(0);
        }
        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
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
    }

    @Override protected void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        MenuOwnedPortfolioId currentMenu = mSelectedPortfolioContainer.getCurrentMenu();
        if (currentMenu != null && portfolioCompactDTO.id == currentMenu.portfolioId)
        {
            this.portfolioCompactDTO = portfolioCompactDTO;
            FXInfoFragment.setPortfolioCompactDTO(portfolioCompactDTO);
            FXInfoFragment.setPurchaseApplicableOwnedPortfolioId(currentMenu);

            OldPositionListFragment.setGetPositionsDTOKey((GetPositionsDTOKey)mSelectedPortfolioContainer.getCurrentMenu());
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
        return quoteDTO != null && positionDTOCompactList != null && applicableOwnedPortfolioIds != null;
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

    private class DiscoveryPagerAdapter extends FragmentPagerAdapter
    {
        private Bundle mArgs;

        public DiscoveryPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        @Override public Fragment getItem(int position)
        {
            FXMainTabType tabType = FXMainTabType.values()[position];
            ActionBarOwnerMixin.putKeyShowHomeAsUp(mArgs, true);
            if (position == 2)
            {
                if (mSelectedPortfolioContainer.getCurrentMenu() != null && quoteDTO != null)
                {
                    OldPositionListFragment.putShownUser(mArgs, currentUserId.toUserBaseKey());
                    OldPositionListFragment.putGetPositionsDTOKey(mArgs, mSelectedPortfolioContainer.getCurrentMenu());
                    mArgs.putInt(OldPositionListFragment.BUNDLE_KEY_SECURITY_ID, quoteDTO.securityId);
                    OldPositionListFragment.setIsFX(true);
                }
                else
                {
                    THToast.show(R.string.history_tab_wait_message);
                }
            }
            return Fragment.instantiate(getActivity(), tabType.fragmentClass.getName(), mArgs);
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(FXMainTabType.values()[position].titleStringResId);
        }

        @Override public int getCount()
        {
            return FXMainTabType.values().length;
        }

        public void setBundle(Bundle args)
        {
            mArgs = args;
        }
    }
}
