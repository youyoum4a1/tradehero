package android.tradehero.fragments;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.tradehero.activities.R;
import android.tradehero.activities.TradeHeroTabActivity;
import android.tradehero.activities.WelcomeActivity;
import android.tradehero.http.HttpRequestTask;
import android.tradehero.http.RequestFactory;
import android.tradehero.http.RequestTaskCompleteListener;
import android.tradehero.models.Request;
import android.tradehero.networkstatus.NetworkStatus;
import android.tradehero.utills.Util;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginFragment extends Fragment implements OnClickListener,RequestTaskCompleteListener,OnFocusChangeListener{

	private TextView mForgotPassword;
	private Button mSignIn;
	private EditText inputEmailName,inputPassword;
	private ProgressDialog mProgressDialog;
	private LayoutInflater inflater;
	private View v;
	private Context mContext;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//View view = null;
		View view = inflater.inflate(R.layout.login_withemail_screen, container, false);
		_initView(view);
		mContext=getActivity();
		return view;
	}

	private void _initView(View view) {

		inflater=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inflater.inflate(R.layout.topbar, null);
		TextView txt = (TextView) v.findViewById(R.id.header_txt);
		txt.setText(getString(R.string.sign_in));
		ViewGroup header = (ViewGroup)view.findViewById(R.id.loginemail_wraper);
		header.addView(v);
		mSignIn = (Button) view.findViewById(R.id.btn_login);
		mForgotPassword = (TextView)view. findViewById(R.id.txt_forgotpwd);
		inputEmailName=(EditText)view.findViewById(R.id.et_emailid_login);
		inputPassword=(EditText)view.findViewById(R.id.et_pwd_login);
		//inputEmailName.setText("neeraj@eatechnologies.com");
		//inputPassword.setText("testing");
		mProgressDialog= new ProgressDialog(getActivity());
		mProgressDialog.setMessage("Logging In");
		mForgotPassword.setOnClickListener(this);
		mSignIn.setOnClickListener(this);
		//mSignIn.setOnTouchListener(this);
		//mForgotPassword.setOnTouchListener(this)
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_login:

			String uname=inputEmailName.getText()!=null?inputEmailName.getText().toString():"";
			String pass =inputPassword.getText()!=null?inputPassword.getText().toString():"";
			if(uname.trim().length()>0 && pass.trim().length()>0)
			{
				
				try {
					
					if(NetworkStatus.getInstance().isConnected(getActivity()))
					{
						HttpRequestTask  mRequestTask= new HttpRequestTask(this);
						RequestFactory mRF= new RequestFactory();
						Request[] lRequests={ mRF.getLoginThroughEmail(mContext,uname,pass)};
						mRequestTask.execute(lRequests);
						mProgressDialog.show();
					}else
					{
						Util.show_toast(getActivity(), getResources().getString(R.string.network_error));
					}
					
				} catch (Exception e){
					e.printStackTrace();
				}

			}else 
			{
				//startActivity(new Intent(getActivity(),TradeHeroTabActivity.class));
				//getActivity().finish();
				Util.show_toast(getActivity(),getResources().getString(R.string.field_not_balnk));

			}

			break;
		case R.id.txt_forgotpwd:
			showForgotDIlog();
			break;

		default:
			break;
		}

	}

	private void showForgotDIlog(){

		inputEmailName= new EditText(getActivity());

		AlertDialog.Builder dialog = new Builder(getActivity());
		dialog.setMessage(getResources().getString(R.string.enter_message_email))
		.setCancelable(false)
		.setView(inputEmailName)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.cancel();

			}
		})
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, int arg1) {

				String email = inputEmailName.getText().toString();

				if(email==null || email.equals(""))
				{

					Util.show_toast(getActivity(), getResources().getString(R.string.email_alert));
				}
				else
				{
					doForgotPassword(email);
					dialog.dismiss();
				}

			}
		});

		AlertDialog alrt = dialog.create();
		alrt.show();
	}

	@Override
	public void onTaskComplete(JSONObject pResponseObject) {
		mProgressDialog.dismiss();

		if(pResponseObject != null)
		{
			try {

				if(pResponseObject.has("Message"))
				{
					String msg = pResponseObject.optString("Message");
					Util.show_toast(getActivity(), msg);					
				}
				else
				{    
					startActivity(new Intent(getActivity(),TradeHeroTabActivity.class));
					Util.show_toast(getActivity(), pResponseObject.toString());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onErrorOccured(int pErrorCode, String pErrorMessage) {

	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) {

	}

	/*@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			switch (v.getId()) {
			case R.id.btn_login:

				String uname=inputEmailName.getText()!=null?inputEmailName.getText().toString():"";
				String pass =inputPassword.getText()!=null?inputPassword.getText().toString():"";

				if(uname.trim().length()>0 && pass.trim().length()>0)
				{
					
					try {
						
						if(NetworkStatus.getInstance().isConnected(getActivity()))
						{
							HttpRequestTask  mRequestTask= new HttpRequestTask(this);
							RequestFactory mRF= new RequestFactory();
							Request[] lRequests={ mRF.getLoginThroughEmail(mContext,uname,pass)};
							mRequestTask.execute(lRequests);
							mProgressDialog.show();
						}else
						{
							Util.show_toast(getActivity(), getResources().getString(R.string.network_error));
						}
						
					} catch (Exception e){
						e.printStackTrace();
					}

				}else 
				{
					
					Util.show_toast(getActivity(), "Field should not be blank .");

				}
				break;

			default:
				break;
			}

			break;

		case MotionEvent.ACTION_UP:

			switch (v.getId()) {
			case R.id.btn_login:
				mSignIn.setBackgroundResource(R.drawable.roundrectangle_signin);

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
*/

	private void doForgotPassword(String email){
		HttpRequestTask  mRequestTask= new HttpRequestTask(new RequestTaskCompleteListener(){
			@Override
			public void onTaskComplete(JSONObject pResponseObject) {
				mProgressDialog.dismiss();
				startActivity(new Intent(getActivity(),TradeHeroTabActivity.class));
				Util.show_toast(getActivity(), getResources().getString(R.string.thank_you_message_email) +pResponseObject!=null?pResponseObject.toString():"Error");
			}

			@Override
			public void onErrorOccured(int pErrorCode,
					String pErrorMessage) {

			}});
		RequestFactory mRF= new RequestFactory();
		android.tradehero.models.Request[] lRequests = new Request[1];
		try 
		{
			lRequests[0]=mRF.getFogotPasswordRequest(email);
		}

		catch (JSONException e)
		{
			e.printStackTrace();
		}	
		mRequestTask.execute(lRequests);
		mProgressDialog.show();

	}

}
