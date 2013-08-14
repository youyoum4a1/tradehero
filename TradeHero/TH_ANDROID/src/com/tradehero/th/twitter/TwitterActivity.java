package com.tradehero.th.twitter;

import com.tradehero.th.models.Request;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.tradehero.th.R;
import com.tradehero.th.activities.TradeHeroTabActivity;
import com.tradehero.th.application.App;
import com.tradehero.th.fragments.authentication.InitialSignUpFragment;
import com.tradehero.th.http.HttpRequestTask;
import com.tradehero.th.http.RequestFactory;
import com.tradehero.th.http.RequestTaskCompleteListener;
import com.tradehero.th.models.ProfileDTO;
import com.tradehero.th.utills.Constants;
import com.tradehero.th.utills.PUtills;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class TwitterActivity extends Activity implements RequestTaskCompleteListener
{
    private static final String TAG = Twitter.TAG;
    private static final boolean DEBUG = Twitter.DEBUG;
    private ProgressDialog mProgressDialog;
    private static final int RETRIEVE_REQUEST_TOKEN = 1;
    private static final int RETRIEVE_ACCESS_TOKEN = 2;

    private static final String KEY_URL = "url";
    private static SharedPreferences mSharedPreferences;
    private H mMainThreadHandler;
    private RequestTaskCompleteListener mRequestTaskCompleteListener;
    private OAuthConsumer mConsumer;
    private OAuthProvider mProvider;
    private String twitter_email;
    private ProgressDialog mSpinner;
    private WebView mWebView;
    private Thread thread;

    /**
     * Handler to run shit on the UI thread
     *
     * @author Grantland Chew
     */
    private class H extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle data = msg.getData();

            switch (msg.what)
            {
                case RETRIEVE_REQUEST_TOKEN:
                {
                    mWebView.loadUrl(data.getString(KEY_URL));
                }
                break;
                case RETRIEVE_ACCESS_TOKEN:
                {

                }
                break;
                default:
                {

                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.twitter_layout);
        twitter_email = InitialSignUpFragment.email;
        Intent intent = getIntent();
        mConsumer = (OAuthConsumer) intent.getSerializableExtra(Twitter.EXTRA_CONSUMER);
        String authorizeParams = intent.getStringExtra(Twitter.EXTRA_AUTHORIZE_PARAMS);
        mSharedPreferences =
                this.getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Logging In");
        mMainThreadHandler = new H();
        mRequestTaskCompleteListener = this;
        mProvider = new CommonsHttpOAuthProvider(
                Twitter.REQUEST_TOKEN,
                Twitter.ACCESS_TOKEN,
                Twitter.AUTHORIZE + authorizeParams);
        mProvider.setOAuth10a(true);
        mSpinner = new ProgressDialog(this);
        mSpinner.setMessage(getResources().getString(R.string.loading_loading));
        mSpinner.setOnCancelListener(new OnCancelListener()
        {
            @Override public void onCancel(DialogInterface dialog)
            {
                cancel();
            }
        });

        mWebView = (WebView) findViewById(R.id.twitter_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSaveFormData(false);
        mWebView.getSettings().setSavePassword(false);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setWebViewClient(new TwitterWebViewClient());

        thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    Message msg = new Message();
                    msg.what = RETRIEVE_REQUEST_TOKEN;

                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_URL,
                            mProvider.retrieveRequestToken(mConsumer, Twitter.CALLBACK_URI));
                    msg.setData(bundle);

                    if (DEBUG) Log.d(TAG, "url: " + bundle.getString(KEY_URL));
                    mMainThreadHandler.sendMessage(msg);
                } catch (OAuthException e)
                {
                    error(e);
                }
            }
        };

        thread.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (KeyEvent.KEYCODE_BACK == keyCode)
        {
            cancel();
            return true; // consume event
        }

        return false; // don't consume event
    }

    private void error(Throwable error)
    {
        Intent intent = this.getIntent();
        intent.putExtra(Twitter.EXTRA_ERROR, error.getMessage());
        this.setResult(RESULT_OK, intent);
        finish();
    }

    private void cancel()
    {
        Intent intent = this.getIntent();
        this.setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void complete(String accessKey, String accessSecret)
    {
        /*Intent intent = this.getIntent();
        intent.putExtra(Twitter.EXTRA_ACCESS_KEY, accessKey);
        intent.putExtra(Twitter.EXTRA_ACCESS_SECRET, accessSecret);
        this.setResult(RESULT_OK, intent);*/

        Editor e = mSharedPreferences.edit();

        // After getting access token, access token secret
        // store them in application preferences
        e.putString(Constants.PREF_KEY_OAUTH_TOKEN, accessKey);
        e.putString(Constants.PREF_KEY_OAUTH_SECRET, accessSecret);
        // Store login status - true
        e.putBoolean(Constants.PREF_KEY_TWITTER_LOGIN, true);
        e.commit(); // save changes

        HttpRequestTask mRequestTask = new HttpRequestTask(mRequestTaskCompleteListener);
        RequestFactory mRF = new RequestFactory();
        Request[] lRequests = new Request[1];
        try
        {
            lRequests[0] = mRF.getRegistrationThroughTwitter(twitter_email,
                    mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_SECRET, null),
                    mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_TOKEN, null));
        } catch (JSONException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        //mProgressDialog.show();
        mRequestTask.execute(lRequests);
    }

    private void retrieveAccessToken(final Uri uri)
    {
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    if (DEBUG) Log.d(TAG, uri.toString());
                    String oauth_token = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
                    String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
                    if (DEBUG) Log.d(TAG, oauth_token);
                    if (DEBUG) Log.d(TAG, verifier);

                    mConsumer.setTokenWithSecret(oauth_token, mConsumer.getConsumerSecret());
                    mProvider.retrieveAccessToken(mConsumer, verifier);

                    complete(mConsumer.getToken(), mConsumer.getTokenSecret());
                } catch (OAuthException e)
                {
                    error(e);
                }
            }
        };
        thread.start();
    }

    private class TwitterWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            if (DEBUG) Log.d(TAG, url);
            Uri uri = Uri.parse(url);
            if (uri != null && Twitter.CALLBACK_SCHEME.equals(uri.getScheme()))
            {
                String denied = uri.getQueryParameter(Twitter.DENIED);

                if (denied != null)
                {
                    cancel();
                }
                else
                {
                    retrieveAccessToken(uri);
                }

                return true;
            }
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            super.onPageStarted(view, url, favicon);
            if (DEBUG) Log.d(TAG, "Webview loading URL: " + url);
            if (view.getVisibility() != View.INVISIBLE && !mSpinner.isShowing())
            {
                mSpinner.show();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            mSpinner.dismiss();
            view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl)
        {
            super.onReceivedError(view, errorCode, description, failingUrl);
            error(new TwitterError(description, errorCode, failingUrl));
        }
    }

    @Override
    public void onTaskComplete(JSONObject pResponseObject)
    {
        mProgressDialog.dismiss();
        //Util.show_toast(this,"Login SuccessFul"+ pResponseObject.toString());
        System.out.println("login---" + pResponseObject.toString());
        try
        {
            if (pResponseObject != null)
            {//JSONObject obj = pResponseObject.getJSONObject("profileDTO");
                ProfileDTO prof = new PUtills(this)._parseJson(pResponseObject);
                ((App) this.getApplication()).setProfileDTO(prof);
                startActivity(new Intent(TwitterActivity.this, TradeHeroTabActivity.class));
            }
            finish();
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorOccured(int pErrorCode, String pErrorMessage)
    {
        // TODO Auto-generated method stub

    }

    ;
}
