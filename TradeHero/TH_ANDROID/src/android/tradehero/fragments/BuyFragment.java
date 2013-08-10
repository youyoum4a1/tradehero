/**
 * BuyFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Aug 3, 2013
 */
package android.tradehero.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.tradehero.activities.R;
import android.tradehero.application.App;
import android.tradehero.application.Config;
import android.tradehero.models.Token;
import android.tradehero.networkstatus.NetworkStatus;
import android.tradehero.utills.Constants;
import android.tradehero.utills.Logger;
import android.tradehero.utills.Logger.LogLevel;
import android.tradehero.utills.Util;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
	

public class BuyFragment extends Fragment {
	
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
	private final static String BP_SIGNED_QUOTE_DTO = "signedQuoteDto ";
	
	private EditText mCommentsET;
	private Button mBtnConform;
	private TextView mBuyDetails;
	private TextView mHeaderText;
	
	//private String lastPrice;
	private String quantity;
	private String quotes;
	//private String yahooQuoteStr;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		view = inflater.inflate(R.layout.fragment_buy, container, false);
		initViews(view);
		return view;
	}
	
	private void initViews(View v) {
		
		//lastPrice = getArguments().getString(TradeFragment.LAST_PRICE);
		quantity = getArguments().getString(TradeFragment.QUANTITY);
		
		mHeaderText =  (TextView) v.findViewById(R.id.header_txt);
		mHeaderText.setText(String.format("%s:%s", getArguments().getString(TradeFragment.EXCHANGE), 
				getArguments().getString(TradeFragment.SYMBOL)));
		
		mCommentsET = (EditText) v.findViewById(R.id.comments);
		mBtnConform = (Button) v.findViewById(R.id.right_button);
		mBtnConform.setText(R.string.btn_buy);
		mBtnConform.setVisibility(View.VISIBLE);
		
		mBuyDetails = (TextView) v.findViewById(R.id.buy_info);
		mBuyDetails.setText(getArguments().getString(TradeFragment.BUY_DETAIL_STR));
		//lastPrice = getArguments().getString(TradeFragment.LAST_PRICE);
		quantity = getArguments().getString(TradeFragment.QUANTITY);
		
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mBtnConform.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//requestToBuyRequiredSecurities();
				
				if(NetworkStatus.getInstance().isConnected(getActivity()))
					requestToGetBuyQuotes();
				else
					Util.show_toast(getActivity(), getResources().getString(R.string.network_error));
			}
		});
	}
	
	
	private void requestToGetBuyQuotes() {
		
		Token mToken = ((App)getActivity().getApplication()).getToken();
		
		AsyncHttpClient client = new AsyncHttpClient(); 
		String  authToken = Base64.encodeToString(mToken.getToken().getBytes(), Base64.DEFAULT);
		client.addHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE);
		client.addHeader(Constants.AUTHORIZATION, String.format("%s %s", Constants.TH_EMAIL_PREFIX, authToken));
		
		client.get(String.format(Config.getTrendNewBuyQuotes(), getArguments().getString(TradeFragment.EXCHANGE), 
				getArguments().getString(TradeFragment.SYMBOL)), new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, String response) {
				
				if(response.length() > 0) {
					
					String rawStr = JSONObject.quote(response.trim()); 
					rawStr = rawStr.replace(" ", "");
					setQuotes(rawStr);
					requestToBuyTransaction();
				}
				else
					Logger.log(TAG, "TH Quote response is blank", LogLevel.LOGGING_LEVEL_ERROR);
			}
			
			@Override
			public void onFailure(Throwable arg0, String response) {
				Logger.log(TAG, "Unable to get TH Quotes:\n"+response, LogLevel.LOGGING_LEVEL_ERROR);
			}
		});
	}
	
	
	private void requestToBuyTransaction() {
		
		Token mToken = ((App)getActivity().getApplication()).getToken();
		
		AsyncHttpClient client = new AsyncHttpClient(); 
		String  authToken = Base64.encodeToString(mToken.getToken().getBytes(), Base64.DEFAULT);
		client.addHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE);
		client.addHeader(Constants.AUTHORIZATION, String.format("%s %s", Constants.TH_EMAIL_PREFIX, authToken));
		
		RequestParams postParams = new RequestParams();
		postParams.put(BP_QUANTITY, quantity);
		postParams.put(BP_PORTFOLIO, ((App)getActivity().getApplication()).getProfileDTO().getPortfolio().getId());
		postParams.put(BP_SIGNED_QUOTE_DTO, getQuotes());
		postParams.put(BP_GEO_ALT, "");
		postParams.put(BP_GEO_LAT, "");
		postParams.put(BP_GEO_LONG, "");
		postParams.put(BP_IS_PUBLIC, "false");  //Yes = 1 No = 0
		postParams.put(BP_PUBLIST_TO_FB, "false"); //Yes = 1 No = 0
		postParams.put(BP_PUBLIST_TO_LI, "false"); //Yes = 1 No = 0
		postParams.put(BP_PUBLIST_TO_TW, "false"); //Yes = 1 No = 0
		
		//postParams.put(BP_TIMESTAMP, DateUtils.getCurrentTimeStampWithFormat());
		//postParams.put(BP_TRANSACTION_COST, String.valueOf(TradeFragment.TRANSACTION_COST));
		
		//postParams.put(BP_SIGNATURE, genarateSignatureUsingBuyParametersAndItsValues());
		//postParams.put(BP_YCSV_QUOTE_DATA, yahooQuoteStr);
		
		Logger.log(TAG, "--Post Params:--\n"+postParams.toString(), LogLevel.LOGGING_LEVEL_INFO);

		client.post(String.format(Config.getBuyNewTrend(), getArguments().getString(TradeFragment.EXCHANGE), 
				getArguments().getString(TradeFragment.SYMBOL)), postParams, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, String response) {
				
				Logger.log(TAG, response, LogLevel.LOGGING_LEVEL_INFO);
				
				try {
					JSONObject jsonObj = new JSONObject(response);
					Toast.makeText(getActivity(), jsonObj.optString("Message"), Toast.LENGTH_SHORT).show();
				} 
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable arg0, String response) {
				super.onFailure(arg0, response);
				Logger.log(TAG, response, LogLevel.LOGGING_LEVEL_ERROR);
				
				try {
					JSONObject jsonObj = new JSONObject(response);
					Toast.makeText(getActivity(), jsonObj.optString("Message"), Toast.LENGTH_SHORT).show();
				} 
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		
	}
	
	public String getQuotes() {
		return quotes;
	}

	public void setQuotes(String quotes) {
		this.quotes = quotes;
	}
	
	
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
