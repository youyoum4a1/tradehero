package android.tradehero.Utills;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;



public class NetworkUtil {
	private static final String TAG = NetworkUtil.class.getSimpleName();

	public static boolean isNetworkAvailable(Context context) {
		boolean outcome = false;

		if (Logger.ENABLE_LOGGING) {
			Log.d(TAG, "checking network is available with context " + context);
		}
		if (context != null) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo[] networkInfos = cm.getAllNetworkInfo();

			for (NetworkInfo tempNetworkInfo : networkInfos) {

				if (Logger.ENABLE_LOGGING) {
					Log.d(TAG,
							"checking network info for "
									+ tempNetworkInfo.getTypeName()
									+ " state: "
									+ tempNetworkInfo.isAvailable()
									+ " connected "
									+ tempNetworkInfo.isConnected());
				}
				/**
				 * Can also check if the user is in roaming
				 */
				if (tempNetworkInfo.isConnected()) {
					outcome = true;
					break;
				}
			}
		}

		return outcome;
	}

	public static void registerNetworkStateReciever(Context context,
			BroadcastReceiver receiver) {

		if (context != null && receiver != null) {
			IntentFilter intentFilter = new IntentFilter(
					android.net.ConnectivityManager.CONNECTIVITY_ACTION);
			try {
				context.registerReceiver(receiver, intentFilter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void unregisterNetworkStateReciever(Context context,
			BroadcastReceiver receiver) {
		if (context != null && receiver != null) {
			try {
				context.unregisterReceiver(receiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
