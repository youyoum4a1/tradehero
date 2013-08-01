package android.tradehero.http;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.tradehero.models.Request;


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

		try {
			String responseBody = null;
			switch (pRequest[0].getRequestType()) {
			case Request.REQUEST_TYPE_POST:
				responseBody = HttpRequestUtil.doPostRequestNgetResponseBody(pRequest[0].getContext(),pRequest[0].getApiUrl() ,  pRequest[0].getParameters(),pRequest[0].getHeaders());
				break;
			case Request.REQUEST_TYPE_POST_PARAMS:
				responseBody = HttpRequestUtil.doEmailLoginPostRequestNgetResponseBody(pRequest[0].getContext(), pRequest[0].getApiUrl(), pRequest[0].getUserName(),pRequest[0].getPassword());
				break;
			case Request.REQUEST_TYPE_POST_JSON:
				responseBody = HttpRequestUtil.doPostRequestNgetResponseBody(pRequest[0].getContext(),pRequest[0].getApiUrl() ,  pRequest[0].getReqJsonObject(),pRequest[0].getHeaders());
				break;
			case Request.REQUEST_TYPE_GET:
				responseBody=HttpRequestUtil.doPostRequestNgetResponseBody(pRequest[0].getContext(),pRequest[0].getApiUrl() ,  pRequest[0].getHeaders());
				break;
			case Request.REQUEST_TYPE_BASIC_AUTH:
				responseBody=HttpRequestUtil.doGetBasicAuthenictaion(pRequest[0].getContext(), pRequest[0].getApiUrl(),pRequest[0].getUserName(), pRequest[0].getPassword());
				break;
			case 8:
				responseBody=HttpRequestUtil.doLinkedLoginRequestNgetResponseBody(pRequest[0].getContext(), pRequest[0].getApiUrl(), pRequest[0].getUserName(),pRequest[0].getPassword());
				break;
			case 9:
				responseBody=HttpRequestUtil.doTwitterLoginRequestNgetResponseBody(pRequest[0].getContext(), pRequest[0].getApiUrl(), pRequest[0].getUserName(),pRequest[0].getPassword());
				break;
			default:
				break;
			}
			result=new JSONObject(responseBody);
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
