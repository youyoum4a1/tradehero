
package android.tradehero.activities;


import twitter4j.auth.AccessToken;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.tradehero.activities.dialog.TwitterOAuthView;
import android.tradehero.activities.dialog.TwitterOAuthView.Result;
import android.tradehero.utills.Constants;
/**
 * An example Activity implementation using {@link TwitterOAuthView}.
 *
 * @author Mukul
 */
public class TwitterOAuthActivity extends Activity implements TwitterOAuthView.Listener
{
    private static final boolean DUMMY_CALLBACK_URL = false;
    private TwitterOAuthView view;
    private boolean oauthStarted;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = new TwitterOAuthView(this);
        setContentView(view);
        oauthStarted = false;
    }


    @Override
    protected void onResume()
    {
        super.onResume();

/*        if (oauthStarted)
        {
            return;
        }
*/
        oauthStarted = true;

        // Start Twitter OAuth process. Its result will be notified via
        // TwitterOAuthView.Listener interface.
        view.start(Constants.TWITTER_CONSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET, Constants.BASE_API_URL,DUMMY_CALLBACK_URL , this);;
    }


    public void onSuccess(TwitterOAuthView view, AccessToken accessToken)
    {
        // The application has been authorized and an access token
        // has been obtained successfully. Save the access token
        // for later use.
        showMessage("Authorized by " + accessToken.getScreenName());
        setResult(RESULT_OK);
        finish();
    }


    public void onFailure(TwitterOAuthView view, Result result)
    {
        // Failed to get an access token.
        showMessage("Failed due to " + result);
        setResult(-1);
        finish();
    }


    private void showMessage(String message)
    {
        // Show a popup message.
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        
    }
}