
package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.tradehero.common.social.facebook.FacebookObservable;
import com.tradehero.common.social.facebook.WebDialogConstants;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.misc.exception.THException;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

@Routable("facebookshare/")
public class FacebookShareActivity extends Activity
        implements AchievementAcceptor
{
    private static final String BUNDLE_KEY_NAME = "name";
    private static final String BUNDLE_KEY_DESCRIPTION = "description";
    private static final String BUNDLE_KEY_CAPTION = "caption";
    private static final String BUNDLE_KEY_MESSAGE = "message";
    private static final String BUNDLE_KEY_LINK = "link";
    private static final String BUNDLE_KEY_PICTURE = "picture";

    public static final String BASE_ART_WORK = "http://portalvhdskgrrf4wksb8vq.blob.core.windows.net/emails/iTunesArtwork.png";

    private Subscription shareSubscription;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        shareSubscription = AppObservable.bindActivity(
                this,
                FacebookObservable.openActiveSession(this, true))
                .flatMap(new Func1<Session, Observable<Bundle>>()
                {
                    @Override public Observable<Bundle> call(Session session)
                    {
                        return FacebookObservable.createFeedDialog(
                                new WebDialog.FeedDialogBuilder(
                                        FacebookShareActivity.this,
                                        session,
                                        getIntent().getExtras()));
                    }
                })
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        finish();
                    }
                })
                .subscribe(
                        new Action1<Bundle>()
                        {
                            @Override public void call(Bundle values)
                            {
                                // When the story is posted, echo the success
                                // and the post Id.
                                final String postId = values.getString(WebDialogConstants.RESULT_BUNDLE_KEY_POST_ID);
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
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable error)
                            {
                                if (error instanceof FacebookOperationCanceledException)
                                {
                                    // User clicked the "x" button
                                    THToast.show(R.string.share_cancel);
                                }
                                else
                                {
                                    // Generic, ex: network error
                                    THToast.show(new THException(error));
                                }
                            }
                        });
    }

    @Override protected void onDestroy()
    {
        shareSubscription.unsubscribe();
        super.onDestroy();
    }

    public static void setName(@NonNull Bundle b, @NonNull String name)
    {
        b.putString(BUNDLE_KEY_NAME, name);
    }

    public static void setDescription(@NonNull Bundle b, @NonNull String description)
    {
        b.putString(BUNDLE_KEY_DESCRIPTION, description);
    }

    public static void setCaption(@NonNull Bundle b, @NonNull String caption)
    {
        b.putString(BUNDLE_KEY_CAPTION, caption);
    }

    public static void setMessage(@NonNull Bundle b, @NonNull String message)
    {
        b.putString(BUNDLE_KEY_MESSAGE, message);
    }

    public static void setLinkUrl(@NonNull Bundle b, @NonNull String linkUrl)
    {
        b.putString(BUNDLE_KEY_LINK, linkUrl);
    }

    public static void setPictureUrl(@NonNull Bundle b, @NonNull String pictureUrl)
    {
        b.putString(BUNDLE_KEY_PICTURE, pictureUrl);
    }

    public static void setDefaultPictureUrl(@NonNull Bundle b)
    {
        b.putString(BUNDLE_KEY_PICTURE, BASE_ART_WORK);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (Session.getActiveSession() != null)
        {
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        }
    }
}
