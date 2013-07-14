package android.tradehero.Models;

import org.json.JSONObject;

public class Request {
	private String mApiUrl;
	private JSONObject mRequestJSonObject;
	public String getApiUrl() {
		return mApiUrl;
	}
	public void setApiUrl(String pApiUrl) {
		this.mApiUrl = pApiUrl;
	}
	public JSONObject getRequestJSonObject() {
		return mRequestJSonObject;
	}
	public void setRequestJSonObject(JSONObject pRequestJSonObject) {
		this.mRequestJSonObject = pRequestJSonObject;
	}
	
	
	
 
}
