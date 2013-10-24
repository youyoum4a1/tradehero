/**
 * BuyFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Aug 3, 2013
 */
package com.tradehero.th.fragments.trade;

import android.os.AsyncTask;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ToggleButton;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import android.os.Bundle;
import com.tradehero.th.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import retrofit.RetrofitError;

public class BuyFragment extends AbstractTradeFragment
{
    private final static String TAG = BuyFragment.class.getSimpleName();

    public final static String BUNDLE_KEY_SHARE_FACEBOOK = BuyFragment.class.getName() + ".shareFacebook";
    public final static String BUNDLE_KEY_SHARE_TWITTER = BuyFragment.class.getName() + ".shareTwitter";
    public final static String BUNDLE_KEY_SHARE_LINKEDIN = BuyFragment.class.getName() + ".shareLinkedIn";
    public final static String BUNDLE_KEY_SHARE_LOCATION = BuyFragment.class.getName() + ".shareLocation";
    public final static String BUNDLE_KEY_SHARE_PUBLIC = BuyFragment.class.getName() + ".sharePublic";

    private View actionBar;
    private ImageButton mBackBtn;
    private ImageView mMarketClose;
    private TextView mExchangeSymbol;
    private Button mBtnConfirm;

    private ProgressBar mQuoteRefreshProgressBar;
    private EditText mCommentsET;
    private ToggleButton mBtnShareFacebook;
    private ToggleButton mBtnShareTwitter;
    private ToggleButton mBtnShareLinkedIn;
    private ToggleButton mBtnLocation;
    private ToggleButton mBtnSharePublic;
    private TextView mBuyDetails;

    private boolean publishToFb = false;
    private boolean publishToTw = false;
    private boolean publishToLi = false;
    private boolean shareLocation = false;
    private boolean sharePublic = false;

    @Inject protected Lazy<SecurityService> securityService;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    private boolean isBuying = false;
    private boolean isSelling = false;
    private AsyncTask<Void, Void, SecurityPositionDetailDTO> buySellTask;

    //private String yahooQuoteStr;
    //private Quote mQuote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.d(TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        View view = null;
        view = inflater.inflate(R.layout.fragment_buy, container, false);
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

        mQuoteRefreshProgressBar = (ProgressBar) view.findViewById(R.id.quote_refresh_countdown);
        if (mQuoteRefreshProgressBar != null)
        {
            mQuoteRefreshProgressBar.setMax((int) (MILLISEC_QUOTE_REFRESH / MILLISEC_QUOTE_COUNTDOWN_PRECISION));
            mQuoteRefreshProgressBar.setProgress(mQuoteRefreshProgressBar.getMax());
        }

        mCommentsET = (EditText) view.findViewById(R.id.comments);

        // Commented because of removal of right button.
        //mBtnConfirm = (Button) v.findViewById(R.id.right_button);

        mBtnShareFacebook = (ToggleButton) view.findViewById(R.id.btn_share_fb);
        if (mBtnShareFacebook != null)
        {
            mBtnShareFacebook.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    toggleShareFacebook();
                }
            });
        }

        mBtnShareTwitter = (ToggleButton) view.findViewById(R.id.btn_share_tw);
        if (mBtnShareTwitter != null)
        {
            mBtnShareTwitter.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    toggleShareTwitter();
                }
            });
        }

        mBtnShareLinkedIn = (ToggleButton) view.findViewById(R.id.btn_share_li);
        if (mBtnShareLinkedIn != null)
        {
            mBtnShareLinkedIn.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    toggleShareLinkedIn();
                }
            });
        }

        mBtnLocation = (ToggleButton) view.findViewById(R.id.btn_location);
        if (mBtnLocation != null)
        {
            mBtnLocation.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    THLog.d(TAG, "onClick Location");
                    toggleShareLocation();
                }
            });
        }

        mBtnSharePublic = (ToggleButton) view.findViewById(R.id.switch_share_public);
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

        mBuyDetails = (TextView) view.findViewById(R.id.buy_info);
        displayPageElements();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        createBuyConfirmActionBar(menu, inflater);
    }

    private void createBuyConfirmActionBar(Menu menu, MenuInflater inflater)
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSherlockActivity().getSupportActionBar().setCustomView(R.layout.topbar_buy_confirm);

        actionBar = getSherlockActivity().getSupportActionBar().getCustomView();

        mBackBtn = (ImageButton) actionBar.findViewById(R.id.btn_back);
        if (mBackBtn != null)
        {
            mBackBtn.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    navigator.popFragment();
                }
            });
        }

        mMarketClose = (ImageView) actionBar.findViewById(R.id.ic_market_close);

        mExchangeSymbol = (TextView) actionBar.findViewById(R.id.header_txt);

        mBtnConfirm = (Button) actionBar.findViewById(R.id.btn_confirm);
        if (mBtnConfirm != null)
        {
            mBtnConfirm.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    launchBuySell();
                }
            });
        }
        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // We display here as onCreateOptionsMenu may be called after onResume
        displayActionBarElements();
    }

    @Override public void onDestroyOptionsMenu()
    {
        if (mBackBtn != null)
        {
            mBackBtn.setOnClickListener(null);
        }
        mBackBtn = null;
        if (mBtnConfirm != null)
        {
            mBtnConfirm.setOnClickListener(null);
        }
        mBtnConfirm = null;
        actionBar = null;
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        THLog.d(TAG, "onDestroyView");
        if (buySellTask != null)
        {
            buySellTask.cancel(false);
        }
        buySellTask = null;

        if (mBtnShareFacebook != null)
        {
            mBtnShareFacebook.setOnClickListener(null);
        }
        mBtnShareFacebook = null;

        if (mBtnShareTwitter != null)
        {
            mBtnShareTwitter.setOnClickListener(null);
        }
        mBtnShareTwitter = null;

        if (mBtnShareLinkedIn != null)
        {
            mBtnShareLinkedIn.setOnClickListener(null);
        }
        mBtnShareLinkedIn = null;

        if (mBtnLocation != null)
        {
            mBtnLocation.setOnClickListener(null);
        }
        mBtnLocation = null;

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
            displayExchangeSymbol();
            displayBuySellDetails();
        }
    }

    @Override public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        super.linkWith(securityCompactDTO, andDisplay);
        if (andDisplay)
        {
            displayMarketClose();
            displayBuySellDetails();
        }
    }

    @Override public void linkWith(SecurityPositionDetailDTO securityPositionDetailDTO, boolean andDisplay)
    {
        super.linkWith(securityPositionDetailDTO, andDisplay);
        if (andDisplay)
        {
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
            displayBuySellDetails();
        }
    }

    public void toggleShareFacebook()
    {
        publishToFb = !publishToFb;
        displayPublishToFb();
    }

    public void toggleShareTwitter()
    {
        publishToTw = !publishToTw;
        displayPublishToTw();
    }

    public void toggleShareLinkedIn()
    {
        publishToLi = !publishToLi;
        displayPublishToLi();
    }

    public void toggleShareLocation()
    {
        shareLocation = !shareLocation;
        displayShareLocation();
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayActionBarElements();
        displayPageElements();
    }

    public void displayActionBarElements()
    {
        displayExchangeSymbol();
        displayMarketClose();
    }

    public void displayPageElements()
    {
        displayBuySellDetails();
        displayButtonConfirm();
        displayPublishToFb();
        displayPublishToTw();
        displayPublishToLi();
        displayShareLocation();
    }

    public void displayExchangeSymbol()
    {
        if (mExchangeSymbol != null)
        {
            if (securityId != null)
            {
                mExchangeSymbol.setText(String.format("%s:%s", securityId.exchange, securityId.securitySymbol));
            }
            else
            {
                mExchangeSymbol.setText("-:-");
            }
        }
    }

    public void displayMarketClose()
    {
        if (mMarketClose != null)
        {
            if (securityCompactDTO != null)
            {
                mMarketClose.setVisibility(securityCompactDTO.marketOpen ? View.GONE : View.VISIBLE);
            }
            else
            {
                mMarketClose.setVisibility(View.GONE);
            }
        }
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

    public void displayButtonConfirm()
    {
        if (mBtnConfirm != null)
        {
            mBtnConfirm.setEnabled(
                    (!isBuying && isTransactionTypeBuy && hasValidInfoForBuy()) ||
                    (!isSelling &&!isTransactionTypeBuy && hasValidInfoForSell()));
            mBtnConfirm.setAlpha(mBtnConfirm.isEnabled() ? 1 : 0.5f);
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
        freshQuoteHolder.identifier = "BuyFragment";
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
        displayButtonConfirm();
        // TODO visual cue
    }

    private void setSelling(boolean isSelling)
    {
        this.isSelling = isSelling;
        displayButtonConfirm();
        // TODO other visual cue
    }

    //<editor-fold desc="FreshQuoteHolder.FreshQuoteListener">
    @Override public void onMilliSecToRefreshQuote(long milliSecToRefresh)
    {
        if (mQuoteRefreshProgressBar != null)
        {
            mQuoteRefreshProgressBar.setProgress((int) (milliSecToRefresh / MILLISEC_QUOTE_COUNTDOWN_PRECISION));
        }
    }
    //</editor-fold>

    private TransactionFormDTO getBuySellOrder(boolean isBuy)
    {
        if (quoteDTO == null)
        {
            return null;
        }
        if (securityPositionDetailDTO == null || securityPositionDetailDTO.positions == null || securityPositionDetailDTO.positions.size() == 0 ||
                securityPositionDetailDTO.positions.get(mPositionIndex) == null)
        {
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
                securityPositionDetailDTO.positions.get(mPositionIndex).portfolioId
        );
    }

    private void launchBuySell()
    {
        if (buySellTask != null)
        {
            buySellTask.cancel(false);
        }
        buySellTask = createBuySellTask(isTransactionTypeBuy);
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

    private AsyncTask<Void, Void, SecurityPositionDetailDTO> createBuySellTask(final boolean isBuy)
    {
        return new AsyncTask<Void, Void, SecurityPositionDetailDTO>()
        {
            @Override protected SecurityPositionDetailDTO doInBackground(Void... voids)
            {
                TransactionFormDTO buySellOrder = getBuySellOrder(isBuy);
                if (buySellOrder == null)
                {
                    return null;
                }
                SecurityPositionDetailDTO returned = null;
                try
                {
                    if (isBuy)
                    {
                        returned = securityService.get().buy(securityId.exchange, securityId.securitySymbol, buySellOrder);
                    }
                    else
                    {
                        returned = securityService.get().sell(securityId.exchange, securityId.securitySymbol, buySellOrder);
                    }
                }
                catch (RetrofitError e)
                {
                    THLog.e(TAG, "Failed to buy-sell", e);
                }
                if (returned != null)
                {
                    securityPositionDetailCache.get().put(securityId, returned);

                    if (returned.portfolio != null)
                    {
                        UserBaseKey userBaseKey = THUser.getCurrentUserBase().getBaseKey();
                        UserProfileDTO userProfileDTO = userProfileCache.get().get(userBaseKey);
                        if (userProfileDTO != null && (userProfileDTO.portfolio == null || userProfileDTO.portfolio.id == returned.portfolio.id))
                        {
                            userProfileDTO.portfolio = returned.portfolio;
                            userProfileCache.get().put(userBaseKey, userProfileDTO);
                        }
                    }
                }
                return returned;
            }

            @Override protected void onPostExecute(SecurityPositionDetailDTO securityPositionDetailDTO)
            {
                super.onPostExecute(securityPositionDetailDTO);
                if (isCancelled())
                {
                    return;
                }

                if (securityPositionDetailDTO != null && isBuy)
                {
                    setBuying(false);
                }
                else if (securityPositionDetailDTO != null)
                {
                    setSelling(false);
                }
                displayButtonConfirm();
                // TODO post to social network?
                returnToTradeFragment();
            }
        };
    }

    private void returnToTradeFragment()
    {
        navigator.popFragment();
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
