package android.tradehero.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.tradehero.application.App;
import android.tradehero.models.Request;
import android.tradehero.models.Token;
import android.tradehero.utills.Constants;





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
		_setToken(pContext, pEmail, pPassword);
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
		_setToken(pContext, pUserName, pPassword);
		/*lLoginRequest.setParameter(new BasicNameValuePair(Constants.CLIENT_VERSION, "1.5.0"));
		lLoginRequest.setParameter(new BasicNameValuePair("clientiOS", "1"));
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_VALUE_URL_ENCODED));
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.TH_CLIENT_VERSION, "1.4.1.2813"));
		lLoginRequest.addRequestHeader(new BasicHeader("Authorization", "Basic "+pUserName+pPassword));
		lLoginRequest.setRequestType(Request.REQUEST_TYPE_POST);*/
		return lLoginRequest;
	}

	public Request getRegistrationThroughFB(String pFacebookAccessToken) throws JSONException{
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
	public Request getRegistrationThroughLinkedIn(Context pContext,String pLinkedinAccessTokenSecret,String pLinkedinAccessToken) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_SOCIAL_MEDIA_USER_URL);
		lRegistrationRequest.setContext(pContext); 
		lRegistrationRequest.addRequestHeader(new BasicHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE));
		lRegistrationRequest.addRequestHeader(new BasicHeader(Constants.CONTENT_TYPE,Constants.CONTENT_TYPE_VALUE_URL_ENCODED ));
		//lRegistrationRequest.addRequestHeader(new BasicHeader("Authorization","TH-LinkedIn "+pLinkedinAccessToken + ":" + pLinkedinAccessTokenSecret));
		//		JSONObject jsonRegistrationObject = new JSONObject();
		//		jsonRegistrationObject.put(Constants.LINKED_ACCESS_TOKEN_SCERET,pLinkedinAccessToken);
		//		jsonRegistrationObject.put(Constants.LINKED_ACCESS_TOKEN,pLinkedinAccessTokenSecret);
		//		lRegistrationRequest.setReqJsonObject(jsonRegistrationObject);
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.LINKED_ACCESS_TOKEN_SCERET,pLinkedinAccessTokenSecret));
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.LINKED_ACCESS_TOKEN,pLinkedinAccessToken));
		//	lRegistrationRequest.setParameter(new BasicNameValuePair("clientiOS","1"));

		lRegistrationRequest.setPassword((pLinkedinAccessTokenSecret));
		lRegistrationRequest.setUserName(pLinkedinAccessToken);
		//lRegistrationRequest.setRequestType(8);
		lRegistrationRequest.setRequestType(Request.REQUEST_TYPE_POST);	

		System.out.println("variables--------------"+"secret-"+pLinkedinAccessTokenSecret+" token-"+pLinkedinAccessToken);
		return lRegistrationRequest;
	}
	public Request getRegistrationThroughTwitter(String pEmailId,String pTwitterAccessTokenSecret,String pTwitrerAccessToken) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_SOCIAL_MEDIA_USER_URL);
		lRegistrationRequest.addRequestHeader(new BasicHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE_NEW));
		lRegistrationRequest.addRequestHeader(new BasicHeader(Constants.CONTENT_TYPE,Constants.CONTENT_TYPE_VALUE_URL_ENCODED ));
		lRegistrationRequest.setParameter(new BasicNameValuePair("email",pEmailId));
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.TWITTER_ACCESS_TOKEN,pTwitrerAccessToken));
		lRegistrationRequest.setParameter(new BasicNameValuePair(Constants.TWITTER_ACCESS_TOKEN_SCERET,pTwitterAccessTokenSecret));
		//lRegistrationRequest.setRequestJSonObject(jsonRegistrationObject);
		//lRegistrationRequest.setRequestType(9);
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
		_setToken(pContext, pLinkedinAccessToken, pLinkedinAccessTokenSecret);
		return lLoginRequest;
	}
	public Request getLoginThroughTwiiter(Context ctx,String pTwitterAccessTokenSecret,String pTwitrerAccessToken) throws JSONException{
		Request lLoginRequest = new Request();
		lLoginRequest.setApiUrl(Constants.LOGIN_URL);
		lLoginRequest.setParameter(new BasicNameValuePair(Constants.CLIENT_VERSION,Constants.CLIENT_VERSION_VALUE));
		lLoginRequest.setParameter(new BasicNameValuePair(Constants.CLIENT_OS,Constants.CLIENT_OS_VALUE));
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE));
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.AUTHORIZATION, Constants.TH_TWITTER_PREFIX+" "+pTwitrerAccessToken+":"+pTwitterAccessTokenSecret));
		//lLoginRequest.addRequestHeader(new BasicHeader(Constants.TH_FB_PREFIX,pFBAuthToken ));
		lLoginRequest.addRequestHeader(new BasicHeader(Constants.CONTENT_TYPE,Constants.CONTENT_TYPE_VALUE_URL_ENCODED ));
		lLoginRequest.setRequestType(Request.REQUEST_TYPE_POST);		//lLoginRequest.setRequestJSonObject(jsonLoginObject);
		lLoginRequest.setContext(ctx);
		_setToken(ctx, pTwitrerAccessToken, pTwitterAccessTokenSecret);
		
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


	private void _setToken(Context ctx,String username,String pwd)
	{
		Token token = new Token();
		token.setToken(username+":"+pwd);
		((App)ctx.getApplicationContext()).setToken(token); 
	}


}
