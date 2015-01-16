
package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.tradehero.route.Routable;

@Routable("facebookshare/")
public class FacebookShareActivity extends Activity
{
    private static final String BUNDLE_KEY_NAME = "name";
    private static final String BUNDLE_KEY_DESCRIPTION = "caption";
    private static final String BUNDLE_KEY_CAPTION = "description";
    private static final String BUNDLE_KEY_LINK = "linkUrl";
    private static final String BUNDLE_KEY_PICTURE = "pictureUrl";

    private UiLifecycleHelper uiLifecycleHelper;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        uiLifecycleHelper = new UiLifecycleHelper(this, null);
        uiLifecycleHelper.onCreate(savedInstanceState);

        if (FacebookDialog.canPresentShareDialog(getApplicationContext(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG))
        {
            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                    .setName(getName(extras))
                    .setDescription(getDescription(extras))
                    .setCaption(getCaption(extras))
                    .setLink(getLinkUrl(extras))
                    .setPicture(getPictureUrl(extras))
                    .build();
            uiLifecycleHelper.trackPendingDialogCall(shareDialog.present());
        }
        else
        {
            shareWithFallbackMethod(extras);
        }
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

    private void shareWithFallbackMethod(Bundle b)
    {
        Bundle params = new Bundle();
        params.putString("name", getName(b));
        params.putString("caption", getCaption(b));
        params.putString("description", getDescription(b));
        params.putString("link", getLinkUrl(b));
        params.putString("picture", getPictureUrl(b));

        WebDialog feedDialog = (
                new WebDialog.FeedDialogBuilder(this,
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener((values, error) -> {
                    if (error == null)
                    {
                        // When the story is posted, echo the success
                        // and the post Id.
                        final String postId = values.getString("post_id");
                        if (postId != null)
                        {

                        }
                        else
                        {
                            // User clicked the Cancel button
                        }
                    }
                    else if (error instanceof FacebookOperationCanceledException)
                    {
                        // User clicked the "x" button
                    }
                    else
                    {
                        // Generic, ex: network error
                    }
                })
                .build();
        feedDialog.show();
    }

    @Override protected void onResume()
    {
        super.onResume();
        uiLifecycleHelper.onResume();
    }

    @Override protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        uiLifecycleHelper.onSaveInstanceState(outState);
    }

    @Override protected void onPause()
    {
        super.onPause();
        uiLifecycleHelper.onPause();
    }

    @Override protected void onDestroy()
    {
        super.onDestroy();
        uiLifecycleHelper.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        uiLifecycleHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback()
        {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data)
            {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data)
            {
                Log.i("Activity", "Success!");
            }
        });
    }
}
