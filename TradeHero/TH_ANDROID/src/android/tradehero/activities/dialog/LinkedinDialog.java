package android.tradehero.activities.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.tradehero.activities.R;
import android.tradehero.fragments.InitialSignUpFragment;
import android.tradehero.utills.Constants;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;

import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;


public class LinkedinDialog extends Dialog {
	private ProgressDialog progressDialog = null;
	public static LinkedInApiClientFactory factory;
	public static LinkedInOAuthService oAuthService;
	public static LinkedInRequestToken liToken;
	private String scopeParams="r_basicprofile%20r_emailaddress%20r_network%20r_contactinfo%20rw_nus%20w_messages";

	/**
	 * Construct a new LinkedIn dialog
	 * 
	 * @param context
	 *            activity {@link Context}
	 * @param progressDialog
	 *            {@link ProgressDialog}
	 *            
	 *            
	 */

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		InitialSignUpFragment.linkedinButtonPressed = false;
	}
	public LinkedinDialog(Context context, ProgressDialog progressDialog) {
		super(context);
		this.progressDialog = progressDialog;
		

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// must call before super.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ln_dialog);
		setWebView();
	}

	/**
	 * set webview.
	 */
	private void setWebView()
	{
		LinkedinDialog.oAuthService = LinkedInOAuthServiceFactory.getInstance()
				.createLinkedInOAuthService(Constants.LINKEDIN_CONSUMER_KEY,
						Constants.LINKEDIN_CONSUMER_SECRET,scopeParams);
		LinkedinDialog.factory = LinkedInApiClientFactory.newInstance(
				Constants.LINKEDIN_CONSUMER_KEY, Constants.LINKEDIN_CONSUMER_SECRET);

		LinkedinDialog.liToken = LinkedinDialog.oAuthService
				.getOAuthRequestToken(Constants.OAUTH_CALLBACK_URL);

		WebView mWebView = (WebView) findViewById(R.id.webkitWebView1);
		mWebView.getSettings().setJavaScriptEnabled(true);

		Log.i("LinkedinSample", LinkedinDialog.liToken.getAuthorizationUrl());
		mWebView.loadUrl(LinkedinDialog.liToken.getAuthorizationUrl());
		mWebView.setWebViewClient(new HelloWebViewClient());

		mWebView.setPictureListener(new PictureListener() {
			@Override
			public void onNewPicture(WebView view, Picture picture) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

			}
		});

	}

	/**
	 * webview client for internal url loading
	 * callback url: "https://www.linkedin.com/uas/oauth/mukeshyadav4u.blogspot.in"
	 */
	public class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains(Constants.OAUTH_CALLBACK_URL))
			{
				Uri uri = Uri.parse(url);
				String verifier = uri.getQueryParameter("oauth_verifier");

				cancel();

				for (OnVerifyListener d : listeners) 
				{
					// call listener method
					d.onVerify(verifier);
				}
			} 
			else
			{ //if (url.contains(Constants.BASE_API_URL)){
				cancel();
			} /*else {

				view.loadUrl(url);
			}*/

			return true;
		}
	}

	/**
	 * List of listener.
	 */
	public List<OnVerifyListener> listeners = new ArrayList<OnVerifyListener>();

	/**
	 * Register a callback to be invoked when authentication have finished.
	 * 
	 * @param data
	 *            The callback that will run
	 */
	public void setVerifierListener(OnVerifyListener data) {
		listeners.add(data);
	}

	/**
	 * Listener for oauth_verifier.
	 */
	public interface OnVerifyListener {
		/**
		 * invoked when authentication have finished.
		 * 
		 * @param verifier
		 *            oauth_verifier code.
		 */
		public void onVerify(String verifier);
	}
}
