package android.tradehero.fragments;


import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.tradehero.activities.R;
import android.tradehero.activities.TwitterOAuthActivity;
import android.tradehero.activities.WebViewActivity;
import android.tradehero.activities.dialog.LinkedinDialog;
import android.tradehero.activities.dialog.LinkedinDialog.OnVerifyListener;
import android.tradehero.http.HttpRequestTask;
import android.tradehero.http.RequestFactory;
import android.tradehero.http.RequestTaskCompleteListener;
import android.tradehero.utills.Constants;
import android.tradehero.utills.Util;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;


public class InitialSignUpFragment extends Fragment implements OnClickListener,RequestTaskCompleteListener,OnTouchListener{

	public static final int LOGIN=90001;
	public static final int SIGNUP=90002;
	public static final int OP_FB=11111;
	public static final int OP_LINKEDIN=22222;
	public static final int OP_TWITTER=33333;
	private Button mFaceBookBtn,mTwitterBtn,mLinkedinBtn;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private TextView mEmailTv,mTerms;
	private TextView mBottomtxt,mHeaderBellowtxt;
	private Context mContext;
	private LayoutInflater inflater;
	private View v;
	private RequestTaskCompleteListener mRequestTaskCompleteListener;
	private ProgressDialog mProgressDialog;
	// Twitter
	private static Twitter twitter;
	private static RequestToken requestToken;

	// Shared Preferences
	private static SharedPreferences mSharedPreferences;

	private String mBottmLine,mHeader,mHeaderBellow;
	private int activityType;
	private int operationType;
	private Bundle mData;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction ;
	final LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory
			.getInstance().createLinkedInOAuthService(
					Constants.LINKEDIN_CONSUMER_KEY,
					Constants.LINKEDIN_CONSUMER_SECRET);
	final LinkedInApiClientFactory factory = LinkedInApiClientFactory
			.newInstance(Constants.LINKEDIN_CONSUMER_KEY,
					Constants.LINKEDIN_CONSUMER_SECRET);
	LinkedInRequestToken liToken;
	LinkedInApiClient client;
	LinkedInAccessToken accessToken = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {


		mSharedPreferences = getActivity().getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);

		View view = inflater.inflate(R.layout.sign_in_screen, container, false);
		_initSetup(view);

		return view;
	}

	private void _initSetup(View view){

		mData = getArguments();
		mRequestTaskCompleteListener= this;
		mBottmLine = mData.getString("BOTTOM_LINE");
		mHeader  = mData.getString("HEADER_LINE");
		mHeaderBellow  = mData.getString("HEADER_LINEBELLOW");
		activityType=mData.getInt("ACTIVITY_TYPE",0);
		inflater=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inflater.inflate(R.layout.topbar, null);
		mBottomtxt = (TextView)view.findViewById(R.id.txt_bottom);
		mHeaderBellowtxt = (TextView)view.findViewById(R.id.Sigin_with);
		mBottomtxt.setText(mBottmLine);
		mHeaderBellowtxt.setText(mHeaderBellow);
		TextView txt = (TextView) v.findViewById(R.id.header_txt);
		txt.setText(mHeader);
		ViewGroup header = (ViewGroup)view.findViewById(R.id.wraper2);
		header.addView(v);
		mFaceBookBtn = (Button)view.findViewById(R.id.btn_fbook_signin);
		mTwitterBtn = (Button)view.findViewById(R.id.btn_twitter_signin);
		mLinkedinBtn = (Button)view.findViewById(R.id.btn_linkedin_linkedin);
		mEmailTv = (TextView)view.findViewById(R.id.txt_email);
		mTerms = (TextView)view.findViewById(R.id.txt_termservice_signin);


		mFaceBookBtn.setOnClickListener(this);
		mTwitterBtn.setOnClickListener(this);
		mLinkedinBtn.setOnClickListener(this);
		mTerms.setOnClickListener(this);
		mEmailTv.setOnClickListener(this);

		mFaceBookBtn.setOnTouchListener(this);
		mTwitterBtn.setOnTouchListener(this);
		mLinkedinBtn.setOnTouchListener(this);
		//mTerms.setOnTouchListener(this);
		//mEmailTv.setOnTouchListener(this);

		mProgressDialog= new ProgressDialog(getActivity());
		mProgressDialog.setMessage("Logging In");
		mContext=getActivity();
		/** This if conditions is tested once is
		 * redirected from twitter page. Parse the uri to get oAuth
		 * Verifier
		 * */
		if (!isTwitterLoggedInAlready()) 
		{
			Uri uri = getActivity().getIntent().getData();
			if (uri != null && uri.toString().startsWith(Constants.TWITTER_CALLBACK_URL))
			{
				// oAuth verifier
				String verifier = uri.getQueryParameter(Constants.URL_TWITTER_OAUTH_VERIFIER);

				try {
					// Get the access token
					AccessToken accessToken = twitter.getOAuthAccessToken(
							requestToken, verifier);

					// Shared Preferences
					Editor e = mSharedPreferences.edit();

					// After getting access token, access token secret
					// store them in application preferences
					e.putString(Constants.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(Constants.PREF_KEY_OAUTH_SECRET,accessToken.getTokenSecret());
					// Store login status - true
					e.putBoolean(Constants.PREF_KEY_TWITTER_LOGIN, true);
					e.commit(); // save changes

					Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

					// Hide login button
					// btnLoginTwitter.setVisibility(View.GONE);

					// Show Update Twitter
					//lblUpdate.setVisibility(View.VISIBLE);
					//txtUpdate.setVisibility(View.VISIBLE);
					//btnUpdateStatus.setVisibility(View.VISIBLE);
					// btnLogoutTwitter.setVisibility(View.VISIBLE);

					// Getting user details from twitter
					// For now i am getting his name only
					long userID = accessToken.getUserId();
					User user = twitter.showUser(userID);
					String username = user.getName();

					// Displaying in xml ui
					//lblUserName.setText(Html.fromHtml("<b>Welcome " + username + "</b>"));
				} catch (Exception e) {
					// Check log for login errors
					Log.e("Twitter Login Error", "> " + e.getMessage());
				}
			}

		}
	}


	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_fbook_signin:
			operationType=OP_FB;
			onClickLoginFaceBook();
			break;
		case R.id.btn_twitter_signin:
			operationType=OP_TWITTER;
			loginToTwitter();
			break;
		case R.id.btn_linkedin_linkedin:
			operationType=OP_LINKEDIN;
			linkedInLogin();


			break;
		case R.id.txt_email:
			if(activityType==LOGIN)
			{
				//startActivity(new Intent(getActivity(),LoginActivity.class));
				fragmentManager = getActivity().getSupportFragmentManager();
				fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,0);
				LoginFragment fragment = new LoginFragment();
				fragmentTransaction.replace(R.id.sign_in_up_content, fragment,"login");
				fragmentTransaction.addToBackStack("login");
				fragmentTransaction.commit();
			}else
			{
				//startActivity(new Intent(getActivity(),EmailRegistrationActivity.class));
				fragmentManager = getActivity().getSupportFragmentManager();
				fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,0);
				EmailRegistrationFragment fragment = new EmailRegistrationFragment();
				fragmentTransaction.replace(R.id.sign_in_up_content, fragment,"email_registration");
				fragmentTransaction.addToBackStack("email_registration");
				fragmentTransaction.commit();
			}
			break;
		case R.id.txt_termservice_signin:
			Intent pWebView =new Intent(getActivity(),WebViewActivity.class);
			pWebView.putExtra(WebViewActivity.SHOW_URL, android.tradehero.utills.Constants.PRIVACY_TERMS_OF_SERVICE);
			startActivity(pWebView);
			break;

		default:
			break;
		}

	}




	@Override
	public void onTaskComplete(JSONObject pResponseObject) {
		// TODO Auto-generated method stub
		mProgressDialog.dismiss();
		Util.show_toast(mContext,"Login SuccessFul"+ pResponseObject.toString());

	}

	@Override
	public void onErrorOccured(int pErrorCode, String pErrorMessage) {
		// TODO Auto-generated method stub

	}

	@SuppressLint("ResourceAsColor")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			switch (v.getId()) {
			case R.id.btn_fbook_signin:
				mFaceBookBtn.setBackgroundResource(R.drawable.twit_rectangle);
				break;
			case R.id.btn_twitter_signin:
				mTwitterBtn.setBackgroundResource(R.drawable.linkedin_rectangle);
				break;
			case R.id.btn_linkedin_linkedin:
				mLinkedinBtn.setBackgroundResource(R.drawable.rectangle_fbook);
				break;

			default:
				break;
			}

			break;

		case MotionEvent.ACTION_UP:

			switch (v.getId()) {
			case R.id.btn_fbook_signin:
				mFaceBookBtn.setBackgroundResource(R.drawable.rectangle_fbook);

				break;
			case R.id.btn_twitter_signin:

				mTwitterBtn.setBackgroundResource(R.drawable.twit_rectangle);
				break;
			case R.id.btn_linkedin_linkedin:
				mLinkedinBtn.setBackgroundResource(R.drawable.linkedin_rectangle);
				break;

			default:
				break;
			}

			break;

		default:
			break;
		}


		return false;
	}

	@Override
	public void onStart() {
		super.onStart();
		if(Session.getActiveSession()!=null && statusCallback!=null && Session.getActiveSession()!=null)
		{
			Session.getActiveSession().addCallback(statusCallback);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if(Session.getActiveSession()!=null && statusCallback!=null&& Session.getActiveSession()!=null)
		{
			Session.getActiveSession().removeCallback(statusCallback);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode ==9)
		{

		}else{

			Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
		}
	}
	private void onClickLoginFaceBook() {
		Session session = Session.getActiveSession();
		if (session!=null &&!session.isOpened() && !session.isClosed()) 
		{
			session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
		}
		else
		{
			Session.openActiveSession(getActivity(), this, true, statusCallback);
		}
	}
	private void onClickLogout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed()) 
		{
			session.closeAndClearTokenInformation();
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			String accessToken = session.getAccessToken();
			HttpRequestTask  mRequestTask= new HttpRequestTask(mRequestTaskCompleteListener);
			RequestFactory mRF= new RequestFactory();
			android.tradehero.models.Request[] lRequests =new android.tradehero.models.Request[1];
			if(activityType==LOGIN){
				try {

					lRequests[0] = mRF.getLoginThroughFB(accessToken);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					lRequests[0] = mRF.getRegirstationThroughFB(accessToken);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mProgressDialog.show();
			mRequestTask.execute(lRequests);
		}
	}
	private void linkedInLogin() {
		ProgressDialog progressDialog = new ProgressDialog(getActivity());

		LinkedinDialog d = new LinkedinDialog(getActivity(),progressDialog);
		d.show();

		// set call back listener to get oauth_verifier value
		d.setVerifierListener(new OnVerifyListener() {
			@Override
			public void onVerify(String verifier) {
				try {
					Log.i("LinkedinSample", "verifier: " + verifier);

					accessToken = LinkedinDialog.oAuthService
							.getOAuthAccessToken(LinkedinDialog.liToken,
									verifier);
					LinkedinDialog.factory.createLinkedInApiClient(accessToken);
					client = factory.createLinkedInApiClient(accessToken);
					// client.postNetworkUpdate("Testing by Mukesh!!! LinkedIn wall post from Android app");
					Log.i("LinkedinSample",
							"ln_access_token: " + accessToken.getToken());
					Log.i("LinkedinSample",
							"ln_access_token: " + accessToken.getTokenSecret());
					Person p = client.getProfileForCurrentUser();
					/*name.setText("Welcome " + p.getFirstName() + " "
							+ p.getLastName());
					name.setVisibility(0);
					login.setVisibility(4);
					share.setVisibility(0);
					et.setVisibility(0);*/
					HttpRequestTask  mRequestTask= new HttpRequestTask(mRequestTaskCompleteListener);
					RequestFactory mRF= new RequestFactory();
					android.tradehero.models.Request[] lRequests =new android.tradehero.models.Request[1];
					if(activityType==LOGIN)
					{
						try {

							lRequests[0] = mRF.getLoginThroughLinkedIn(getActivity(),accessToken.getTokenSecret(), accessToken.getToken());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else
					{
						try {
							lRequests[0] = mRF.getRegirstationThroughLinkedIn(getActivity(),accessToken.getTokenSecret(), accessToken.getToken());

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					mProgressDialog.show();
					mRequestTask.execute(lRequests);


				} catch (Exception e) {
					Log.i("LinkedinSample", "error to get verifier");
					e.printStackTrace();
				}
			}
		});

		// set progress dialog
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(true);
		progressDialog.show();
	}
	/**
	 * Function to login twitter
	 * */
	private void loginToTwitter() {
		// Check if already logged in
		if (!isTwitterLoggedInAlready()) 
		{
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET);
			Configuration configuration = builder.build();

			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter
						.getOAuthRequestToken(Constants.TWITTER_CALLBACK_URL);
				this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse(requestToken.getAuthenticationURL())));
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
			// user already logged into twitter
			Util.show_toast(getActivity(), "Already Logged into twitter");

		}
	}

	/**
	 * Check user already logged in your application using twitter Login flag is
	 * fetched from Shared Preferences
	 * */
	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreferences.getBoolean(Constants.PREF_KEY_TWITTER_LOGIN, false);
	}


}
