package com.tradehero.th.utills;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.xml.sax.InputSource;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PostData
{
    private HostnameVerifier hostnameVerifier;
    private DefaultHttpClient client;
    private SchemeRegistry registry;
    private SSLSocketFactory socketFactory;
    private SingleClientConnManager mgr;
    private DefaultHttpClient httpclient;
    private HttpPost httppost;

    public PostData(Context mcontext)
    {

        TelephonyManager mTelephonyMgr = (TelephonyManager) mcontext
                .getSystemService(Context.TELEPHONY_SERVICE);
        // deviceid = mTelephonyMgr.getDeviceId();
        // mSharedPreferences=mcontext.getSharedPreferences("CurrentUser",Context.MODE_PRIVATE);
        hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        client = new DefaultHttpClient();
        registry = new SchemeRegistry();
        socketFactory = SSLSocketFactory.getSocketFactory();
        socketFactory
                .setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
        registry.register(new Scheme("https", socketFactory, 443));
        registry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        mgr = new SingleClientConnManager(client.getParams(), registry);
        httpclient = new DefaultHttpClient(mgr, client.getParams());

        try
        {

            httpclient.getParams().setParameter(
                    HttpConnectionParams.CONNECTION_TIMEOUT, 200000);
        } catch (Exception e)
        {

        }
        // Set verifier
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
    }

    private InputSource retrieveInputStream(HttpEntity httpEntity)
    {
        InputSource insrc = null;
        try
        {
            insrc = new InputSource(new InputStreamReader(
                    httpEntity.getContent(), "utf-8"));
        } catch (Exception e)
        {

        } finally
        {

        }
        return insrc;
    }

    public String executeHttpRequest(String Server_URL, String data)
    {
        String result = "";
        try
        {
            URL url = new URL(Server_URL);
            URLConnection connection = url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            DataOutputStream dataOut = new DataOutputStream(
                    connection.getOutputStream());
            dataOut.writeBytes(data);
            dataOut.flush();
            dataOut.close();

            DataInputStream dataIn = new DataInputStream(
                    connection.getInputStream());
            String inputLine;
            while ((inputLine = dataIn.readLine()) != null)
            {
                result += inputLine;
            }
            dataIn.close();
        } catch (IOException e)
        {
            e.printStackTrace();
            result = "";
        }
        return result;
    }

    public String postHttpexecute(String url, String[] key, String[] value)
    {

        BufferedReader in = null;
        try
        {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(url);

            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            for (int i = 0; i < value.length; i++)
            {
                //				System.out.println("@@@@@@@ key---- " + value[i]);
                postParameters.add(new BasicNameValuePair(key[i], value[i]));
            }

            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                    postParameters);
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);

            in = new BufferedReader(new InputStreamReader(response.getEntity()
                    .getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null)
            {
                sb.append(line + NL);
            }
            in.close();
            String result = sb.toString();
            //			System.out.println("@@@@@@@---- " + result);
            return result;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public String httpGetConnection(String url)
    {
        String result = null;
        Log.d("Final URL : ", url);
        HttpClient httpClient = new DefaultHttpClient();

        // Sending a GET request to the web page that we want
        // Because of we are sending a GET request, we have to pass the values
        // through the URL
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE);
        httpGet.setHeader("Authorization", "Basic bmVlcmFqQGVhdGVjaG5vbG9naWVzLmNvbTp0ZXN0aW5n");
        try
        {
            // execute(); executes a request using the default context.
            // Then we assign the execution result to HttpResponse
            HttpResponse httpResponse = httpClient.execute(httpGet);

            // getEntity() ; obtains the message entity of this response
            // getContent() ; creates a new InputStream object of the entity.
            // Now we need a readable source to read the byte stream that comes
            // as the httpResponse
            InputStream inputStream = httpResponse.getEntity().getContent();

            // We have a byte stream. Next step is to convert it to a Character
            // stream
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream);

            // Then we have to wraps the existing reader (InputStreamReader) and
            // buffer the input
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);

            // InputStreamReader contains a buffer of bytes read from the source
            // stream and converts these into characters as needed.
            // The buffer size is 8K
            // Therefore we need a mechanism to append the separately coming
            // chunks in to one String element
            // We have to use a class that can handle modifiable sequence of
            // characters for use in creating String
            StringBuilder stringBuilder = new StringBuilder();

            String bufferedStrChunk = null;

            // There may be so many buffered chunks. We have to go through each
            // and every chunk of characters
            // and assign a each chunk to bufferedStrChunk String variable
            // and append that value one by one to the stringBuilder
            while ((bufferedStrChunk = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(bufferedStrChunk);
            }

            Log.d("result:", stringBuilder + "");
            return stringBuilder.toString();
        } catch (ClientProtocolException cpe)
        {
            System.out.println("Exception generates caz of httpResponse :"
                    + cpe);
            cpe.printStackTrace();
        } catch (IOException ioe)
        {
            System.out
                    .println("Second exception generates caz of httpResponse :"
                            + ioe);
            ioe.printStackTrace();
        }

        return null;
    }

    public String httpMultipartCon(String url, String displayname,
            String email, String firstName, String lastName, String password,
            String passwordConfirmation)
    {

        StringBuilder stringBuilder = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost;
        HttpResponse response = null;
        MultipartEntity entity;

        try
        {

            System.out.println("upload url..." + url);
            httpPost = new HttpPost(url);
            //httpPost.addHeader("Content-Type", "image/jpeg");
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            //httpPost.addHeader("TH-Client-Version", "1.5.1");
            entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            //			ByteArrayOutputStream bao = new ByteArrayOutputStream();
            //			bmp.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            //			byte [] ba = bao.toByteArray();
            //			entity.addPart("profilePicture", new ByteArrayBody(ba,"profile_pic.jpg"));
            entity.addPart("TH-Client-Version", new StringBody("1.5.1"));
            entity.addPart("displayName", new StringBody(displayname));
            entity.addPart("email", new StringBody(email));
            entity.addPart("firstName", new StringBody(firstName));
            entity.addPart("lastName", new StringBody(lastName));
            entity.addPart("password", new StringBody(password));
            entity.addPart("passwordConfirmation", new StringBody(passwordConfirmation));
            httpPost.setEntity(entity);

            response = httpClient.execute(httpPost, localContext);
            InputStream inputStream = response.getEntity().getContent();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream);
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            stringBuilder = new StringBuilder();

            String bufferedStrChunk = null;
            while ((bufferedStrChunk = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(bufferedStrChunk);
            }

            Log.d("result:", stringBuilder + "");

            System.out.println("SSERVER RESPONSE    ......." + stringBuilder.toString());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}