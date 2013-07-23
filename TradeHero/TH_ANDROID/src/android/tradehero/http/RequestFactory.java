package android.tradehero.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.tradehero.models.Request;
import android.tradehero.utills.Constants;
import android.util.Base64;





public class RequestFactory {
	public Request getRegistrationThroughEmailRequest(Context pContext,String pEmail,String pDisplayName ,String pFirstName,String pLastName,String pPassword,String pConfirmPassword) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_EMAIL_URL);

		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.PASSWORD_CONFIRMATION,pConfirmPassword));
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.PASSWORD,pPassword));
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.DISPLAY_NAME,pDisplayName));
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.FIRST_NAME,pFirstName));
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.EMAIL,pEmail));
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.LAST_NAME,pLastName));
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE));
		//RegistrationRequest.setRequestJSonObject(jsonRegistrationObject);
		lRegistrationRequest.setContext(pContext);
		return lRegistrationRequest;
	}
	public Request getLoginThroughEmail(Context pContext,String pUserName,String pPassword ) throws JSONException{
		Request lLoginRequest = new Request();
		lLoginRequest.setApiUrl(Constants.LOGIN_URL);
		//JSONObject mLJsonObject = new JSONObject(); 
		lLoginRequest.setUserName(pUserName);
		lLoginRequest.setPassword(pPassword);
		lLoginRequest.setContext(pContext);
		lLoginRequest.setUserName(pUserName);
		lLoginRequest.setRequestType(Request.REQUEST_TYPE_POST_PARAMS);
		 

		 return lLoginRequest;
	}

	public Request getRegirstationThroughFB(String pFacebookAccessToken) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_SOCIAL_MEDIA_USER_URL);
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.FB_ACCESS_TOKEN,pFacebookAccessToken));
		//JSONObject jsonRegistrationObject = new JSONObject();
		//jsonRegistrationObject.put(Constants.FB_ACCESS_TOKEN,pFacebookAccessToken);
		lRegistrationRequest.addRequestHeader(new BasicHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_VALUE_URL_ENCODED));
		lRegistrationRequest.addRequestHeader(new BasicHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE));
		lRegistrationRequest.setRequestType(Request.REQUEST_TYPE_POST);
		//lRegistrationRequest.setRequestJSonObject(jsonRegistrationObject);
		return lRegistrationRequest;
	}
	public Request getRegirstationThroughLinkedIn(Context pContext,String pLinkedinAccessTokenSecret,String pLinkedinAccessToken) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_SOCIAL_MEDIA_USER_URL);
		lRegistrationRequest.setContext(pContext);
		lRegistrationRequest.addRequestHeader(new BasicHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE));
		lRegistrationRequest.addRequestHeader(new BasicHeader(Constants.CONTENT_TYPE,Constants.CONTENT_TYPE_VALUE_URL_ENCODED ));
		JSONObject jsonRegistrationObject = new JSONObject();
		jsonRegistrationObject.put(Constants.LINKED_ACCESS_TOKEN_SCERET,pLinkedinAccessTokenSecret);
		jsonRegistrationObject.put(Constants.LINKED_ACCESS_TOKEN,pLinkedinAccessToken);
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.LINKED_ACCESS_TOKEN_SCERET,pLinkedinAccessTokenSecret));
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.LINKED_ACCESS_TOKEN,pLinkedinAccessToken));
		lRegistrationRequest.setPassword((pLinkedinAccessTokenSecret));
		lRegistrationRequest.setUserName(pLinkedinAccessToken);
		lRegistrationRequest.setRequestType(8);
		//lRegistrationRequest.setRequestType(Request.REQUEST_TYPE_POST);```																																																																																																																							
		return lRegistrationRequest;
	}
	public Request getRegirstationThroughTwitter(String pEmailId,String pTwitterAccessTokenSecret,String pTwitrerAccessToken) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_SOCIAL_MEDIA_USER_URL);
		lRegistrationRequest.addRequestHeader(new BasicHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE_NEW));
		lRegistrationRequest.addRequestHeader(new BasicHeader(Constants.CONTENT_TYPE,Constants.CONTENT_TYPE_VALUE_URL_ENCODED ));
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.TWITTER_ACCESS_TOKEN,pTwitrerAccessToken));
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.TWITTER_ACCESS_TOKEN_SCERET,pTwitterAccessTokenSecret));
		//lRegistrationRequest.setRequestJSonObject(jsonRegistrationObject);
		lRegistrationRequest.setRequestType(Request.REQUEST_TYPE_POST);
		// lRegistrationRequest.setRequestJSonObject(jsonRegistrationObject);
		return lRegistrationRequest;
	}


	public Request getLoginThroughFB(String pFBAuthToken) throws JSONException{
		Request lLoginRequest = new Request();

		lLoginRequest.setApiUrl(Constants.LOGIN_URL);
		lLoginRequest.setParameter(new BasicNameValuePair(Constants.CLIENT_VERSION,Constants.CLIENT_VERSION_VALUE));
		lLoginRequest.setParameter(new BasicNameValuePair(Constants.CLIENT_OS,Constants.CLIENT_OS_VALUE));
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE));
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.AUTHORIZATION, Constants.TH_FB_PREFIX+" "+pFBAuthToken ));
		//lLoginRequest.addRequestHeader(new BasicHeader(Constants.TH_FB_PREFIX,pFBAuthToken ));
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.CONTENT_TYPE,Constants.CONTENT_TYPE_VALUE_URL_ENCODED ));
		lLoginRequest.setRequestType(Request.REQUEST_TYPE_POST);
		//lLoginRequest.setRequestJSonObject(jsonLoginObject);
		return lLoginRequest;
	}
	public Request getLoginThroughLinkedIn(Context pContext,String pLinkedinAccessTokenSecret,String pLinkedinAccessToken) throws JSONException{
		Request lLoginRequest = new Request();
		lLoginRequest.setApiUrl(Constants.LOGIN_URL);
		lLoginRequest.setParameter(new BasicNameValuePair(Constants.CLIENT_VERSION,Constants.CLIENT_VERSION_VALUE));
		lLoginRequest.setParameter(new BasicNameValuePair(Constants.CLIENT_OS,Constants.CLIENT_OS_VALUE));
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE));
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.AUTHORIZATION, Constants.TH_LINKEDIN_PREFIX+" "+pLinkedinAccessToken+":"+pLinkedinAccessTokenSecret ));
		//lLoginRequest.addRequestHeader(new BasicHeader(Constants.TH_FB_PREFIX,pFBAuthToken ));
		lLoginRequest.setContext(pContext);
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.CONTENT_TYPE,Constants.CONTENT_TYPE_VALUE_URL_ENCODED ));
		lLoginRequest.setRequestType(Request.REQUEST_TYPE_POST);		//lLoginRequest.setRequestJSonObject(jsonLoginObject);
		return lLoginRequest;
	}
	public Request getLoginThroughTwiiter(String pTwitterAccessTokenSecret,String pTwitrerAccessToken) throws JSONException{
		Request lLoginRequest = new Request();
		lLoginRequest.setApiUrl(Constants.LOGIN_URL);
		lLoginRequest.setParameter(new BasicNameValuePair(Constants.CLIENT_VERSION,Constants.CLIENT_VERSION_VALUE));
		lLoginRequest.setParameter(new BasicNameValuePair(Constants.CLIENT_OS,Constants.CLIENT_OS_VALUE));
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE));
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.AUTHORIZATION, Constants.TH_TWITTER_PREFIX+" "+pTwitrerAccessToken+":"+pTwitterAccessTokenSecret));
		//lLoginRequest.addRequestHeader(new BasicHeader(Constants.TH_FB_PREFIX,pFBAuthToken ));
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.CONTENT_TYPE,Constants.CONTENT_TYPE_VALUE_URL_ENCODED ));
		lLoginRequest.setRequestType(Request.REQUEST_TYPE_POST);		//lLoginRequest.setRequestJSonObject(jsonLoginObject);
	
		return lLoginRequest;
	}
	public Request getFogotPasswordRequest(String pEmailId) throws JSONException{
		Request lFPRequest = new Request();
		lFPRequest.setApiUrl(Constants.FORGOT_PASSWORD);
		JSONObject  pFPJsonObject= new JSONObject();
		pFPJsonObject.put(Constants.USER_EMAIL,pEmailId);
		lFPRequest.setReqJsonObject(pFPJsonObject);
		lFPRequest.addRequestHeader(new BasicHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE));
		//lLoginRequest.addRequestHeader(new BasicHeader(Constants.TH_FB_PREFIX,pFBAuthToken ));
		lFPRequest.addRequestHeader(new BasicHeader(Constants.CONTENT_TYPE,Constants.CONTENT_TYPE_VALUE_JSON ));
		lFPRequest.setRequestType(Request.REQUEST_TYPE_POST_JSON);		//lLoginRequest.setRequestJSonObject(jsonLoginObject);
	
		return lFPRequest;
	}
}
