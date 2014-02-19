/**
 * BuySellConfirmFragment.java
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Aug 3, 2013
 */
package com.tradehero.th.fragments.trade;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;

public class BuySellConfirmFragment extends AbstractBuySellFragment
{
    private final static String TAG = BuySellConfirmFragment.class.getSimpleName();

    public final static String BUNDLE_KEY_SHARE_FACEBOOK = BuySellConfirmFragment.class.getName() + ".shareFacebook";
    public final static String BUNDLE_KEY_SHARE_TWITTER = BuySellConfirmFragment.class.getName() + ".shareTwitter";
    public final static String BUNDLE_KEY_SHARE_LINKEDIN = BuySellConfirmFragment.class.getName() + ".shareLinkedIn";
    public final static String BUNDLE_KEY_SHARE_LOCATION = BuySellConfirmFragment.class.getName() + ".shareLocation";
    public final static String BUNDLE_KEY_SHARE_PUBLIC = BuySellConfirmFragment.class.getName() + ".sharePublic";

    @InjectView(R.id.quote_refresh_countdown) ProgressBar mQuoteRefreshProgressBar;
    @InjectView(R.id.comments) EditText mCommentsET;

    @InjectView(R.id.btn_share_fb) ToggleButton mBtnShareFacebook;
    @OnClick(R.id.btn_share_fb)
    public void toggleShareFacebook()
    {
        publishToFb = !publishToFb;
        displayPublishToFb();
    }

    @InjectView(R.id.btn_share_tw) ToggleButton mBtnShareTwitter;
    @OnClick(R.id.btn_share_tw)
    public void toggleShareTwitter()
    {
        publishToTw = !publishToTw;
        displayPublishToTw();
    }

    @InjectView(R.id.btn_share_li)ToggleButton mBtnShareLinkedIn;
    @OnClick(R.id.btn_share_li)
    public void toggleShareLinkedIn()
    {
        publishToLi = !publishToLi;
        displayPublishToLi();
    }

    @InjectView(R.id.btn_location) ToggleButton mBtnLocation;
    @OnClick(R.id.btn_location)
    public void toggleShareLocation()
    {
        shareLocation = !shareLocation;
        displayShareLocation();
    }

    @InjectView(R.id.switch_share_public) ToggleButton mBtnSharePublic;
    @InjectView(R.id.buy_info) TextView mBuyDetails;

    private MenuItem buySellConfirmItem;

    private boolean publishToFb = false;
    private boolean publishToTw = false;
    private boolean publishToLi = false;
    private boolean shareLocation = false;
    private boolean sharePublic = false;

    @Inject protected Lazy<SecurityService> securityService;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    private boolean isBuying = false;
    private boolean isSelling = false;
    private BaseBuySellAsyncTask buySellTask;

    //private String yahooQuoteStr;
    //private Quote mQuote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.d(TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        View view = null;
        view = inflater.inflate(R.layout.fragment_buy_sell_confirm, container, false);
        initViews(view);
        return view;
    }

    @Override protected void collectFromParameters(Bundle args)
    {
        super.collectFromParameters(args);
        if (args != null)
        {
            publishToFb = args.getBoolean(BUNDLE_KEY_SHARE_FACEBOOK, publishToFb);
            publishToTw = args.getBoolean(BUNDLE_KEY_SHARE_TWITTER, publishToTw);
            publishToLi = args.getBoolean(BUNDLE_KEY_SHARE_LINKEDIN, publishToLi);
            shareLocation = args.getBoolean(BUNDLE_KEY_SHARE_LOCATION, shareLocation);
            sharePublic = args.getBoolean(BUNDLE_KEY_SHARE_PUBLIC, sharePublic);
        }
    }

    @Override protected void initViews(View view)
    {
        super.initViews(view);
        ButterKnife.inject(this, view);

        if (mQuoteRefreshProgressBar != null)
        {
            mQuoteRefreshProgressBar.setMax((int) (MILLISEC_QUOTE_REFRESH / MILLISEC_QUOTE_COUNTDOWN_PRECISION));
            mQuoteRefreshProgressBar.setProgress(mQuoteRefreshProgressBar.getMax());
        }

        if (mBtnSharePublic != null)
        {
            mBtnSharePublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b)
                {
                    sharePublic = b;
                }
            });
        }

        displayPageElements();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.buy_sell_confirm_menu, menu);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

        displayExchangeSymbol(actionBar);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        buySellConfirmItem = menu.findItem(R.id.buy_sell_menu_confirm);
        displayConfirmMenuItem();

        marketCloseIcon = menu.findItem(R.id.buy_sell_menu_market_status);
        displayMarketClose();
    }

    @Override public void onDestroyOptionsMenu()
    {
        super.onDestroyOptionsMenu();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.buy_sell_menu_market_status:
                handleMarketCloseClicked();
                return true;
            case R.id.buy_sell_menu_confirm:
                launchBuySell();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override public void onDestroyView()
    {
        THLog.d(TAG, "onDestroyView");

        DeviceUtil.dismissKeyboard(getActivity());

        ButterKnife.reset(this);

        if (buySellTask != null)
        {
            buySellTask.cancel(false);
        }
        buySellTask = null;

        if (mBtnSharePublic != null)
        {
            mBtnSharePublic.setOnCheckedChangeListener(null);
        }
        mBtnSharePublic = null;
        super.onDestroyView();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        THLog.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_KEY_SHARE_FACEBOOK, publishToFb);
        outState.putBoolean(BUNDLE_KEY_SHARE_TWITTER, publishToTw);
        outState.putBoolean(BUNDLE_KEY_SHARE_LINKEDIN, publishToLi);
        outState.putBoolean(BUNDLE_KEY_SHARE_LOCATION, shareLocation);
        outState.putBoolean(BUNDLE_KEY_SHARE_PUBLIC, sharePublic);
    }

    @Override public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (andDisplay)
        {
            //displayExchangeSymbol();
            displayBuySellDetails();
        }
    }

    @Override public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        super.linkWith(securityCompactDTO, andDisplay);
        if (andDisplay)
        {
            displayBuySellDetails();
        }
    }

    @Override public void linkWith(SecurityPositionDetailDTO securityPositionDetailDTO, boolean andDisplay)
    {
        super.linkWith(securityPositionDetailDTO, andDisplay);
        if (andDisplay)
        {
            displayConfirmMenuItem();
            displayBuySellDetails();
        }
    }

    @Override public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        if (isTransactionTypeBuy)
        {
            Integer maxPurchasableShares = getMaxPurchasableShares();
            if (maxPurchasableShares != null)
            {
                mBuyQuantity = Math.min(maxPurchasableShares.intValue(), mBuyQuantity);
            }
        }
        super.linkWith(userProfileDTO, andDisplay);
        if (andDisplay)
        {
            displayConfirmMenuItem();
            displayBuySellDetails();
        }
    }

    @Override protected void linkWith(QuoteDTO quoteDTO, boolean andDisplay)
    {
        if (isTransactionTypeBuy)
        {
            Integer maxPurchasableShares = getMaxPurchasableShares();
            if (maxPurchasableShares != null)
            {
                mBuyQuantity = Math.min(maxPurchasableShares.intValue(), mBuyQuantity);
            }
        }
        else
        {
            Integer maxSellableShares = getMaxSellableShares();
            if (maxSellableShares != null)
            {
                mSellQuantity = Math.min(maxSellableShares.intValue(), mSellQuantity);
            }
        }
        super.linkWith(quoteDTO, andDisplay);
        if (andDisplay)
        {
            displayConfirmMenuItem();
            displayBuySellDetails();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayActionBarElements();
        displayPageElements();
    }

    public void displayPageElements()
    {
        displayBuySellDetails();
        displayPublishToFb();
        displayPublishToTw();
        displayPublishToLi();
        displayShareLocation();
    }

    public void displayActionBarElements()
    {
        displayMarketClose();
        displayConfirmMenuItem();
    }

    public void displayBuySellDetails()
    {
        if (mBuyDetails != null)
        {
            if (isTransactionTypeBuy)
            {
                mBuyDetails.setText(getBuyDetails());
            }
            else
            {
                mBuyDetails.setText(getSellDetails());
            }
            if (!refreshingQuote)
            {
                mBuyDetails.setAlpha(1);
            }
        }
    }

    public void displayConfirmMenuItem()
    {
        if (buySellConfirmItem != null)
        {
            boolean buttonEnabled = (!isBuying && isTransactionTypeBuy && hasValidInfoForBuy()) ||
                    (!isSelling &&!isTransactionTypeBuy && hasValidInfoForSell());
            buySellConfirmItem.setEnabled(buttonEnabled);
            //buySellConfirmItem.getActionView().setAlpha(buttonEnabled ? 1 : 0.5f);
        }
    }

    public void displayPublishToFb()
    {
        if (mBtnShareFacebook != null)
        {
            displayToggleButton(mBtnShareFacebook, publishToFb);
        }
    }

    public void displayPublishToTw()
    {
        if (mBtnShareTwitter != null)
        {
            displayToggleButton(mBtnShareTwitter, publishToTw);
        }
    }

    public void displayPublishToLi()
    {
        if (mBtnShareLinkedIn != null)
        {
            displayToggleButton(mBtnShareLinkedIn, publishToLi);
        }
    }

    public void displayShareLocation()
    {
        if (mBtnLocation != null)
        {
            displayToggleButton(mBtnLocation, shareLocation);
        }
    }

    private void displayToggleButton(ToggleButton button, boolean expectedStatus)
    {
        if (button != null && button.isChecked() != expectedStatus)
        {
            button.setChecked(expectedStatus);
        }
    }
    //</editor-fold>

    @Override protected void prepareFreshQuoteHolder()
    {
        super.prepareFreshQuoteHolder();
        freshQuoteHolder.identifier = "BuySellConfirmFragment";
    }

    @Override protected void setRefreshingQuote(boolean refreshingQuote)
    {
        super.setRefreshingQuote(refreshingQuote);
        if (mBuyDetails != null && refreshingQuote)
        {
            mBuyDetails.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_out));
        }
    }

    private void setBuying(boolean isBuying)
    {
        this.isBuying = isBuying;
        //displayConfirmMenuItem(buySellConfirmItem);
        // TODO visual cue
    }

    private void setSelling(boolean isSelling)
    {
        this.isSelling = isSelling;
        //displayConfirmMenuItem(buySellConfirmItem);
        // TODO other visual cue
    }

    private TransactionFormDTO getBuySellOrder(boolean isBuy)
    {
        if (quoteDTO == null)
        {
            return null;
        }
        if (getApplicablePortfolioId() == null || getApplicablePortfolioId().portfolioId == null)
        {
            THLog.e(TAG, "No portfolioId to apply to", new IllegalStateException());
            return null;
        }
        return new TransactionFormDTO(
                publishToFb,
                publishToTw,
                publishToLi,
                shareLocation ? null : null, // TODO implement location
                shareLocation ? null : null,
                shareLocation ? null : null,
                sharePublic,
                mCommentsET == null ? null : mCommentsET.getText().toString(),
                quoteDTO.rawResponse,
                isBuy ? mBuyQuantity : mSellQuantity,
                getApplicablePortfolioId().portfolioId
        );
    }

    private void launchBuySell()
    {
        if (buySellTask != null)
        {
            buySellTask.cancel(false);
        }
        buySellTask = new BuySellAsyncTask(getActivity(), isTransactionTypeBuy, securityId);
        if (isTransactionTypeBuy)
        {
            setBuying(true);
        }
        else
        {
            setSelling(true);
        }
        buySellTask.execute();
    }

    public class BuySellAsyncTask extends BaseBuySellAsyncTask
    {
        private ProgressDialog transactionDialog;

        public BuySellAsyncTask(Context context, boolean isBuy, SecurityId securityId)
        {
            super(context, isBuy, securityId);
        }

        @Override protected void onPreExecute()
        {
            transactionDialog =
                    ProgressDialogUtil.show(BuySellConfirmFragment.this.getActivity(), R.string.loading_loading, R.string.alert_dialog_please_wait);
            super.onPreExecute();
        }

        @Override TransactionFormDTO getBuySellOrder()
        {
            return BuySellConfirmFragment.this.getBuySellOrder(isBuy);
        }

        @Override protected void onPostExecute(SecurityPositionDetailDTO securityPositionDetailDTO)
        {
            super.onPostExecute(securityPositionDetailDTO);
            if (transactionDialog != null)
            {
                transactionDialog.dismiss();
            }

            if (isCancelled())
            {
                return;
            }

            if (isBuy)
            {
                setBuying(false);
            }
            else
            {
                setSelling(false);
            }
            //displayConfirmMenuItem(buySellConfirmItem);
            if (errorCode == CODE_OK)
            {
                if (securityPositionDetailDTO != null && securityPositionDetailDTO.portfolio != null)
                {
                    pushPortfolioFragment(new OwnedPortfolioId(currentUserId.toUserBaseKey(), securityPositionDetailDTO.portfolio.getPortfolioId()));
                }
                else
                {
                    pushPortfolioFragment();
                }
            }
        }
    }

    private void pushPortfolioFragment()
    {
        pushPortfolioFragment(getApplicablePortfolioId());
    }

    private void pushPortfolioFragment(OwnedPortfolioId ownedPortfolioId)
    {
        // TODO find a better way to remove this fragment from the stack
        getNavigator().popFragment();

        Bundle args = new Bundle();
        args.putBundle(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
        getNavigator().pushFragment(PositionListFragment.class, args);
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    @Override protected FreshQuoteHolder.FreshQuoteListener createFreshQuoteListener()
    {
        return new BuySellConfirmationFreshQuoteListener();
    }

    protected class BuySellConfirmationFreshQuoteListener extends AbstractBuySellFreshQuoteListener
    {
        @Override public void onMilliSecToRefreshQuote(long milliSecToRefresh)
        {
            if (mQuoteRefreshProgressBar != null)
            {
                mQuoteRefreshProgressBar.setProgress((int) (milliSecToRefresh / MILLISEC_QUOTE_COUNTDOWN_PRECISION));
            }
        }
    }
}
