package android.tradehero.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.tradehero.utills.Constants;
import android.tradehero.utills.Logger;
import android.tradehero.utills.Logger.LogLevel;
import android.util.Base64;



public class HttpRequestUtil {


	public static String doGetBasicAuthenictaion(Context context, String url, String authUser,String authPass) {
		URI uri = null;

		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		HttpGet get = new HttpGet();
		get.setURI(uri);

		UsernamePasswordCredentials credentials =new UsernamePasswordCredentials(authUser, authPass);
		BasicScheme scheme = new BasicScheme();
		Header authorizationHeader;
		try {
			authorizationHeader = scheme.authenticate(credentials, get);
			get.addHeader(authorizationHeader);
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getResponseBody(context, get);
	}
	/**
	 * @param context
	 * @param url
	 * @param headerList
	 * @return
	 */
	public static String doGetRequestNgetResponseBody(Context context, String url, List<Header> headerList) {

		URI uri = null;

		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		HttpGet get = new HttpGet(uri);

		for (Header header : headerList) {
			get.addHeader(header);
		}

		return getResponseBody(context, get);

	} 

	/**
	 * @param context
	 * @param url
	 * @param headerList
	 * @return
	 */
	public static HttpResponse doGetRequestNgetResponse(Context context, String url, List<Header> headerList) {

		URI uri = null;

		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		HttpGet get = new HttpGet(uri);

		for (Header header : headerList) {
			get.addHeader(header);
		}

		return getResponse(context, get);

	} 


	/**
	 * @param context
	 * @param url
	 * @param params
	 * @return
	 */
	public static String doPostRequestNgetResponseBody(Context context, String url, List params) {

		HttpPost post = new HttpPost(url);

		UrlEncodedFormEntity ent = null;

		try {
			ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (ent != null)
			post.setEntity(ent);

		return getResponseBody(context, post);

	} 
	/**
	 * @param context
	 * @param url
	 * @param params
	 * @return
	 */
	public static String doPostRequestNgetResponseBody(Context context, String url, List params,List<Header> headerList) {

		HttpPost post = new HttpPost(url);

		UrlEncodedFormEntity ent = null;
		for (Header header : headerList) {
			post.addHeader(header);
		}
		try {

			//ent =new UrlEncodedFormEntity(params);
			ent =new UrlEncodedFormEntity(params, HTTP.UTF_8);


		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (ent != null)

			post.setEntity(ent);



		return getResponseBody(context, post);

	}
	/**
	 * @param context
	 * @param url
	 * @param params
	 * @return
	 */
	public static String doEmailLoginPostRequestNgetResponseBody(Context context, String url, String pUserName,String pPassword) {

		HttpPost post = new HttpPost(url);
		try {
			String token=pUserName+":"+pPassword;
			byte[] data = null;
			try {
				data = token.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//token=Base64.encodeToString(data, Base64.DEFAULT);
			token=Base64.encodeToString(data, Base64.NO_WRAP);
			post.setEntity(new StringEntity(Constants.TH_ENTITY));
			//post.setEntity(new StringEntity("{\"clientVersion\":\"1.4.1\",\"clientiOS\":1}"));
			post.setHeader(Constants.CONTENT_TYPE,Constants.CONTENT_TYPE_VALUE_JSON);
			//post.setHeader("Content-type", "application/json; charset=UTF-8");
			post.setHeader(Constants.TH_CLIENT_VERSION,Constants.TH_CLIENT_VERSION_VALUE);
			//post.setHeader("TH-Client-Version","1.4.1.2813");
			post.setHeader(Constants.AUTHORIZATION,Constants.TH_EMAIL_PREFIX+" "+token);
			//post.setHeader("Authorization","Basic "+token.substring(0, token.length()-1));
			System.out.println( "Mukul ----- "+ token);
			//post.setHeader("Authorization","Basic bmVlcmFqQGVhdGVjaG5vbG9naWVzLmNvbTp0ZXN0aW5n");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}



		return getResponseBody(context, post);

	} 

	/**
	 * @param context
	 * @param url
	 * @param params
	 * @return
	 */
	public static String doLinkedLoginRequestNgetResponseBody(Context context, String url, String pTokenSceret,String pToken) {

		HttpPost post = new HttpPost(url);



		try {
			

			String r = "{ \"linkedin_access_token\": \""+pToken+"\", \"linkedin_access_token_secret\": \""+pTokenSceret+"\"}";
			//String r = "{ \"linkedin_access_token\": \""+"53b4314f-ed6e-455f-a100-f3bd23f37ada"+"\", \"linkedin_access_token_secret\": \""+"d1890c45-d4f7-4c17-914d-085ebcd7e8c4"+"\"}";
			System.out.println("json data------"+r);
			post.setEntity(new StringEntity(r));
			post.setHeader(Constants.TH_CLIENT_VERSION,Constants.TH_CLIENT_VERSION_VALUE);
		//	post.setHeader("Accept-Language","en-us");
			post.setHeader("Content-type", "application/json");
			post.setHeader("Authorization","TH-LinkedIn "+pToken + ":" + pTokenSceret);
			//post.setHeader("Accept", "application/json");
		//	post.setHeader("Accept-Encoding", "gzip, deflate");
			post.setHeader(Constants.TH_CLIENT_VERSION,Constants.TH_CLIENT_VERSION_VALUE_NEW);
			post.setHeader("Content-type", "application/json; charset=UTF-8");
			JSONObject jsonParams = new JSONObject();
			jsonParams.put(Constants.LINKED_ACCESS_TOKEN_SCERET, pTokenSceret);
			jsonParams.put(Constants.LINKED_ACCESS_TOKEN, pToken);
			StringEntity ent = new StringEntity(jsonParams.toString(), HTTP.UTF_8);
			//ent.setContentType("application/json");
			post.setEntity(ent);
			//post.setHeader("TH-Client-Version","1.4.1.2813");
			//post.setParams(new BasicHttpParams().setParameter(Constants.LINKED_ACCESS_TOKEN_SCERET, pPassword).setParameter(Constants.LINKED_ACCESS_TOKEN, pUserName));
			//post.setParams(new BasicHttpParams().setParamet	er(Constants.LINKED_ACCESS_TOKEN, pUserName));\
								
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getResponseBody(context, post);

	} 


	public static String doTwitterLoginRequestNgetResponseBody(Context context, String url, String pTokenSceret,String pToken) {

		HttpPost post = new HttpPost(url);
		try {
			String r = "{ \"twitter_access_token\": \""+pToken+"\", \"twitter_access_token_secret\": \""+pTokenSceret+"\" }";
			System.out.println(r);
			post.setEntity(new StringEntity(r));
			post.setHeader(Constants.TH_CLIENT_VERSION,Constants.TH_CLIENT_VERSION_VALUE_NEW);
			post.setHeader("Accept-Language","en-us");
			post.setHeader("Content-type", "application/json");
			post.setHeader("Accept", "application/json");
			post.setHeader("Accept-Encoding", "gzip, deflate");

			post.setHeader(Constants.TH_CLIENT_VERSION,Constants.TH_CLIENT_VERSION_VALUE_NEW);
			post.setHeader("Content-type", "application/json; charset=UTF-8");
			JSONObject jsonParams = new JSONObject();
			jsonParams.put(Constants.LINKED_ACCESS_TOKEN_SCERET, pTokenSceret);
			jsonParams.put(Constants.LINKED_ACCESS_TOKEN, pToken);
			StringEntity ent = new StringEntity(jsonParams.toString(), HTTP.UTF_8);
			//ent.setContentType("application/json");
			post.setEntity(ent);
			//post.setHeader("TH-Client-Version","1.4.1.2813");
			//post.setParams(new BasicHttpParams().setParameter(Constants.LINKED_ACCESS_TOKEN_SCERET, pPassword).setParameter(Constants.LINKED_ACCESS_TOKEN, pUserName));
			//post.setParams(new BasicHttpParams().setParamet	er(Constants.LINKED_ACCESS_TOKEN, pUserName));\
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getResponseBody(context, post);

	} 


	/**
	 * @param context
	 * @param url
	 * @param params
	 * @return
	 */
	public static HttpResponse doPostRequestNgetResponse(Context context, String url, List params) {

		HttpPost post = new HttpPost(url);

		UrlEncodedFormEntity ent = null;

		try {
			ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (ent != null)
			post.setEntity(ent);

		return getResponse(context, post);

	}
	/**
	 * @param context
	 * @param url
	 * @param jsonParams
	 * @return
	 */
	public static String doPostRequestNgetResponseBody(Context context, String url, JSONObject jsonParams,List<Header> headerList) {

		HttpPost post = new HttpPost(url);
		StringEntity ent = null;

		try {

			for (Header header : headerList) {
				post.addHeader(header);
			}
			ent = new StringEntity(jsonParams.toString(), HTTP.UTF_8);
			ent.setContentType("application/json");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (ent != null)
			post.setEntity(ent);

		return getResponseBody(context, post);

	}


	/**
	 * @param context
	 * @param url
	 * @param jsonParams
	 * @return
	 */
	public static String doPostRequestNgetResponseBody(Context context, String url, JSONObject jsonParams) {

		HttpPost post = new HttpPost(url);
		StringEntity ent = null;

		try {
			ent = new StringEntity(jsonParams.toString(), HTTP.UTF_8);
			ent.setContentType("application/json");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (ent != null)
			post.setEntity(ent);

		return getResponseBody(context, post);

	}


	/**
	 * @param context
	 * @param url
	 * @param jsonParams
	 * @return
	 */
	public static HttpResponse doPostRequestNgetResponse(Context context, String url, JSONObject jsonParams) {

		HttpPost post = new HttpPost(url);
		StringEntity ent = null;

		try {
			ent = new StringEntity(jsonParams.toString(), HTTP.UTF_8);
			ent.setContentType("application/json");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (ent != null)
			post.setEntity(ent);


		return getResponse(context, post);

	}

	/**
	 * @param context
	 * @param httpRequest
	 * @return
	 */
	private static String getResponseBody(Context context, HttpRequestBase httpRequest) {
		
		HttpResponse response = getResponse( context, httpRequest);
		//Logger.log("Response ", "Response "+response.toString(), LogLevel.LOGGING_LEVEL_INFO);
		if(response!= null){
			return getResponseBody(response);
		}
		return "";
		
	}

	/**
	 * It returns response body string
	 * @param response
	 * @return
	 */
	public static String getResponseBody(HttpResponse response) {

		HttpEntity resEntity = null;
		if (response != null)
			resEntity = response.getEntity();

		if (resEntity != null) {
			try {
				return EntityUtils.toString(resEntity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	/**
	 * @param context
	 * @param url
	 * @param headerList
	 * @return
	 */
	public static HashMap doGetRequestNgetResponseHeader(Context context, String url, List<Header> headerList) {

		URI uri = null;

		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		HttpGet get = new HttpGet(uri);

		for (Header header : headerList) {
			get.addHeader(header);
		}

		return getResponseHeader(context, get);

	} 

	/**
	 * @param context
	 * @param url
	 * @param params
	 * @return
	 */
	public static HashMap doPostRequestNgetResponseHeader(Context context, String url, List params) {

		HttpPost post = new HttpPost(url);

		UrlEncodedFormEntity ent = null;

		try {
			ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (ent != null)
			post.setEntity(ent);

		return getResponseHeader(context, post);

	} 

	/**
	 * @param context
	 * @param url
	 * @param jsonParams
	 * @return
	 */
	public static HashMap doPostRequestNgetResponseHeader(Context context, String url, JSONObject jsonParams) {

		HttpPost post = new HttpPost(url);
		StringEntity ent = null;

		try {
			ent = new StringEntity(jsonParams.toString(), HTTP.UTF_8);
			ent.setContentType("application/json");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (ent != null)
			post.setEntity(ent);

		return getResponseHeader(context, post);

	}

	/**
	 * @param context
	 * @param httpRequest
	 * @return
	 */
	private static HashMap getResponseHeader(Context context, HttpRequestBase httpRequest) {

		HttpResponse response = getResponse( context, httpRequest);

		return getResponseHeader(response);
	}

	/**
	 * @param response
	 * @param headerName
	 * @return
	 */
	public static String getResponseHeaderValue(HttpResponse response, String headerName){

		HashMap header = getResponseHeader(response);
		return (String)header.get(headerName);

	}
	/**
	 * @param response
	 * @return
	 */
	public static HashMap getResponseHeader(HttpResponse response) {

		if (response != null){
			Header[] headers = response.getAllHeaders();
			return converHeaders2Map(headers);
		}else{
			return new HashMap();
		}

	}

	/**
	 * when response parameter is null, it will return 410 Gone status.
	 * @param response
	 * @return
	 */
	public static int getResponseCode(HttpResponse response) {

		if (response != null){
			return response.getStatusLine().getStatusCode();
		}else{
			return HttpStatus.SC_GONE;
		}

	}

	/**
	 * @param headers
	 * @return
	 */
	private static HashMap converHeaders2Map(Header[] headers){

		HashMap hashMap = new HashMap();

		for(Header header: headers){
			hashMap.put(header.getName(), header.getValue());
		}

		return hashMap;

	}

	/**
	 * @param context
	 * @param httpRequest
	 * @return
	 */
	public static HttpResponse getResponse(Context context, HttpRequestBase httpRequest) {

		HttpClient httpClient = HttpClientUtil.getHttpClient(context);

		HttpResponse response = null;
		try {
			response = httpClient.execute(httpRequest);
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return response;
	}
}
