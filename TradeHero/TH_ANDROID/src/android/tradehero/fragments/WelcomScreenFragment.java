package android.tradehero.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.tradehero.activities.R;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

public class WelcomScreenFragment extends Fragment implements OnClickListener{

	private Button mNewUser,mExistingUser;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction ;
	private InitialSignUpFragment fragment;
	private Bundle mData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//View view = null;
		View view = inflater.inflate(R.layout.sign_in_sign_up_screen, container, false);
		_initSetup(view);
		return view;
	}


	private void _initSetup(View view){

		mNewUser = (Button)view.findViewById(R.id.btn_newuser);
		mExistingUser = (Button)view.findViewById(R.id.btn_signin);
		mNewUser.setOnClickListener(this);
		mExistingUser.setOnClickListener(this);


	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_newuser:

			fragmentManager = getActivity().getSupportFragmentManager();
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_right,R.anim.slide_out_left,R.anim.slide_out_right);
			fragment = new InitialSignUpFragment();
			fragmentTransaction.replace(R.id.sign_in_up_content, fragment,"initial_signup");
			mData = new Bundle();
			mData.putString("BOTTOM_LINE", "or sign up with an");
			mData.putString("HEADER_LINE", "New User");
			mData.putString("HEADER_LINEBELLOW", "sign up with");
			mData.putInt("ACTIVITY_TYPE", InitialSignUpFragment.SIGNUP);
			fragment.setArguments(mData);
			fragmentTransaction.addToBackStack("initial_signup");
			fragmentTransaction.commit();
			break;

		case R.id.btn_signin:

			fragmentManager = getActivity().getSupportFragmentManager();
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_right,R.anim.slide_out_left,R.anim.slide_out_right);
			fragment = new InitialSignUpFragment();
			fragmentTransaction.replace(R.id.sign_in_up_content, fragment,"initial_signup");
			mData = new Bundle();
			mData.putString("BOTTOM_LINE", "or sign in with an");
			mData.putString("HEADER_LINE", "Sign In");
			mData.putString("HEADER_LINEBELLOW", "sign in with");
			mData.putInt("ACTIVITY_TYPE", InitialSignUpFragment.LOGIN);
			fragment.setArguments(mData);
			fragmentTransaction.addToBackStack("initial_signup");
			fragmentTransaction.commit();
			break;

		default:
			break;
		}

	}



	

}
