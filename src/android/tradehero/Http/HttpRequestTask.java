package android.tradehero.Http;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.tradehero.Models.Request;
import android.tradehero.Utills.Constants;
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
		try {
			if(pRequest[0].getRequestType()==Request.REQUEST_TYPE_POST){

				HttpPost post = new HttpPost(pRequest[0].getApiUrl());
				post.setHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_VALUE);
				post.setHeader(Constants.CHARSET, Constants.CHARSET_VALUE);
				ArrayList<BasicHeader> lHeaders= pRequest[0].getHeaders();
				StringEntity se = new StringEntity( pRequest[0].getRequestJSonObject().toString());
				post.setEntity(se);

				//post.setEntity(new UrlEncodedFormEntity(pRequest[0].getParameters()));
				HttpResponse response = client.execute(post);
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					InputStream instream = entity.getContent();
					String res_str= Util.convertStreamToString(instream);
					result=new JSONObject(res_str);
					instream.close();
				}
			}else{
				HttpGet get = new HttpGet(pRequest[0].getApiUrl());
				HttpResponse resp = client.execute(get);
				HttpEntity entity = resp.getEntity();

				if (entity != null) {
					InputStream instream = entity.getContent();
					String res_str= Util.convertStreamToString(instream);
					
					
					result=new JSONObject();
					result.put(Constants.GETRESULT,res_str);
					
					instream.close();
				}
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
