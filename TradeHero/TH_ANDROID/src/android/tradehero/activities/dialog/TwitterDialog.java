package android.tradehero.activities.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.tradehero.activities.R;
import android.tradehero.activities.dialog.TwitterOAuthView.Listener;
import android.tradehero.utills.Constants;

public class TwitterDialog extends Dialog {
	private ProgressDialog progressDialog = null;
    private Listener mListener;
    private TwitterOAuthView mView;
    private boolean oauthStarted;
    private Context mContext;
	//Old
	private static final boolean DUMMY_CALLBACK_URL =false;
	/**
	 * Construct a new LinkedIn dialog
	 * 
	 * @param context
	 *            activity {@link Context}
	 * @param progressDialog
	 *            {@link ProgressDialog}
	 */
	public TwitterDialog(Context context, ProgressDialog progressDialog,Listener pListener) {
		super(context);
		this.progressDialog = progressDialog;
		this.mListener=pListener;
		mContext=context;
		mView= new TwitterOAuthView(mContext);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// must call before super.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tw_dialog);
		
		LinearLayout ll =(LinearLayout)findViewById(R.id.container);
		mView.start(Constants.TWITTER_CONSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET, Constants.TWITTER_CALLBACK_URL,DUMMY_CALLBACK_URL , mListener);
		ll.addView(mView);
	}

	


}
