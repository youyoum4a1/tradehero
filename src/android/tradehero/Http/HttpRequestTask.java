package android.tradehero.Http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.tradehero.Models.Request;
import android.tradehero.Utills.Util;

public class HttpRequestTask extends AsyncTask<Request, Void, JSONObject> {
	private RequestTaskCompleteListener mRequestTaskCompleteListener;
	
	//SoapObject pSoapObject,String pAction
	public HttpRequestTask(RequestTaskCompleteListener pRequestTaskCompleteListener)
	{
		this.mRequestTaskCompleteListener = pRequestTaskCompleteListener;
	}

	@Override
	protected JSONObject doInBackground(Request... pRequest) {

		JSONObject result =null;
		HttpClient client = HttpConnection.getHttpClient();
		HttpPost post = new HttpPost(pRequest[0].getApiUrl());

		try {
			post.setEntity(new UrlEncodedFormEntity(pRequest[0].getParameter()));
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String res_str= Util.convertStreamToString(instream);

                result=new JSONObject(res_str);

                instream.close();
            }
			
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//mRequestTaskCompleteListener.onErrorOccured(, pErrorMessage)
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return result;
	}
	@Override
	protected void onPostExecute(JSONObject pResult) {
		// TODO Auto-generated method stub
		//super.onPostExecute(pResult);
		mRequestTaskCompleteListener.onTaskComplete(pResult);
	}
}
