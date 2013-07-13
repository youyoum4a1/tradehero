package android.tradehero.Utills;

public class HttpUtil {

	public static boolean isValid(String iconURL) {
		boolean outcome = false;
		if (iconURL != null && (iconURL.toLowerCase().startsWith("http://") || iconURL.toLowerCase().startsWith("https://"))) {
			outcome = true;
		}

		return outcome;
	}

}
