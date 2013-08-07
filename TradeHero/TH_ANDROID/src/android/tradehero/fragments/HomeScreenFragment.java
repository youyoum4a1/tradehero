package android.tradehero.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.tradehero.activities.R;
import android.tradehero.adapters.TradeWeekAdapter;
import android.tradehero.application.App;
import android.tradehero.application.ConvolutionMatrix;
import android.tradehero.cache.ImageLoader;
import android.tradehero.http.RequestTaskCompleteListener;
import android.tradehero.models.Medias;
import android.tradehero.models.ProfileDTO;
import android.tradehero.models.Request;
import android.tradehero.models.Token;
import android.tradehero.models.TradeofWeek;
import android.tradehero.utills.Constants;
import android.tradehero.utills.Util;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;


public class HomeScreenFragment extends Fragment implements OnClickListener,RequestTaskCompleteListener{

	private ImageView mUserImg;
	private TextView txtUserName;
	private String mUserName;
	private ListView mListviewContent;
	private LinearLayout mBagroundImage;
	private ProfileDTO profile;
	private String picture ;
	private BitmapDrawable drawableBitmap;
	private Bitmap mBitmap,mBGBtmp;
	private String id;
	private ProgressDialog mProgressDialog;
	private ProgressBar listview_content_progress;
	private Request lLoginRequest;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.profile_screen, container, false);
		_initView(view);
		lLoginRequest = new Request();

		return view;
	}


	private void _initView(View view) {


		mProgressDialog= new ProgressDialog(getActivity());
		mProgressDialog.setMessage(getResources().getString(R.string.loading_loading));
		listview_content_progress =  (ProgressBar)view.findViewById(R.id.progressbar_tradeofweek);
		mListviewContent = (ListView)view.findViewById(R.id.list_user_content);
		txtUserName = (TextView)view.findViewById(R.id.header_txt_homescreen);
		profile = ((App)getActivity().getApplication()).getProfileDTO();
		
		if(profile != null)
		{
			
			String mUserName = profile.getDisplayName();
			picture = profile.getPicture();
			id = profile.getId();
			txtUserName.setText(mUserName);
			mBagroundImage = (LinearLayout) view.findViewById(R.id.top_layout);
			mUserImg = (ImageView)view.findViewById(R.id.img_banner_user);
			new UpdateUi().execute();
			_getDataOfTrade();
		}
		

	}


	public static Bitmap applyGaussianBlur(Bitmap src) {
		double[][] GaussianBlurConfig = new double[][] {
				{ 1, 2, 1 },
				{ 2, 4, 2 },
				{ 1, 2, 1 }
		};
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(GaussianBlurConfig);
		convMatrix.Factor = 27;
		convMatrix.Offset = 0;
		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	}



	class UpdateUi extends AsyncTask<Void, Void, Void>
	{
		ImageLoader imgLoader;
		ProgressDialog dlg;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			imgLoader = new ImageLoader(getActivity());
			dlg = new ProgressDialog(getActivity());
			dlg.setMessage(getResources().getString(R.string.loading_loading));
			dlg.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			mBitmap = imgLoader.getBitmap(picture);
			mBGBtmp = applyGaussianBlur(imgLoader.getBitmap(picture));

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

			mUserImg.setImageBitmap( Util.getRoundedShape(mBitmap));
			drawableBitmap=new BitmapDrawable(applyGaussianBlur(mBGBtmp));
			mBagroundImage.setBackgroundDrawable(drawableBitmap);

			if(dlg.isShowing())
			{
				dlg.cancel();
			}

			super.onPostExecute(result);
		}

	}

	@Override
	public void onTaskComplete(JSONObject pResponseObject) {
		//mProgressDialog.dismiss();
		System.out.println("botm line----"+pResponseObject.toString());
		//Util.show_toast(getActivity(), pResponseObject.toString());

	}


	@Override
	public void onErrorOccured(int pErrorCode, String pErrorMessage) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}




	private void _getDataOfTrade()
	{
		Token token = ((App)getActivity().getApplication()).getToken();

		if(token!= null)
		{
			String tokenData = token.getToken();
			String  mytoken = Base64.encodeToString(tokenData.getBytes(), Base64.DEFAULT);
			System.out.println("my toke ------"+ mytoken);
			AsyncHttpClient client = new AsyncHttpClient(); 
			client.addHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE);
			client.addHeader(Constants.AUTHORIZATION, Constants.TH_EMAIL_PREFIX+" "+mytoken);
			client.get(Constants.SIGN_UP_WITH_SOCIAL_MEDIA_USER_URL+"/"+id+Constants.TH_TRADE_WEEK_POSTFIX, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(String response) {

					Util.show_toast(getActivity(), response);
					parseResponse(response);
					listview_content_progress.setVisibility(View.INVISIBLE);
					mListviewContent.setVisibility(View.VISIBLE);
				}

				@Override
				public void onFailure(Throwable arg0, String arg1) {

					listview_content_progress.setVisibility(View.INVISIBLE);
					mListviewContent.setVisibility(View.INVISIBLE);
					Util.show_toast(getActivity(), arg1);

				}
			});
		}

	}

	private void parseResponse(String response)
	{
		ArrayList<TradeofWeek> tradweekList = new ArrayList<TradeofWeek>();
		TradeofWeek mTradeWeek = null;
		Medias objMedia = null;
		try {
			JSONObject obj = new JSONObject(response);

			JSONArray mJsonArray = obj.getJSONArray("enhancedItems");

			for (int i = 0; i < mJsonArray.length(); i++) {

				mTradeWeek = new TradeofWeek();
				JSONObject mobj = mJsonArray.getJSONObject(i);

				String _id = mobj.getString("id");
				String _createdAtUtc = mobj.getString("createdAtUtc");
				String _userId = mobj.getString("userId");
				String _text = mobj.getString("text");
				//String _pushTypeId = mobj.getString("pushTypeId");
				JSONArray mediajson = mobj.getJSONArray("medias");
				for (int j = 0; j < mediajson.length(); j++) {

					objMedia = new Medias();
					JSONObject mediajobj = mediajson.getJSONObject(j);
					String  _securityId = mediajobj.getString("securityId");
					String  _exchange = mediajobj.getString("exchange");
					String  _symbol = mediajobj.getString("symbol");
					String  _url = mediajobj.getString("url");
					String  _type = mediajobj.getString("type");
					objMedia.setExchange(_exchange);
					objMedia.setSecurityId(_securityId);
					objMedia.setUrl(_url);
					objMedia.setType(_type);
					objMedia.setSymbol(_symbol);
				}
				mTradeWeek.setCreatedAtUtc(_createdAtUtc);
				mTradeWeek.setId(_id);
				mTradeWeek.setUserId(_userId);
				mTradeWeek.setMedias(objMedia);	
				mTradeWeek.setText(_text);
				//mTradeWeek.setPushTypeId(_pushTypeId);
				tradweekList.add(mTradeWeek);

			}

			System.out.println("Trade week size======="+tradweekList.size());

			mListviewContent.setAdapter(new TradeWeekAdapter(getActivity(), tradweekList));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
