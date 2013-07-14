package android.tradehero.Models;



import java.util.ArrayList;

import org.json.JSONObject;
import org.apache.http.message.BasicHeader;


public class Request {
	public static int REQUEST_TYPE_POST=-100;
	public static int REQUEST_TYPE_GET=-200;
	private String mApiUrl;
	private JSONObject mRequestJSonObject;
	private int mRequestType;
	private ArrayList<BasicHeader> mBasicHeaders=new ArrayList<BasicHeader>();
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
	public void addRequestHeader(BasicHeader pBasicHeader) {
		mBasicHeaders.add(pBasicHeader);
	}
	public ArrayList<BasicHeader> getHeaders(){
		return mBasicHeaders;
	}
	public int getRequestType() {
		return mRequestType;
	}
	public void setRequestType(int pRequestType) {
		this.mRequestType = pRequestType;
	}
	
		
 
}
