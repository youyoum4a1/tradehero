package android.tradehero.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.tradehero.activities.R;
import android.tradehero.adapters.ProfileContentAdapter;
import android.tradehero.application.ConvolutionMatrix;
import android.tradehero.utills.Util;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class HomeScreenFragment extends Fragment{

	private ImageView mUserImg;
	private TextView txtUserName;
	private String mUserName;
    private ListView mListviewContent;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.profile_screen, container, false);
		_initView(view);
		return view;
	}


	private void _initView(View view) {

		mUserName ="Rajiv Bhatia";
		txtUserName = (TextView)view.findViewById(R.id.header_txt_homescreen);
		txtUserName.setText(mUserName);

		mUserImg = (ImageView)view.findViewById(R.id.img_banner_user);
		//mUserImg.setImageBitmap(Util.getRoundedShape(BitmapFactory.decodeResource(getResources(),R.drawable.bhatia_img1)));
		mUserImg.setImageBitmap(Util.getRoundedShape(BitmapFactory.decodeResource(getResources(),R.drawable.bhatia_img1)));
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
		convMatrix.Factor = 16;
		convMatrix.Offset = 0;
		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	}

}
