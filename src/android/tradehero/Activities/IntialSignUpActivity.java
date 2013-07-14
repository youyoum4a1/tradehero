package android.tradehero.Activities;

import org.brickred.socialauth.Profile;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.tradehero.Http.HttpRequestTask;
import android.tradehero.Http.RequestFactory;
import android.tradehero.Http.RequestTaskCompleteListener;
import android.tradehero.Models.Request;
import android.tradehero.SocailAuth.DialogListener;
import android.tradehero.SocailAuth.SocialAuthAdapter;
import android.tradehero.SocailAuth.SocialAuthAdapter.Provider;
import android.tradehero.SocailAuth.SocialAuthError;
import android.tradehero.SocailAuth.SocialAuthListener;
import android.tradehero.Utills.Util;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class IntialSignUpActivity extends Activity implements OnClickListener,RequestTaskCompleteListener{

	public static final int LOGIN=90001;
	public static final int SIGNUP=90002;
	public static final int OP_FB=11111;
	public static final int OP_LINKEDIN=22222;
	public static final int OP_TWITTER=33333;
	private Button mFaceBookBtn,mTwitterBtn,mLinkedinBtn;
	private TextView mEmailTv,mTerms;
	private TextView mBottomtxt,mHeaderBellowtxt;
	private SocialAuthAdapter mSocialAuthAdapter;
	private Profile mProfileMap;
	private Context mContext;
	private LayoutInflater inflater;
	private View v;
    private RequestTaskCompleteListener mRequestTaskCompleteListener;
    private ProgressDialog mProgressDialog;


	private String mBottmLine,mHeader,mHeaderBellow;
	private int activityType;
	private int operationType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		_initSetup();
	}

	private void _initSetup(){
		setContentView(R.layout.sign_in_screen);
		mRequestTaskCompleteListener=this;
		mBottmLine = getIntent().getStringExtra("BOTTOM_LINE");
		mHeader  = getIntent().getStringExtra("HEADER_LINE");
		mHeaderBellow  = getIntent().getStringExtra("HEADER_LINEBELLOW");
		activityType=getIntent().getIntExtra("ACTIVITY_TYPE",0);
		inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inflater.inflate(R.layout.topbar, null);
		mBottomtxt = (TextView) findViewById(R.id.txt_bottom);
		mHeaderBellowtxt = (TextView)findViewById(R.id.Sigin_with);
		mBottomtxt.setText(mBottmLine);
		mHeaderBellowtxt.setText(mHeaderBellow);
		TextView txt = (TextView) v.findViewById(R.id.header_txt);
		txt.setText(mHeader);
		ViewGroup header = (ViewGroup)findViewById(R.id.wraper2);
		header.addView(v);
		mFaceBookBtn = (Button) findViewById(R.id.btn_fbook_signin);
		mTwitterBtn = (Button) findViewById(R.id.btn_twitter_signin);
		mLinkedinBtn = (Button) findViewById(R.id.btn_linkedin_linkedin);
		mEmailTv = (TextView) findViewById(R.id.txt_email);
		mTerms = (TextView) findViewById(R.id.txt_termservice_signin);

		mSocialAuthAdapter = new SocialAuthAdapter(new ResponseListener());
		mFaceBookBtn.setOnClickListener(this);
		mTwitterBtn.setOnClickListener(this);
		mLinkedinBtn.setOnClickListener(this);
		mTerms.setOnClickListener(this);
		mEmailTv.setOnClickListener(this);
		mProgressDialog= new ProgressDialog(this);
		mProgressDialog.setMessage("Logging In");
		mContext=this;


	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_fbook_signin:
			mSocialAuthAdapter.addCallBack(Provider.FACEBOOK,"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
			//startActivity(new Intent(New_user_Activity.this,Login_Activity.class));
			mSocialAuthAdapter.authorize(this, Provider.FACEBOOK);
			operationType=OP_FB;
			break;
		case R.id.btn_twitter_signin:
			//startActivity(new Intent(New_user_Activity.this,New_user_Activity.class));
			mSocialAuthAdapter.addCallBack(Provider.TWITTER,"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
			mSocialAuthAdapter.authorize(this, Provider.TWITTER);
			operationType=OP_TWITTER;
			break;
		case R.id.btn_linkedin_linkedin:
			mSocialAuthAdapter.addCallBack(Provider.LINKEDIN,"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
			mSocialAuthAdapter.authorize(this, Provider.LINKEDIN);
			operationType=OP_LINKEDIN;
			break;
		case R.id.txt_email:
			if(activityType==LOGIN){
				startActivity(new Intent(IntialSignUpActivity.this,LoginActivity.class));
			}else{
				startActivity(new Intent(IntialSignUpActivity.this,EmailRegistrationActivity.class));
			}
			break;
		case R.id.txt_termservice_signin:
			Intent pWebView =new Intent(IntialSignUpActivity.this,WebViewActivity.class);
			pWebView.putExtra(WebViewActivity.SHOW_URL, android.tradehero.Utills.Constants.PRIVACY_TERMS_OF_SERVICE);
			startActivity(pWebView);
			break;

		default:
			break;
		}

	}


	private class ResponseListener implements DialogListener{


		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			mSocialAuthAdapter.getUserProfileAsync(new ProfileDataListener());


		}

		@Override
		public void onError(SocialAuthError e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBack() {
			// TODO Auto-generated method stub

		}


		// To receive the profile response after authentication
		private final class ProfileDataListener implements SocialAuthListener<Profile> {

			@Override
			public void onExecute(Profile t) {
				mProfileMap = t;
				mProgressDialog.show();
				HttpRequestTask  mRequestTask= new HttpRequestTask(mRequestTaskCompleteListener);
				RequestFactory mRF= new RequestFactory();
				Request[] lRequests =new Request[1];
				if(activityType==LOGIN){
					switch (operationType) {
					case OP_FB:
						
						

						try {
							lRequests[0] = mRF.getLoginThroughFB(t.getValidatedId());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						break;
					case OP_LINKEDIN:
						try {
							lRequests[0] = mRF.getLoginThroughLinkedIn();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						break;	
					case OP_TWITTER	:
						try {
							lRequests[0] = mRF.getLoginThroughTwiiter();
						} catch (JSONException e) {
							
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					default:
						break;
					}
					
				}else{
					switch (operationType) {
					case OP_FB:
						try {
							lRequests[0] = mRF.getRegirstationThroughFB(t.getValidatedId());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case OP_LINKEDIN:
						try {
							lRequests[0] = mRF.getRegirstationThroughLinkedIn(t.getProviderId(),t.getValidatedId());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;	
					case OP_TWITTER	:
						try {
							lRequests[0] = mRF.getRegirstationThroughTwitter(t.getProviderId(),t.getValidatedId());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					default:
						break;
					}


				}
				if(lRequests.length>0){
					
					mRequestTask.execute(lRequests);
				}
				Util.show_toast(mContext,"profile fetched"+mProfileMap.getDisplayName() );
			}

			@Override
			public void onError(SocialAuthError e) {
				Util.show_toast(mContext,"Ettt" +e.getMessage());

			}
		}


	}


	@Override
	public void onTaskComplete(JSONObject pResponseObject) {
		// TODO Auto-generated method stub
		mProgressDialog.dismiss();
		Util.show_toast(mContext,"Login SuccessFul");

	}

	@Override
	public void onErrorOccured(int pErrorCode, String pErrorMessage) {
		// TODO Auto-generated method stub

	}

}
