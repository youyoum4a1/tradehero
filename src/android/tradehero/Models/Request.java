package android.tradehero.Models;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

public class Request {
	private String mApiUrl;
	private ArrayList<BasicNameValuePair> mParameter = new ArrayList<BasicNameValuePair>();
	public String getApiUrl() {
		return mApiUrl;
	}
	public void setApiUrl(String pApiUrl) {
		this.mApiUrl = mApiUrl;
	}
	public ArrayList<BasicNameValuePair> getParameter() {
		return mParameter;
	}
	public void setParameter(ArrayList<BasicNameValuePair> mParameter) {
		this.mParameter = mParameter;
	}
	
	
 
}
