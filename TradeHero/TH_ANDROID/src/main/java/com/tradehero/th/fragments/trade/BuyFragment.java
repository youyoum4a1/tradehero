/**
 * BuyFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Aug 3, 2013
 */
package com.tradehero.th.fragments.trade;

import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.THUser;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.NetworkUtils;
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
import com.tradehero.th.utills.Util;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BuyFragment extends AbstractTradeFragment
{
    private final static String TAG = BuyFragment.class.getSimpleName();

    public final static String BUNDLE_KEY_BUY_DETAIL_STR = BuyFragment.class.getName() + ".buyDetailStr";
    public final static String BUNDLE_KEY_LAST_PRICE = BuyFragment.class.getName() + ".lastPrice";
    public final static String BUNDLE_KEY_QUANTITY = BuyFragment.class.getName() + ".quantity";

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

    //private EditText mCommentsET;
    private TextView mBuyDetails;
    private TextView mHeaderText;

    //private String lastPrice;
    private String quantity;

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
        mHeaderText = (TextView) view.findViewById(R.id.header_txt);

        //mCommentsET = (EditText) v.findViewById(R.id.comments);

        // Commented because of removal of right button.
        //mBtnConfirm = (Button) v.findViewById(R.id.right_button);
        mBtnConfirm.setText(R.string.btn_buy);
        mBtnConfirm.setVisibility(View.VISIBLE);

        mBuyDetails = (TextView) view.findViewById(R.id.buy_info);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mBtnConfirm.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (NetworkUtils.isConnected(getActivity()))
                {
                    // TODO buy
                }
                else
                {
                    Util.show_toast(getActivity(), getResources().getString(R.string.network_error));
                }
            }
        });
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        createBuyConfirmActionBar(menu, inflater);
    }

    private void createBuyConfirmActionBar(Menu menu, MenuInflater inflater)
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSherlockActivity().getSupportActionBar().setCustomView(R.layout.trade_topbar);

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
                    buy();
                }
            });
        }

        // We display here as onCreateOptionsMenu may be called after onResume
        display();
    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        if (args != null)
        {
            linkWith(new SecurityId(args), true);
        }

        //lastPrice = getArguments().getString(TradeFragment.LAST_PRICE);
        quantity = getArguments().getString(BUNDLE_KEY_QUANTITY);

        if (mBuyDetails != null)
        {
            mBuyDetails.setText(getArguments().getString(BUNDLE_KEY_BUY_DETAIL_STR));
        }
        //lastPrice = getArguments().getString(TradeFragment.LAST_PRICE);
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

    public void display()
    {
        displayHeaderText();
    }

    public void displayHeaderText()
    {
        if (mHeaderText != null)
        {
            if (securityId != null)
            {
                mHeaderText.setText(String.format("%s:%s", securityId.exchange, securityId.securitySymbol));
            }
            else
            {
                mHeaderText.setText("-:-");
            }
        }
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
        // TODO
    }
    //</editor-fold>

    private void buy()
    {
        // TODO
    }
}
