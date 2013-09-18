/**
 * BuyFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Aug 3, 2013
 */
package com.tradehero.th.fragments;

import com.tradehero.th.base.THUser;
import com.tradehero.th.utils.NetworkUtils;
import com.tradehero.th.fragments.trending.TradeFragment;
import java.util.LinkedHashMap;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.application.Config;
import com.tradehero.th.http.THAsyncClientFactory;
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
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class BuyFragment extends Fragment
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

    //private EditText mCommentsET;
    private Button mBtnConform;
    private TextView mBuyDetails;
    private TextView mHeaderText;

    //private String lastPrice;
    private String quantity;

    //private String yahooQuoteStr;
    //private Quote mQuote;
    private Object quotes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = null;
        view = inflater.inflate(R.layout.fragment_buy, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View v)
    {

        //lastPrice = getArguments().getString(TradeFragment.LAST_PRICE);
        quantity = getArguments().getString(TradeFragment.QUANTITY);

        mHeaderText = (TextView) v.findViewById(R.id.header_txt);
        mHeaderText.setText(String.format("%s:%s", getArguments().getString(TradeFragment.EXCHANGE),
                getArguments().getString(TradeFragment.SYMBOL)));

        //mCommentsET = (EditText) v.findViewById(R.id.comments);
        mBtnConform = (Button) v.findViewById(R.id.right_button);
        mBtnConform.setText(R.string.btn_buy);
        mBtnConform.setVisibility(View.VISIBLE);

        mBuyDetails = (TextView) v.findViewById(R.id.buy_info);
        mBuyDetails.setText(getArguments().getString(TradeFragment.BUY_DETAIL_STR));
        //lastPrice = getArguments().getString(TradeFragment.LAST_PRICE);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mBtnConform.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (NetworkUtils.isConnected(getActivity()))
                {
                    requestToGetBuyQuotes();
                }
                else
                {
                    Util.show_toast(getActivity(), getResources().getString(R.string.network_error));
                }
            }
        });
    }

    private void requestToGetBuyQuotes()
    {
        AsyncHttpClient client = THAsyncClientFactory.getInstance(Constants.TH_EMAIL_PREFIX);

        client.get(String.format(Config.getTrendNewBuyQuotes(),
                getArguments().getString(TradeFragment.EXCHANGE),
                getArguments().getString(TradeFragment.SYMBOL)),
                new AsyncHttpResponseHandler()
                {

                    @Override
                    public void onSuccess(int arg0, String response)
                    {

                        if (response.length() > 0)
                        {

                            Logger.log(TAG, "Buy Quote Response:\n" + response, LogLevel.LOGGING_LEVEL_INFO);

                            //Object rawStr = (Object)JSONObject.quote(response);

                            setQuotes(response);

                            requestToBuyTransaction();
                        }
                        else
                        {
                            Logger.log(TAG, "TH Quote response is blank", LogLevel.LOGGING_LEVEL_ERROR);
                        }
                    }

                    @Override
                    public void onFailure(Throwable arg0, String response)
                    {
                        Logger.log(TAG, "Unable to get TH Quotes:\n" + response, LogLevel.LOGGING_LEVEL_ERROR);
                    }
                });
    }

    private void requestToBuyTransaction()
    {
        LinkedHashMap<String, Object> postParams = new LinkedHashMap<String, Object>();

        postParams.put(BP_QUANTITY, Integer.valueOf(quantity));
        postParams.put(BP_PORTFOLIO, Integer.valueOf(THUser.getCurrentUser().portfolio.id));
        postParams.put(BP_SIGNED_QUOTE_DTO, getQuotes().toString());
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

        AsyncHttpClient client = THAsyncClientFactory.getInstance(Constants.TH_EMAIL_PREFIX);

        String url = String.format(
                Config.getBuyNewTrend(),
                getArguments().getString(TradeFragment.EXCHANGE),
                getArguments().getString(TradeFragment.SYMBOL));

        Logger.log(TAG, url, LogLevel.LOGGING_LEVEL_INFO);

        client.post(getActivity(), url, entity, "application/json", new AsyncHttpResponseHandler()
        {

            @Override
            public void onSuccess(int arg0, String response)
            {

                Logger.log(TAG, "Buy Transaction Response:\n" + response, LogLevel.LOGGING_LEVEL_INFO);

                try
                {
                    JSONObject jsonObj = new JSONObject(response);
                    Toast.makeText(getActivity(), jsonObj.optString("Message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable arg0, String response)
            {
                super.onFailure(arg0, response);

                Logger.log(TAG, "Buy Transaction Response:\n" + response, LogLevel.LOGGING_LEVEL_ERROR);

                try
                {
                    JSONObject jsonObj = new JSONObject(response);
                    Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
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

    public Object getQuotes()
    {
        return quotes;
    }

    public void setQuotes(Object quotes)
    {
        this.quotes = quotes;
    }

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
}
