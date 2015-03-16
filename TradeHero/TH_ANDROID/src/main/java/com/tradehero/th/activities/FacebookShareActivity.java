
package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.misc.exception.THException;

@Routable("facebookshare/")
public class FacebookShareActivity extends Activity
        implements AchievementAcceptor
{
    private static final String BUNDLE_KEY_NAME = "name";
    private static final String BUNDLE_KEY_DESCRIPTION = "caption";
    private static final String BUNDLE_KEY_CAPTION = "description";
    private static final String BUNDLE_KEY_LINK = "linkUrl";
    private static final String BUNDLE_KEY_PICTURE = "pictureUrl";

    private Session.StatusCallback callback;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        callback = new Session.StatusCallback()
        {
            @Override public void call(Session session, SessionState state, Exception exception)
            {
                if (state.isOpened())
                {
                    FacebookShareActivity.this.share(getIntent().getExtras());
                }
            }
        };
        Bundle extras = getIntent().getExtras();
        Session session = Session.getActiveSession();
        if (session == null || !session.isOpened())
        {
            Session.openActiveSession(this, true, callback);
        }
        else
        {
            share(extras);
        }
    }

    @Override protected void onDestroy()
    {
        callback = null;
        super.onDestroy();
    }

    private String getName(Bundle b)
    {
        return b.getString(BUNDLE_KEY_NAME);
    }

    private String getDescription(Bundle b)
    {
        return b.getString(BUNDLE_KEY_DESCRIPTION);
    }

    private String getCaption(Bundle b)
    {
        return b.getString(BUNDLE_KEY_CAPTION);
    }

    private String getLinkUrl(Bundle b)
    {
        return b.getString(BUNDLE_KEY_LINK);
    }

    private String getPictureUrl(Bundle b)
    {
        return b.getString(BUNDLE_KEY_PICTURE);
    }

    private void share(Bundle b)
    {
        Bundle params = new Bundle();
        params.putString("name", getName(b));
        params.putString("caption", getCaption(b));
        params.putString("description", getDescription(b));
        params.putString("link", getLinkUrl(b));
        params.putString("picture", getPictureUrl(b));

        WebDialog feedDialog;
        feedDialog = (
                new WebDialog.FeedDialogBuilder(this,
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener()
                {
                    @Override public void onComplete(Bundle values, FacebookException error)
                    {
                        if (error == null)
                        {
                            // When the story is posted, echo the success
                            // and the post Id.
                            final String postId = values.getString("post_id");
                            if (postId != null)
                            {
                                THToast.show(R.string.share_success);
                            }
                            else
                            {
                                // User clicked the Cancel button
                                THToast.show(R.string.share_cancel);
                            }
                        }
                        else if (error instanceof FacebookOperationCanceledException)
                        {
                            // User clicked the "x" button
                            THToast.show(R.string.share_cancel);
                        }
                        else
                        {
                            // Generic, ex: network error
                            THToast.show(new THException(error));
                        }
                        FacebookShareActivity.this.finish();
                    }
                })
                .build();
        feedDialog.show();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(Session.getActiveSession() != null)
        {
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        }
    }
}
