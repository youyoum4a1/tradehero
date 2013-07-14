package android.tradehero.Activities;

import org.brickred.socialauth.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

public class IntialSignUpActivity extends Activity implements OnClickListener{
	
	public static int LOGIN=90001;
	public static int SIGNUP=90002;
	
	private Button mFaceBookBtn,mTwitterBtn,mLinkedinBtn;
	private TextView mEmailTv,mTerms;
	private TextView mBottomtxt,mHeaderBellowtxt;
	private SocialAuthAdapter mSocialAuthAdapter;
	private Profile mProfileMap;
	private Context mContext;
	private LayoutInflater inflater;
	private View v;
	
	


	private String mBottmLine,mHeader,mHeaderBellow;
    private int activityType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		_initSetup();
	}

	private void _initSetup(){
		setContentView(R.layout.sign_in_screen);
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
		
		mContext=this;
		
		
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_fbook_signin:
			mSocialAuthAdapter.addCallBack(Provider.FACEBOOK,"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
			//startActivity(new Intent(New_user_Activity.this,Login_Activity.class));
			mSocialAuthAdapter.authorize(this, Provider.FACEBOOK);
			break;
		case R.id.btn_twitter_signin:
			//startActivity(new Intent(New_user_Activity.this,New_user_Activity.class));
			mSocialAuthAdapter.addCallBack(Provider.TWITTER,"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
			mSocialAuthAdapter.authorize(this, Provider.TWITTER);

			break;
		case R.id.btn_linkedin_linkedin:
			mSocialAuthAdapter.addCallBack(Provider.LINKEDIN,"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
			mSocialAuthAdapter.authorize(this, Provider.LINKEDIN);
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

				Util.show_toast(mContext,"profile fetched"+mProfileMap.getDisplayName() );
			}

			@Override
			public void onError(SocialAuthError e) {
				Util.show_toast(mContext,"Ettt" +e.getMessage());
						
			}
		}


	}
		public String getPath(Uri uri) {

		String[] projection = { MediaStore.Images.Media.DATA };

		Cursor cursor = managedQuery(uri, projection, null, null, null);

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		return cursor.getString(column_index);

	}
	

}
