package android.tradehero.Models;

import java.lang.reflect.Array;

import org.json.JSONObject;

import android.preference.PreferenceActivity.Header;

public class Request {
	private String mApiUrl;
	private JSONObject mRequestJSonObject;
	private Array<>
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
