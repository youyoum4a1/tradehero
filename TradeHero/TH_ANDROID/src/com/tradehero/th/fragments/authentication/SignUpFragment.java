package com.tradehero.th.fragments.authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.dialog.LinkedinDialog;
import com.tradehero.th.activities.dialog.LinkedinDialog.OnVerifyListener;
import com.tradehero.th.application.App;
import com.tradehero.th.http.HttpRequestTask;
import com.tradehero.th.http.RequestFactory;
import com.tradehero.th.http.RequestTaskCompleteListener;
import com.tradehero.th.models.ProfileDTO;
import com.tradehero.th.models.Request;
import com.tradehero.th.networkstatus.NetworkStatus;
import com.tradehero.th.utills.Constants;
import com.tradehero.th.utills.PUtills;
import com.tradehero.th.utills.Util;
import com.tradehero.th.webbrowser.WebViewActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUpFragment extends AuthenticationFragment
        implements OnClickListener, RequestTaskCompleteListener
{

    public static final int LOGIN = 90001;
    public static int mCurCheckPosition = 2;
    public static final int SIGNUP = 90002;
    public static final int OP_FB = 11111;
    public static final int OP_LINKEDIN = 22222;
    public static final int OP_TWITTER = 33333;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private TextView mBottomtxt, mHeaderBellowtxt;
    private Context mContext;
    private LayoutInflater inflater;
    private View v;
    private RequestTaskCompleteListener mRequestTaskCompleteListener;
    private ProgressDialog mProgressDialog;
    // Shared Preferences
    private static SharedPreferences mSharedPreferences;
    private String mBottmLine, mHeader, mHeaderBellow;
    private int activityType;
    private int operationType;
    private Bundle mData;
    public static boolean linkedinButtonPressed = false;
    //public static boolean twitterButtonPressed= false;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
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
    public static ProgressDialog linkedinProgress;
    PackageInfo info;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {

        mSharedPreferences = getActivity().getApplicationContext()
                .getSharedPreferences(Constants.SHARED_PREF, 0);

        View view = inflater.inflate(R.layout.authentication_sign_up, container, false);
        _initSetup(view);

        return view;
    }

    private void _initSetup(View view)
    {

        mRequestTaskCompleteListener = this;

        view.findViewById(R.id.btn_facebook_signin).setOnClickListener(onClickListener);
        view.findViewById(R.id.txt_email_sign_up).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_twitter_signin).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_linkedin_signin).setOnClickListener(this);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_loading));
        mContext = getActivity();
        /** This if conditions is tested once is
         * redirected from twitter page. Parse the uri to get oAuth
         * Verifier
         * */
        linkedinProgress = new ProgressDialog(getActivity());
        linkedinProgress.setMessage(getResources().getString(R.string.loading_loading));
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.btn_linkedin_signin:

                if (!linkedinButtonPressed)
                {
                    if (NetworkStatus.getInstance().isConnected(getActivity()))
                    {
                        //linkedinProgress.show();
                        operationType = OP_LINKEDIN;
                        linkedInLogin();
                    }
                    else
                    {
                        Util.show_toast(getActivity(),
                                getResources().getString(R.string.network_error));
                    }
                }
                else
                {
                    Util.show_toast(getActivity(),
                            getResources().getString(R.string.loading_loading));
                }

                break;
            case R.id.txt_term_of_service_signin:
                Intent pWebView = new Intent(getActivity(), WebViewActivity.class);
                pWebView.putExtra(WebViewActivity.SHOW_URL,
                        Constants.PRIVACY_TERMS_OF_SERVICE);
                getActivity().startActivity(pWebView);
                break;

            default:
                break;
        }
    }

    @Override
    public void onTaskComplete(JSONObject pResponseObject)
    {
        if (mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
        }
        if (linkedinButtonPressed)
        {
            linkedinButtonPressed = false;
        }

        if (pResponseObject != null)
        {
            System.out.println("login---" + pResponseObject.toString());
            try
            {

                if (pResponseObject.has("Message"))
                {
                    String msg = pResponseObject.optString("Message");
                    Util.show_toast(getActivity(), msg);
                }
                else
                {

                    if (activityType == LOGIN)
                    {

                        JSONObject obj = pResponseObject.getJSONObject("profileDTO");

                        ProfileDTO prof = new PUtills(getActivity())._parseJson(obj);

                        ((App) getActivity().getApplication()).setProfileDTO(prof);
                        startActivity(new Intent(getActivity(), DashboardActivity.class));
                        getActivity().finish();
                    }
                    else if (activityType == SIGNUP)
                    {

                        //JSONObject obj = pResponseObject.getJSONObject("profileDTO");

                        ProfileDTO prof = new PUtills(getActivity())._parseJson(pResponseObject);

                        ((App) getActivity().getApplication()).setProfileDTO(prof);
                        startActivity(new Intent(getActivity(), DashboardActivity.class));
                        getActivity().finish();
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onErrorOccured(int pErrorCode, String pErrorMessage)
    {

    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (Session.getActiveSession() != null
                && statusCallback != null
                && Session.getActiveSession() != null)
        {
            Session.getActiveSession().addCallback(statusCallback);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (Session.getActiveSession() != null
                && statusCallback != null
                && Session.getActiveSession() != null)
        {
            Session.getActiveSession().removeCallback(statusCallback);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9)
        {

        }
        else
        {

            Session.getActiveSession()
                    .onActivityResult(getActivity(), requestCode, resultCode, data);
        }
    }

    private void onClickLoginFaceBook()
    {
        Session session = Session.getActiveSession();

        if (session != null && !session.isOpened() && !session.isClosed())
        {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        }
        else
        {
            Session.openActiveSession(getActivity(), this, true, statusCallback);
        }
    }

    private void onClickLogout()
    {
        Session session = Session.getActiveSession();
        if (!session.isClosed())
        {
            session.closeAndClearTokenInformation();
        }
    }

    private class SessionStatusCallback implements Session.StatusCallback
    {
        @Override
        public void call(Session session, SessionState state, Exception exception)
        {
            String accessToken = session.getAccessToken();

            if (!accessToken.equals(""))
            {
                HttpRequestTask mRequestTask = new HttpRequestTask(mRequestTaskCompleteListener);
                RequestFactory mRF = new RequestFactory();
                Request[] lRequests = new Request[1];
                if (activityType == LOGIN)
                {
                    try
                    {

                        lRequests[0] = mRF.getLoginThroughFB(accessToken);
                        mProgressDialog.show();
                        mRequestTask.execute(lRequests);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (activityType == SIGNUP)
                {
                    try
                    {
                        lRequests[0] = mRF.getRegistrationThroughFB(accessToken);
                        mProgressDialog.show();
                        mRequestTask.execute(lRequests);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void linkedInLogin()
    {

        linkedinButtonPressed = true;
        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        LinkedinDialog d = new LinkedinDialog(getActivity(), progressDialog);
        d.show();

        // set call back listener to get oauth_verifier value
        d.setVerifierListener(new OnVerifyListener()
        {
            @Override
            public void onVerify(String verifier)
            {
                try
                {
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
                    //					name.setText("Welcome " + p.getFirstName() + " "
                    //							+ p.getLastName());
                    //					name.setVisibility(0);
                    //					login.setVisibility(4);
                    //					share.setVisibility(0);
                    //					et.setVisibility(0);
                    HttpRequestTask mRequestTask =
                            new HttpRequestTask(mRequestTaskCompleteListener);
                    RequestFactory mRF = new RequestFactory();
                    Request[] lRequests = new Request[1];
                    if (activityType == LOGIN)
                    {
                        try
                        {

                            lRequests[0] = mRF.getLoginThroughLinkedIn(getActivity(),
                                    accessToken.getTokenSecret(), accessToken.getToken());
                            mProgressDialog.show();
                            mRequestTask.execute(lRequests);
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if (activityType == SIGNUP)
                    {
                        try
                        {
                            lRequests[0] = mRF.getRegistrationThroughLinkedIn(getActivity(),
                                    accessToken.getTokenSecret(), accessToken.getToken());
                            mProgressDialog.show();
                            mRequestTask.execute(lRequests);
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e)
                {
                    Log.i("LinkedinSample", "error to get verifier");
                    e.printStackTrace();
                }
            }
        });

        // set progress dialog
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        //new Linkedin().execute();

    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

	/*public void linkedinclick(View v){

	if(v.getId() == R.id.btn_linkedin_linkedin)
	{
		Util.show_toast(getActivity(), "alok");
	}

}*/

	/*class Linkedin extends AsyncTask<Void, Void, Void>{
        ProgressDialog progressDialog;
		LinkedinDialog d;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progressDialog = new ProgressDialog(getActivity());
			// set progress dialog
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(true);
			progressDialog.show();
			d = new LinkedinDialog(getActivity(),progressDialog);
			d.show();

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
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
						name.setText("Welcome " + p.getFirstName() + " "
								+ p.getLastName());
						name.setVisibility(0);
						login.setVisibility(4);
						share.setVisibility(0);
						et.setVisibility(0);
						HttpRequestTask  mRequestTask= new HttpRequestTask(mRequestTaskCompleteListener);
						RequestFactory mRF= new RequestFactory();
						com.tradehero.models.Request[] lRequests =new com.tradehero.models.Request[1];
						if(activityType==LOGIN)
						{
							try {

								lRequests[0] = mRF.getLoginThroughLinkedIn(getActivity(),accessToken.getTokenSecret(), accessToken.getToken());
								mProgressDialog.show();
								mRequestTask.execute(lRequests);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						else if(activityType == SIGNUP)
						{
							try {
								lRequests[0] = mRF.getRegistrationThroughLinkedIn(getActivity(),accessToken.getTokenSecret(), accessToken.getToken());
								mProgressDialog.show();
								mRequestTask.execute(lRequests);

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}


					} catch (Exception e) {
						Log.i("LinkedinSample", "error to get verifier");
						e.printStackTrace();
					}
				}
			});
			return null;
		}

	}*/
}
