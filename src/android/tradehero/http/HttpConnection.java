package android.tradehero.http;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpConnection {

	/**
	 * Connection timeout set for the HttpClient
	 */
	private static final int CONNECTION_TIMEOUT= 60000;
	/**
	 * Socket timeout set for the HttpClient
	 */
	private static final int SOCKET_TIMEOUT = 100000;
	private static final String TAG = HttpConnection.class.getSimpleName(); 

	/**
	 * @return httpClient An instance of {@link DefaultHttpClient}
	 */
	public static DefaultHttpClient getHttpClient() {
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		HttpConnectionParams.setConnectionTimeout(httpParameters,CONNECTION_TIMEOUT);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);

		return new DefaultHttpClient(httpParameters);
	}


}
