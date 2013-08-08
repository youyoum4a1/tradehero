/*
 ===========================================================================
 Copyright (c) 2012 Three Pillar Global Inc. http://threepillarglobal.com

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sub-license, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ===========================================================================
 */
package android.tradehero.utills;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.tradehero.activities.R;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * Utility methods
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * @author abhinav.maheswari@3pillarglobal.com
 * 
 */

public final class Util {

	public static int UI_DENSITY;
	public static int UI_SIZE;
	public static int UI_YAHOO_SCROLL;
	public static int UI_YAHOO_ALLOW;
	public static int UI_RESOLUTION;

	/**
	 * URL encoding of query parameters of a URL
	 * 
	 * @param parameters
	 * @return encoded URL
	 */
	public static String encodeUrl(Bundle parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(URLEncoder.encode(key) + "=" + URLEncoder.encode(parameters.getString(key)));
		}
		return sb.toString();
	}

	/**
	 * URL decoding of query parameters of a URL
	 * 
	 * @param s
	 *            URL to be decoded
	 * @return Map of parameter and values
	 */
	public static Map<String, String> decodeUrl(String s) {
		Map<String, String> params = new HashMap<String, String>();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				if (v.length > 1) {
					params.put(URLDecoder.decode(v[0]), v.length > 1 ? URLDecoder.decode(v[1]) : null);
				}
			}
		}
		return params;
	}

	/**
	 * Parse a URL query and fragment parameters into a key-value bundle.
	 * 
	 * @param url
	 *            the URL to parse
	 * @return a dictionary bundle of keys and values
	 */
	public static Map<String, String> parseUrl(String url) {
		// hack to prevent MalformedURLException
		url = url.replace("fbconnect", "http");
		try {
			URL u = new URL(url);
			Map<String, String> params = decodeUrl(u.getQuery());
			params.putAll(decodeUrl(u.getRef()));
			return params;
		} catch (MalformedURLException e) {
			return new HashMap<String, String>();
		}
	}

	/**
	 * Display a simple alert dialog with the given text and title.
	 * 
	 * @param context
	 *            Android context in which the dialog should be displayed
	 * @param title
	 *            Alert dialog title
	 * @param text
	 *            Alert dialog message
	 */
	public static void showAlert(Context context, String title, String text) {
		Builder alertBuilder = new Builder(context);
		alertBuilder.setTitle(title);
		alertBuilder.setMessage(text);
		alertBuilder.create().show();
	}

	/**
	 * Function for check the network connectivity
	 * 
	 * @return true if network Available otherwise false
	 */
	public static boolean isNetworkAvailable(Context context) {
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			return false;
		}

		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnected();
	}

	/**
	 * Function to print screen resolution, screen inches and density of android
	 * device.
	 * 
	 * @param ctx
	 *            Activity Context
	 */

	public static void getDisplayDpi(Context ctx) {

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);

		double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		double screenInches = Math.sqrt(x + y);
		int screenInch = (int) Math.round(screenInches);
		int dapi = dm.densityDpi;

		Log.d("Resolution X", String.valueOf(width));
		Log.d("Resolution Y", String.valueOf(height));
		Log.d("screeninch", String.valueOf(screenInch));
		Log.d("dapi", String.valueOf(dapi));

		try {
			switch (dm.densityDpi) {

			case DisplayMetrics.DENSITY_LOW:

				UI_DENSITY = 120;

				if (screenInch <= 7) {
					UI_SIZE = 4;
					UI_YAHOO_SCROLL = 290;
					UI_YAHOO_ALLOW = 125;

				} else {
					UI_SIZE = 10;
				}

				break;
			case DisplayMetrics.DENSITY_MEDIUM:

				UI_DENSITY = 160;

				if (screenInch <= 7) {

					// For devices having width 320
					if (width == 320) {
						UI_YAHOO_SCROLL = 390;
						UI_YAHOO_ALLOW = 105;
						UI_SIZE = 3;
					} else if (width == 480) {
						UI_YAHOO_SCROLL = 600;
						UI_YAHOO_ALLOW = 200;
						UI_SIZE = 4;
					} else {
						UI_YAHOO_SCROLL = 1;
						UI_YAHOO_ALLOW = 1;
						UI_SIZE = 6;
					}
				} else {
					UI_SIZE = 10;
					UI_YAHOO_SCROLL = 1;
					UI_YAHOO_ALLOW = 1;
				}

				break;

			case DisplayMetrics.DENSITY_HIGH:

				UI_DENSITY = 240;
				UI_YAHOO_SCROLL = 715;
				UI_YAHOO_ALLOW = 375;

				break;
			case DisplayMetrics.DENSITY_XHIGH:
				UI_DENSITY = 320;
				if (width == 720) {
					UI_SIZE = 7;
					UI_YAHOO_SCROLL = 715;
					UI_YAHOO_ALLOW = 350;
				} else if (width == 1280) {
					UI_SIZE = 10;
					UI_YAHOO_SCROLL = 1;
					UI_YAHOO_ALLOW = 1;
				} else {
					UI_YAHOO_SCROLL = 1;
					UI_YAHOO_ALLOW = 1;
				}

				break;
			default:
				break;
			}
		} catch (Exception e) {
			// Caught exception here
		}
	}

	public static void show_toast(Context ctx,String msg){

		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();

	}
	/**
	 *
	 * @param is
	 * @return String
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}


	public static  Bitmap getRoundedShape(Bitmap scaleBitmapImage) 
	{
		int targetWidth = scaleBitmapImage.getWidth();
		int targetHeight =  scaleBitmapImage.getHeight();

		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,targetHeight,
				Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(targetBitmap);
		Path path = new Path();
		path.addCircle(((float) targetWidth) / 2,
				((float) targetHeight) / 2,
				(Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
				Path.Direction.CW);
		Paint paint = new Paint(); 
		paint.setColor(Color.GRAY); 
		//paint.setStyle(Paint.Style.STROKE);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);

		canvas.clipPath(path);
		Bitmap sourceBitmap = scaleBitmapImage;
		canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
				sourceBitmap.getHeight()), new RectF(0, 0, targetWidth,
						targetHeight), paint);
		return targetBitmap;
	}
	public static void showDIlog(Context pContext, String mssg)
	{
		AlertDialog.Builder dialog = new Builder(pContext);
		dialog.setMessage(mssg)
		.setCancelable(false)
		.setIcon(R.id.logo_img)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {

				dialog.cancel();

			}
		});
		AlertDialog alrt = dialog.create();
		alrt.show();

	}

	public static Bitmap getImagerotation(String path,Bitmap b){

		int width = b.getWidth();
		int height = b.getHeight();
		Matrix matrix = new Matrix();
		try{

			/*int newWidth = 150;
		int newHeight = 150;
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;*/

			ExifInterface exif = new ExifInterface(path);
			String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
			if (orientation.equals(ExifInterface.ORIENTATION_NORMAL)) 
			{
				// Do nothing. The original image is fine.
			} else if (orientation.equals(ExifInterface.ORIENTATION_ROTATE_90+""))
			{
				matrix.postRotate(90);
			} else if (orientation.equals(ExifInterface.ORIENTATION_ROTATE_180+"")) 
			{
				matrix.postRotate(180);
			} else if (orientation.equals(ExifInterface.ORIENTATION_ROTATE_270+""))
			{
				matrix.postRotate(270);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return  Bitmap.createBitmap(b, 0, 0, width, height, matrix, true);


	}

	public static final Pattern email_valid = Pattern.compile(
			"^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
			);
	
	
	public static String httpGetConnection(String url) {
		String result = null;
		Log.d("Final URL : ", url);
		HttpClient httpClient = new DefaultHttpClient();

		// Sending a GET request to the web page that we want
		// Because of we are sending a GET request, we have to pass the values
		// through the URL
		HttpGet httpGet = new HttpGet(url);

		try {
			// execute(); executes a request using the default context.
			// Then we assign the execution result to HttpResponse
			HttpResponse httpResponse = httpClient.execute(httpGet);

			// getEntity() ; obtains the message entity of this response
			// getContent() ; creates a new InputStream object of the entity.
			// Now we need a readable source to read the byte stream that comes
			// as the httpResponse
			InputStream inputStream = httpResponse.getEntity().getContent();

			// We have a byte stream. Next step is to convert it to a Character
			// stream
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			// Then we have to wraps the existing reader (InputStreamReader) and
			// buffer the input
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			// InputStreamReader contains a buffer of bytes read from the source
			// stream and converts these into characters as needed.
			// The buffer size is 8K
			// Therefore we need a mechanism to append the separately coming
			// chunks in to one String element
			// We have to use a class that can handle modifiable sequence of
			// characters for use in creating String
			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			// There may be so many buffered chunks. We have to go through each
			// and every chunk of characters
			// and assign a each chunk to bufferedStrChunk String variable
			// and append that value one by one to the stringBuilder
			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				stringBuilder.append(bufferedStrChunk);
			}

			Log.d("result:", stringBuilder + "");
			return stringBuilder.toString();

		} catch (ClientProtocolException cpe) {
			System.out.println("Exception generates caz of httpResponse :"
					+ cpe);
			cpe.printStackTrace();
		} catch (IOException ioe) {
			System.out
					.println("Second exception generates caz of httpResponse :"
							+ ioe);
			ioe.printStackTrace();
		}

		return null;

	}
public static void CustomToast(Context ctx,String message)
{
    Context context = ctx;
   String text = "Sorry ! "+ message;
   Toast customizedToast = Toast.makeText(ctx, text,Toast.LENGTH_LONG);
   customizedToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
   LinearLayout mLayout = new LinearLayout(context);
   mLayout.setOrientation(LinearLayout.VERTICAL);
   mLayout.setBackgroundResource(R.color.black);
   TextView mTV = new TextView(context);
   mTV.setTextColor(Color.WHITE);
   mTV.setTextSize(18);
 //  CustomView cv = new CustomView(context);
   mTV.setText(text);
   mTV.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
   int width = LinearLayout.LayoutParams.FILL_PARENT;
   int height = LinearLayout.LayoutParams.WRAP_CONTENT;
  // mLayout.addView(cv, new LinearLayout.LayoutParams(height, width));
   mLayout.addView(mTV, new LinearLayout.LayoutParams(width, 100));
   customizedToast.setView(mLayout);
   customizedToast.show();
	
}

public static void dismissKeyBoard(Context ctx,View v){
	
	InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
	imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
}

}
