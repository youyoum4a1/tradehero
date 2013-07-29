/**
 * TradeFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 24, 2013
 */
package android.tradehero.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.tradehero.activities.R;
import android.tradehero.application.App;
import android.tradehero.application.Config;
import android.tradehero.cache.ImageLoader;
import android.tradehero.cache.ImageLoader.ImageLoadingListener;
import android.tradehero.models.Trend;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TradeFragment extends Fragment {
	
	private ImageView mStockBgLogo;
	private ImageView mStockLogo;
	private ImageView mStockChart;
	
	private TextView mStockPrice;
	private TextView mBidPrice;
	private TextView mAskPrice;
	private TextView mPriceAsOfValue;
	private TextView mCashAvailableValue;
	private TextView mQuantityValue;
	private TextView mTradeValue;
	
	private Trend trend;
	private ImageLoader mImageLoader;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		view = inflater.inflate(R.layout.fragment_trade, container, false);
		initViews(view);
		return view;
	}
	
	private void initViews(View v) {
		mImageLoader = new ImageLoader(getActivity());
		
		mStockBgLogo = (ImageView) v.findViewById(R.id.stock_bg_logo);
		mStockLogo = (ImageView) v.findViewById(R.id.stock_logo);
		mStockChart = (ImageView) v.findViewById(R.id.stock_chart);
		
		mStockPrice = (TextView) v.findViewById(R.id.stock_price);
		mBidPrice = (TextView) v.findViewById(R.id.bid_price);
		mAskPrice = (TextView) v.findViewById(R.id.ask_price);
		mPriceAsOfValue = (TextView) v.findViewById(R.id.vprice_as_of);
		mCashAvailableValue = (TextView) v.findViewById(R.id.vcash_available);
		mQuantityValue = (TextView) v.findViewById(R.id.vquantity);
		mTradeValue = (TextView) v.findViewById(R.id.vtrade_value);
		
		trend = ((App)getActivity().getApplication()).getTrend();
		
		if(!TextUtils.isEmpty(trend.getImageBlobUrl())) {
			//Bitmap b = convertToMutable((new WebImageCache(TrendingActivity.this)).get(trend.getImageBlobUrl()));
			mImageLoader.getBitmapImage(trend.getImageBlobUrl(), new ImageLoadingListener() {
				public void onLoadingComplete(Bitmap loadedImage) {
					final Bitmap b = convertToMutableAndRemoveBackground(loadedImage);
					mStockLogo.setImageBitmap(b);
					mStockBgLogo.setImageBitmap(b);
				}
			});	
		}
		
		if(!TextUtils.isEmpty(trend.getYahooSymbol())) 
			mImageLoader.DisplayImage(String.format(Config.getTrendingChartUrl(), trend.getYahooSymbol()), 
					mStockChart);
		
		mStockPrice.setText(String.format("%s:%s", trend.getCurrencyDisplay(), trend.getLastPrice()));
		mAskPrice.setText(trend.getAskPrice()+getString(R.string.ask_with_bracket));
		mBidPrice.setText(" x "+trend.getBidPrice()+getString(R.string.bid_with_bracket));
	}
	
	/**
	 * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates
	 * more memory that there is already allocated.
	 * 
	 * @param imgIn - Source image. It will be released, and should not be used more
	 * @return a copy of imgIn, but muttable.
	 */
	public static Bitmap convertToMutableAndRemoveBackground(Bitmap imgIn) {
	    try {
	        //this is the file going to use temporally to save the bytes. 
	        // This file will not be a image, it will store the raw image data.
	        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

	        //Open an RandomAccessFile
	        //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
	        //into AndroidManifest.xml file
	        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

	        // get the width and height of the source bitmap.
	        int width = imgIn.getWidth();
	        int height = imgIn.getHeight();
	        Bitmap.Config type = imgIn.getConfig();

	        //Copy the byte to the file
	        //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
	        FileChannel channel = randomAccessFile.getChannel();
	        MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
	        imgIn.copyPixelsToBuffer(map);
	        //recycle the source bitmap, this will be no longer used.
	        imgIn.recycle();
	        System.gc();// try to force the bytes from the imgIn to be released

	        //Create a new bitmap to load the bitmap again. Probably the memory will be available. 
	        imgIn = Bitmap.createBitmap(width, height, type);
	        map.position(0);
	        //load it back from temporary 
	        imgIn.copyPixelsFromBuffer(map);
	        
	        //Remove background white color
	        int iWidth = imgIn.getWidth();
	        int iHeight = imgIn.getHeight();
	        int pixelSize = iWidth*iHeight;
	        int[] imagePixels = new int[pixelSize];
	        imgIn.getPixels(imagePixels, 0, iWidth, 0, 0, iWidth, iHeight);
	        for(int i = 0 ; i < pixelSize; i++) {
	        	
	        	int alpha = Color.alpha(imagePixels[i]);
	        	int red = Color.red(imagePixels[i]);
	        	int green = Color.green(imagePixels[i]);
	        	int blue = Color.blue(imagePixels[i]);
	        	
	        	  if (((alpha >= 250) && (alpha <= 255)) &&
	        		  ((red >= 250) && (red <= 255)) &&
	        	      ((green >= 250) && (green <= 255)) &&
	        	      ((blue >= 250) && (blue <= 255))) {
	        		  
	        		  imagePixels[i] = Color.TRANSPARENT;  
	        	  }	
	        }
	        imgIn.setPixels(imagePixels, 0, iWidth, 0, 0, iWidth, iHeight);
	        
	        channel.close();
	        randomAccessFile.close();

	        // delete the temp file
	        file.delete();

	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    } 

	    return imgIn;
	}
}
