/**
 * BuyFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Aug 3, 2013
 */
package android.tradehero.fragments;

import java.security.SignatureException;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.tradehero.activities.R;
import android.tradehero.application.App;
import android.tradehero.application.Config;
import android.tradehero.utills.DateUtils;
import android.tradehero.utills.EncryptionUtils;
import android.tradehero.utills.Logger;
import android.tradehero.utills.Logger.LogLevel;
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
	
	private final static String SIGNATURE_KEY = "eyeofthetiger";
	
	private final static String BP_DISPLAY_PRICE = "displayPrice";
	private final static String BP_GEO_ALT = "geo_alt";
	private final static String BP_GEO_LAT = "geo_lat";
	private final static String BP_GEO_LONG = "geo_long";
	private final static String BP_IS_PUBLIC = "isPublic";
	private final static String BP_PORTFOLIO = "portfolio";
	private final static String BP_PUBLIST_TO_FB = "publishToFb";
	private final static String BP_PUBLIST_TO_LI = "publishToLi";
	private final static String BP_PUBLIST_TO_TW = "publishToTw";
	private final static String BP_QUANTITY = "quantity";
	private final static String BP_SIGNATURE = "signature";
	private final static String BP_TIMESTAMP = "timestamp";
	private final static String BP_TRANSACTION_COST = "transactioncost";
	private final static String BP_YCSV_QUOTE_DATA = "ycsvQuoteData";
	private final static String BP_TRADE_COMMENT = "tradeComment";
	
	private EditText mCommentsET;
	private Button mBtnConform;
	private TextView mBuyDetails;
	private TextView mHeaderText;
	
	private String lastPrice;
	private String quantity;
	private String yahooQuoteStr;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		view = inflater.inflate(R.layout.fragment_buy, container, false);
		initViews(view);
		return view;
	}
	
	private void initViews(View v) {
		
		lastPrice = getArguments().getString(TradeFragment.LAST_PRICE);
		quantity = getArguments().getString(TradeFragment.QUANTITY);
		
		mHeaderText =  (TextView) v.findViewById(R.id.header_txt);
		mHeaderText.setText(getArguments().getString(TradeFragment.HEADER));
		
		mCommentsET = (EditText) v.findViewById(R.id.comments);
		mBtnConform = (Button) v.findViewById(R.id.right_button);
		mBtnConform.setText(R.string.btn_buy);
		mBtnConform.setVisibility(View.VISIBLE);
		
		mBuyDetails = (TextView) v.findViewById(R.id.buy_info);
		mBuyDetails.setText(getArguments().getString(TradeFragment.BUY_DETAIL_STR));
		lastPrice = getArguments().getString(TradeFragment.LAST_PRICE);
		quantity = getArguments().getString(TradeFragment.QUANTITY);
		
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		yahooQuoteStr = ((App)getActivity().getApplication()).getYahooQuotesMap().toString();
		
		mBtnConform.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//genarateSignatureUsingBuyParametersAndItsValues();
				//String signature = genarateSignatureUsingBuyParametersAndItsValues();
				//LinkedHashMap<String, String> mPostParams = getBuyParametersWithValues();
				//mPostParams.put(BP_TRADE_COMMENT, mCommentsET.getText().toString());
				//mPostParams.put(BP_SIGNATURE, signature);
				
				requestToBuyRequiredSecurities();
			}
		});
	}
	
	private void requestToBuyRequiredSecurities() {
	
		AsyncHttpClient client = new AsyncHttpClient(); 
		RequestParams postParams = new RequestParams();

		postParams.put(BP_DISPLAY_PRICE, lastPrice);
		postParams.put(BP_GEO_ALT, "");
		postParams.put(BP_GEO_LAT, "");
		postParams.put(BP_GEO_LONG, "");
		postParams.put(BP_IS_PUBLIC, "0");  //Yes = 1 No = 0
		postParams.put(BP_PORTFOLIO, "240128");
		postParams.put(BP_PUBLIST_TO_FB, "0"); //Yes = 1 No = 0
		postParams.put(BP_PUBLIST_TO_LI, "0"); //Yes = 1 No = 0
		postParams.put(BP_PUBLIST_TO_TW, "0"); //Yes = 1 No = 0
		postParams.put(BP_QUANTITY, quantity);
		postParams.put(BP_SIGNATURE, genarateSignatureUsingBuyParametersAndItsValues());
		postParams.put(BP_TIMESTAMP, DateUtils.getCurrentTimeStampWithFormat());
		postParams.put(BP_TRANSACTION_COST, "10");
		postParams.put(BP_YCSV_QUOTE_DATA, yahooQuoteStr);

		
		Logger.log(TAG, "--Post Params:--\n"+postParams.toString(), LogLevel.LOGGING_LEVEL_INFO);
		client.post(Config.getBuyTrend(), postParams, new AsyncHttpResponseHandler() {
			
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
	
	
	private LinkedHashMap<String, String> getBuyParametersWithValues() {
		
		LinkedHashMap<String, String> mBuyParametersMap = new LinkedHashMap<String, String>();
		mBuyParametersMap.put(BP_DISPLAY_PRICE, lastPrice);
		mBuyParametersMap.put(BP_GEO_ALT, null);
		mBuyParametersMap.put(BP_GEO_LAT, null);
		mBuyParametersMap.put(BP_GEO_LONG, null);
		mBuyParametersMap.put(BP_IS_PUBLIC, "0");  //Yes = 1 No = 0
		mBuyParametersMap.put(BP_PORTFOLIO, "240128");
		mBuyParametersMap.put(BP_PUBLIST_TO_FB, "0"); //Yes = 1 No = 0
		mBuyParametersMap.put(BP_PUBLIST_TO_LI, "0"); //Yes = 1 No = 0
		mBuyParametersMap.put(BP_PUBLIST_TO_TW, "0"); //Yes = 1 No = 0
		mBuyParametersMap.put(BP_QUANTITY, quantity);
		//if(!isForSignature)
		//	mBuyParametersMap.put(BP_SIGNATURE, genarateSignatureUsingBuyParametersAndItsValues());
		mBuyParametersMap.put(BP_TIMESTAMP, DateUtils.getCurrentTimeStampWithFormat());
		mBuyParametersMap.put(BP_TRANSACTION_COST, "10");
		mBuyParametersMap.put(BP_YCSV_QUOTE_DATA, yahooQuoteStr);
		//mBuyParametersMap.put(BP_TRADE_COMMENT, "");
		
		return mBuyParametersMap;
	}
	
	private String genarateSignatureUsingBuyParametersAndItsValues() {
		
		String parameterKeyValueString = getBuyParametersWithValues().toString();
		String formatedParameterKeyValueString = "";
		String generatedSignature = "";
		
		if(parameterKeyValueString.length() > 0) {
			
			try {
				//Logger.log(TAG, "--parameterKeyValueString:--\n"+parameterKeyValueString, LogLevel.LOGGING_LEVEL_INFO);
				
				formatedParameterKeyValueString = parameterKeyValueString.replace(",", "&");
				//Logger.log(TAG, "--formatedParameterKeyValueString:--\n"+formatedParameterKeyValueString, LogLevel.LOGGING_LEVEL_INFO);
				
				formatedParameterKeyValueString = formatedParameterKeyValueString.replace("& ", "&");
				//Logger.log(TAG, "--formatedParameterKeyValueString:--\n"+formatedParameterKeyValueString, LogLevel.LOGGING_LEVEL_INFO);
				
				generatedSignature = EncryptionUtils.calculateRFC2104HMAC(formatedParameterKeyValueString, SIGNATURE_KEY);
				//Logger.log(TAG, "--generatedSignature:--\n"+generatedSignature, LogLevel.LOGGING_LEVEL_INFO);
			}
			catch (SignatureException e) {
				e.printStackTrace();
			}
		}
		
		return generatedSignature;
	}
}
