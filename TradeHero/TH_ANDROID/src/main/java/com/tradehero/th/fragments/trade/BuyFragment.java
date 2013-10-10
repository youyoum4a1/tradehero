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
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.network.service.SecurityService;
import dagger.Lazy;
import java.util.LinkedHashMap;
import javax.inject.Inject;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.utills.Constants;
import com.tradehero.th.utills.Logger;
import com.tradehero.th.utills.Logger.LogLevel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit.RetrofitError;

public class BuyFragment extends AbstractTradeFragment
{
    private final static String TAG = BuyFragment.class.getSimpleName();


    private final static String BP_GEO_ALT = "geo_alt";
    private final static String BP_GEO_LAT = "geo_lat";
    private final static String BP_GEO_LONG = "geo_long";
    private final static String BP_IS_PUBLIC = "isPublic";
    private final static String BP_PORTFOLIO = "portfolio";
    private final static String BP_PUBLIST_TO_FB = "publishToFb";
    private final static String BP_PUBLIST_TO_LI = "publishToLi";
    private final static String BP_PUBLIST_TO_TW = "publishToTw";
    private final static String BP_QUANTITY = "quantity";
    //private final static String BP_SIGNATURE = "signature";
    //private final static String BP_TIMESTAMP = "timestamp";
    //private final static String BP_TRANSACTION_COST = "transactioncost";
    //private final static String BP_YCSV_QUOTE_DATA = "ycsvQuoteData";
    //private final static String BP_TRADE_COMMENT = "tradeComment";
    private final static String BP_SIGNED_QUOTE_DTO = "signedQuoteDto";

    private View actionBar;
    private ImageButton mBackBtn;
    private ImageView mMarketClose;
    private TextView mExchangeSymbol;
    private Button mBtnConfirm;

    private ProgressBar mQuoteRefreshProgressBar;
    private EditText mCommentsET;
    private ImageButton mBtnLocation;
    private ToggleButton mBtnSharePublic;
    private TextView mBuyDetails;

    //private String lastPrice;
    private boolean publishToFb;
    private boolean publishToTw;
    private boolean publishToLi;
    private int quantity;
    private String buyDetails;
    private boolean shareLocation;
    private boolean sharePublic;


    @Inject protected Lazy<SecurityService> securityService;
    private boolean isBuying = false;
    private boolean isSelling = false;
    private AsyncTask<Void, Void, SecurityPositionDetailDTO> buySellTask;

    //private String yahooQuoteStr;
    //private Quote mQuote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.d(TAG, "onCreateView");
        View view = null;
        view = inflater.inflate(R.layout.fragment_buy, container, false);
        initViews(view);
        return view;
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

        mBtnLocation = (ImageButton) view.findViewById(R.id.btn_location);
        if (mBtnLocation != null)
        {
            mBtnLocation.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    THLog.d(TAG, "onClick Location");
                    shareLocation = !shareLocation;
                    displayShareLocation();
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
        getSherlockActivity().getSupportActionBar().setCustomView(R.layout.buy_confirm_topbar);

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

    public void displayShareLocation()
    {
        if (mBtnLocation != null)
        {
            mBtnLocation.setAlpha(shareLocation ? 1 : 0.5f);
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
        // TODO visual cue
    }

    private void requestToBuyTransaction()
    {
        LinkedHashMap<String, Object> postParams = new LinkedHashMap<String, Object>();

        postParams.put(BP_QUANTITY, Integer.valueOf(quantity));
        postParams.put(BP_PORTFOLIO, Integer.valueOf(THUser.getCurrentUser().portfolio.id));
        //postParams.put(BP_SIGNED_QUOTE_DTO, getQuotes().toString());
        postParams.put(BP_GEO_ALT, null);
        postParams.put(BP_GEO_LAT, null);
        postParams.put(BP_GEO_LONG, null);
        postParams.put(BP_IS_PUBLIC, false);  //Yes = 1 No = 0
        postParams.put(BP_PUBLIST_TO_FB, false); //Yes = 1 No = 0
        postParams.put(BP_PUBLIST_TO_LI, false); //Yes = 1 No = 0
        postParams.put(BP_PUBLIST_TO_TW, false); //Yes = 1 No = 0]

        //Logger.log(TAG, "Params: \n"+postParams.toString(), LogLevel.LOGGING_LEVEL_INFO);

        StringEntity entity = null;

        try
        {

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(postParams);

            Logger.log(TAG, "Post Params for Buy request: \n" + json, LogLevel.LOGGING_LEVEL_INFO);

            entity = new StringEntity(json, HTTP.UTF_8);
            entity.setContentEncoding(new BasicHeader(Constants.CONTENT_TYPE, "application/json"));
            entity.setContentType("application/json; charset=utf-8");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //	public class PostData implements Serializable {
    //
    //		private static final long serialVersionUID = 1L;
    //
    //		private Object aquotes;
    //
    //		public Object getQuotes() {
    //			return aquotes;
    //		}
    //
    //		public void setQuotes(Object quotes) {
    //			this.aquotes = quotes;
    //		}
    //	}

    //	StringEntity entity = null;
    //	try {
    //	entity = new StringEntity(someData);
    //	entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
    //	} catch(Exception e) {
    //	//Exception
    //	}
    //
    //	client.post(null,url,entity,"application/json",new AsyncHttpResponseHandler() { });

    //	private LinkedHashMap<String, String> getBuyParametersWithValues() {
    //
    //		LinkedHashMap<String, String> mBuyParametersMap = new LinkedHashMap<String, String>();
    //		mBuyParametersMap.put(BP_DISPLAY_PRICE, lastPrice);
    //		mBuyParametersMap.put(BP_GEO_ALT, null);
    //		mBuyParametersMap.put(BP_GEO_LAT, null);
    //		mBuyParametersMap.put(BP_GEO_LONG, null);
    //		mBuyParametersMap.put(BP_IS_PUBLIC, "0");  //Yes = 1 No = 0
    //		mBuyParametersMap.put(BP_PORTFOLIO, "240128");
    //		mBuyParametersMap.put(BP_PUBLIST_TO_FB, "0"); //Yes = 1 No = 0
    //		mBuyParametersMap.put(BP_PUBLIST_TO_LI, "0"); //Yes = 1 No = 0
    //		mBuyParametersMap.put(BP_PUBLIST_TO_TW, "0"); //Yes = 1 No = 0
    //		mBuyParametersMap.put(BP_QUANTITY, quantity);
    //		//if(!isForSignature)
    //		//	mBuyParametersMap.put(BP_SIGNATURE, genarateSignatureUsingBuyParametersAndItsValues());
    //		mBuyParametersMap.put(BP_TIMESTAMP, DateUtils.getCurrentTimeStampWithFormat());
    //		mBuyParametersMap.put(BP_TRANSACTION_COST, "10");
    //		mBuyParametersMap.put(BP_YCSV_QUOTE_DATA, yahooQuoteStr);
    //		//mBuyParametersMap.put(BP_TRADE_COMMENT, "");
    //
    //		return mBuyParametersMap;
    //	}

    //	private String genarateSignatureUsingBuyParametersAndItsValues() {
    //
    //		String parameterKeyValueString = getBuyParametersWithValues().toString();
    //		String formatedParameterKeyValueString = "";
    //		String generatedSignature = "";
    //
    //		if(parameterKeyValueString.length() > 0) {
    //
    //			try {
    //				//Logger.log(TAG, "--parameterKeyValueString:--\n"+parameterKeyValueString, LogLevel.LOGGING_LEVEL_INFO);
    //
    //				formatedParameterKeyValueString = parameterKeyValueString.replace(",", "&");
    //				//Logger.log(TAG, "--formatedParameterKeyValueString:--\n"+formatedParameterKeyValueString, LogLevel.LOGGING_LEVEL_INFO);
    //
    //				formatedParameterKeyValueString = formatedParameterKeyValueString.replace("& ", "&");
    //				//Logger.log(TAG, "--formatedParameterKeyValueString:--\n"+formatedParameterKeyValueString, LogLevel.LOGGING_LEVEL_INFO);
    //
    //				generatedSignature = EncryptionUtils.calculateRFC2104HMAC(formatedParameterKeyValueString, SIGNATURE_KEY);
    //				//Logger.log(TAG, "--generatedSignature:--\n"+generatedSignature, LogLevel.LOGGING_LEVEL_INFO);
    //			}
    //			catch (SignatureException e) {
    //				e.printStackTrace();
    //			}
    //		}
    //
    //		return generatedSignature;
    //	}

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
                shareLocation ? null : null,
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

}
