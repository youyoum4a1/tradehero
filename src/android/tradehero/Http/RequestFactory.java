package android.tradehero.Http;

import org.json.JSONException;
import org.json.JSONObject;

import android.tradehero.Models.Request;
import android.tradehero.Utills.Constants;

public class RequestFactory {
	public Request getRegistrationThroughEmailRequest(String pEmail,String pDisplayName ,String pFirstName,String pLastName,String pPassword,String pConfirmPassword) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_EMAIL_URL);
		JSONObject jsonRegistrationObject = new JSONObject();
		jsonRegistrationObject.put(Constants.PASSWORD_CONFIRMATION,pConfirmPassword);
		jsonRegistrationObject.put(Constants.PASSWORD,pPassword);
		jsonRegistrationObject.put(Constants.DISPLAY_NAME,pDisplayName);
		jsonRegistrationObject.put(Constants.FIRST_NAME,pFirstName);
		jsonRegistrationObject.put(Constants.EMAIL,pEmail);
		jsonRegistrationObject.put(Constants.LAST_NAME,pLastName);
	    lRegistrationRequest.setRequestJSonObject(jsonRegistrationObject);
		return lRegistrationRequest;
	}
	
	public Request getRegirstationThroughFB(String pFacebookAccessToken) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_SOCIAL_MEDIA_USER_URL);
		JSONObject jsonRegistrationObject = new JSONObject();
		jsonRegistrationObject.put(Constants.FB_ACCESS_TOKEN,pFacebookAccessToken);
	    lRegistrationRequest.setRequestJSonObject(jsonRegistrationObject);
		return lRegistrationRequest;
	}
	public Request getRegirstationThroughLinkedIn(String pLinkedinAccessTokenSecret,String pLinkedinAccessToken) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_SOCIAL_MEDIA_USER_URL);
		JSONObject jsonRegistrationObject = new JSONObject();
		jsonRegistrationObject.put(Constants.LINKED_ACCESS_TOKEN_SCERET,pLinkedinAccessTokenSecret);
		jsonRegistrationObject.put(Constants.LINKED_ACCESS_TOKEN,pLinkedinAccessToken);
	    lRegistrationRequest.setRequestJSonObject(jsonRegistrationObject);
		return lRegistrationRequest;
	}
	public Request getRegirstationThroughTwitter(String pTwitterAccessTokenSecret,String pTwitrerAccessToken) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_SOCIAL_MEDIA_USER_URL);
		JSONObject jsonRegistrationObject = new JSONObject();
		jsonRegistrationObject.put(Constants.TWITTER_ACCESS_TOKEN_SCERET,pTwitterAccessTokenSecret);
		jsonRegistrationObject.put(Constants.TWITTER_ACCESS_TOKEN,pTwitrerAccessToken);
	    lRegistrationRequest.setRequestJSonObject(jsonRegistrationObject);
		return lRegistrationRequest;
	}
	
	public Request getLoginThroughEmail(String pEmail,String pDisplayName ,String pFirstName,String pLastName,String pPassword,String pConfirmPassword) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_EMAIL_URL);
		JSONObject jsonRegistrationObject = new JSONObject();
		jsonRegistrationObject.put(Constants.PASSWORD_CONFIRMATION,pConfirmPassword);
		jsonRegistrationObject.put(Constants.PASSWORD,pPassword);
		jsonRegistrationObject.put(Constants.DISPLAY_NAME,pDisplayName);
		jsonRegistrationObject.put(Constants.FIRST_NAME,pFirstName);
		jsonRegistrationObject.put(Constants.EMAIL,pEmail);
		jsonRegistrationObject.put(Constants.LAST_NAME,pLastName);
	    lRegistrationRequest.setRequestJSonObject(jsonRegistrationObject);
		return lRegistrationRequest;
	}
	public Request getLoginThroughFB(String pEmail,String pDisplayName ,String pFirstName,String pLastName,String pPassword,String pConfirmPassword) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_EMAIL_URL);
		JSONObject jsonRegistrationObject = new JSONObject();
		jsonRegistrationObject.put(Constants.PASSWORD_CONFIRMATION,pConfirmPassword);
		jsonRegistrationObject.put(Constants.PASSWORD,pPassword);
		jsonRegistrationObject.put(Constants.DISPLAY_NAME,pDisplayName);
		jsonRegistrationObject.put(Constants.FIRST_NAME,pFirstName);
		jsonRegistrationObject.put(Constants.EMAIL,pEmail);
		jsonRegistrationObject.put(Constants.LAST_NAME,pLastName);
	    lRegistrationRequest.setRequestJSonObject(jsonRegistrationObject);
		return lRegistrationRequest;
	}
	public Request getLoginThroughLinkedIn(String pEmail,String pDisplayName ,String pFirstName,String pLastName,String pPassword,String pConfirmPassword) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_EMAIL_URL);
		JSONObject jsonRegistrationObject = new JSONObject();
		jsonRegistrationObject.put(Constants.PASSWORD_CONFIRMATION,pConfirmPassword);
		jsonRegistrationObject.put(Constants.PASSWORD,pPassword);
		jsonRegistrationObject.put(Constants.DISPLAY_NAME,pDisplayName);
		jsonRegistrationObject.put(Constants.FIRST_NAME,pFirstName);
		jsonRegistrationObject.put(Constants.EMAIL,pEmail);
		jsonRegistrationObject.put(Constants.LAST_NAME,pLastName);
	    lRegistrationRequest.setRequestJSonObject(jsonRegistrationObject);
		return lRegistrationRequest;
	}
	public Request getLoginThroughTwitter(String pEmail,String pDisplayName ,String pFirstName,String pLastName,String pPassword,String pConfirmPassword) throws JSONException{
		Request lRegistrationRequest = new Request();
		lRegistrationRequest.setApiUrl(Constants.SIGN_UP_WITH_EMAIL_URL);
		JSONObject jsonRegistrationObject = new JSONObject();
		jsonRegistrationObject.put(Constants.PASSWORD_CONFIRMATION,pConfirmPassword);
		jsonRegistrationObject.put(Constants.PASSWORD,pPassword);
		jsonRegistrationObject.put(Constants.DISPLAY_NAME,pDisplayName);
		jsonRegistrationObject.put(Constants.FIRST_NAME,pFirstName);
		jsonRegistrationObject.put(Constants.EMAIL,pEmail);
		jsonRegistrationObject.put(Constants.LAST_NAME,pLastName);
	    lRegistrationRequest.setRequestJSonObject(jsonRegistrationObject);
		return lRegistrationRequest;
	}
}
