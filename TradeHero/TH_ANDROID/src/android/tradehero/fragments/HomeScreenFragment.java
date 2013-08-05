package android.tradehero.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.tradehero.activities.R;
import android.tradehero.adapters.ProfileContentAdapter;
import android.tradehero.application.App;
import android.tradehero.application.ConvolutionMatrix;
import android.tradehero.cache.ImageLoader;
import android.tradehero.http.HttpRequestTask;
import android.tradehero.http.RequestFactory;
import android.tradehero.http.RequestTaskCompleteListener;
import android.tradehero.models.ProfileDTO;
import android.tradehero.models.Request;
import android.tradehero.networkstatus.NetworkStatus;
import android.tradehero.utills.Constants;
import android.tradehero.utills.PostData;
import android.tradehero.utills.Util;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class HomeScreenFragment extends Fragment implements OnClickListener,RequestTaskCompleteListener{

	private ImageView mUserImg;
	private TextView txtUserName;
	private String mUserName;
    private ListView mListviewContent;
    private LinearLayout mBagroundImage;
    private ProfileDTO profile;
    String picture ;
	private BitmapDrawable drawableBitmap;
	Bitmap mBitmap,mBGBtmp;
	 String id;
	private ProgressDialog mProgressDialog;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.profile_screen, container, false);
		_initView(view);
		
		return view;
	}


	private void _initView(View view) {
		
		mProgressDialog= new ProgressDialog(getActivity());
		mProgressDialog.setMessage("Loading In...");
		
		profile = ((App)getActivity().getApplication()).getProfileDTO();
		
		 String mUserName = profile.getDisplayName();
		  picture = profile.getPicture();
         id = profile.getId();
		//mUserName ="Rajiv Bhatia";
		txtUserName = (TextView)view.findViewById(R.id.header_txt_homescreen);
		txtUserName.setText(mUserName);
		
		mBagroundImage = (LinearLayout) view.findViewById(R.id.top_layout);
		mUserImg = (ImageView)view.findViewById(R.id.img_banner_user);
		new UpdateUi().execute();

		//mUserImg.setImageBitmap(Util.getRoundedShape(BitmapFactory.decodeResource(getResources(),R.drawable.bhatia_img1)));
		
		
		mListviewContent = (ListView)view.findViewById(R.id.list_user_content);
		mListviewContent.setAdapter(new ProfileContentAdapter(getActivity()));

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
	
	/*public static Bitmap applyGaussianBlur(Bitmap src) {
		double[][] GaussianBlurConfig = new double[][] {
				{ 2, 0, 0 },
				{ 1, 0, 0 },
				{ 1, 1, 0 }
		};
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(GaussianBlurConfig);
		convMatrix.Factor = 16;
		convMatrix.Offset = 0;
		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	}
	
	public  Canvas applyGrayscale(Bitmap src) {
		
		Bitmap grayscaleBitmap = Bitmap.createBitmap(
				src.getWidth(), src.getHeight(),
			    Bitmap.Config.RGB_565);

			Canvas c = new Canvas(grayscaleBitmap);
			Paint p = new Paint();
			ColorMatrix cm = new ColorMatrix();

			cm.setSaturation(0);
			ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
			p.setColorFilter(filter); 
			c.drawBitmap(src, 0, 0, p);
			return c;
			
	}*/
	
	class UpdateUi extends AsyncTask<Void, Void, Void>
	{
		ImageLoader imgLoader;
		ProgressDialog dlg;
		String resp;
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
		    resp = new PostData(getActivity()).httpGetConnection(Constants.SIGN_UP_WITH_SOCIAL_MEDIA_USER_URL+"/"+id+"/timeline?maxCount=42");
			System.out.println("bottom url==========="+Constants.SIGN_UP_WITH_SOCIAL_MEDIA_USER_URL+"/"+id+"/timeline?maxCount=42");
			System.out.println("value======"+resp);
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
			Util.show_toast(getActivity(), resp);
			
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


	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		 System.exit(0);
		 Util.show_toast(getActivity(), "back");
		
	}

	
	
}
