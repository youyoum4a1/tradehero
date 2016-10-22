
package com.androidth.general.activities;

import android.app.Activity;

import com.tradehero.route.Routable;

@Routable("facebookshare/")
public class FacebookShareActivity extends Activity
        implements AchievementAcceptor
{
//    private static final String BUNDLE_KEY_NAME = "name";
//    private static final String BUNDLE_KEY_DESCRIPTION = "description";
//    private static final String BUNDLE_KEY_CAPTION = "caption";
//    private static final String BUNDLE_KEY_MESSAGE = "message";
//    private static final String BUNDLE_KEY_LINK = "link";
//    private static final String BUNDLE_KEY_PICTURE = "picture";
//
    public static final String BASE_ART_WORK = "http://portalvhdskgrrf4wksb8vq.blob.core.windows.net/emails/iTunesArtwork.png";
//
//    private Subscription shareSubscription;
//
//    @Override public void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        shareSubscription = AppObservable.bindActivity(
//                this,
//                FacebookObservable.openActiveToken(this, true))
//                .flatMap(new Func1<AccessToken, Observable<Bundle>>()
//                {
//                    @Override public Observable<Bundle> call(AccessToken session)
//                    {
////                        return FacebookObservable.createFeedDialog(
////                                new WebDialog.FeedDialogBuilder(
////                                        FacebookShareActivity.this,
////                                        session,
////                                        getIntent().getExtras()));
//                        Bundle bundle = getIntent().getExtras();
//                        ShareFeedContent shareFeedContent = new ShareFeedContent.Builder()
//                                .setLinkName(bundle.getString(BUNDLE_KEY_NAME, null))
//                                .setLink(bundle.getString(BUNDLE_KEY_LINK, null))
//                                .setLinkCaption(bundle.getString(BUNDLE_KEY_CAPTION, null))
//                                .setLinkDescription(bundle.getString(BUNDLE_KEY_DESCRIPTION, null))
//                                .setPicture(bundle.getString(BUNDLE_KEY_PICTURE, null))
//                                .build();
//
//                        if(ShareDialog.canShow(ShareFeedContent.class)){
//                            return FacebookObservable.createFeedDialog(new ShareDialog(FacebookShareActivity.this), shareFeedContent);
//                        }else{
//                            return FacebookObservable.createFeedDialog(new ShareDialog(FacebookShareActivity.this), null);
//                        }
//
//                    }
//                })
//                .finallyDo(new Action0()
//                {
//                    @Override public void call()
//                    {
//                        finish();
//                    }
//                })
//                .subscribe(
//                        new Action1<Bundle>()
//                        {
//                            @Override public void call(Bundle values)
//                            {
//                                // When the story is posted, echo the success
//                                // and the post Id.
//                                final String postId = values.getString(WebDialogConstants.RESULT_BUNDLE_KEY_POST_ID);
//                                if (postId != null)
//                                {
//                                    THToast.show(R.string.share_success);
//                                }
//                                else
//                                {
//                                    // User clicked the Cancel button
//                                    THToast.show(R.string.share_cancel);
//                                }
//                            }
//                        },
//                        new Action1<Throwable>()
//                        {
//                            @Override public void call(Throwable error)
//                            {
//                                if (error instanceof FacebookOperationCanceledException)
//                                {
//                                    // User clicked the "x" button
//                                    THToast.show(R.string.share_cancel);
//                                }
//                                else
//                                {
//                                    // Generic, ex: network error
//                                    THToast.show(new THException(error));
//                                }
//                            }
//                        });
//    }
//
//    @Override protected void onDestroy()
//    {
//        shareSubscription.unsubscribe();
//        super.onDestroy();
//    }
//
//    public static void setName(@NonNull Bundle b, @NonNull String name)
//    {
//        b.putString(BUNDLE_KEY_NAME, name);
//    }
//
//    public static void setDescription(@NonNull Bundle b, @NonNull String description)
//    {
//        b.putString(BUNDLE_KEY_DESCRIPTION, description);
//    }
//
//    public static void setCaption(@NonNull Bundle b, @NonNull String caption)
//    {
//        b.putString(BUNDLE_KEY_CAPTION, caption);
//    }
//
//    public static void setMessage(@NonNull Bundle b, @NonNull String message)
//    {
//        b.putString(BUNDLE_KEY_MESSAGE, message);
//    }
//
//    public static void setLinkUrl(@NonNull Bundle b, @NonNull String linkUrl)
//    {
//        b.putString(BUNDLE_KEY_LINK, linkUrl);
//    }
//
//    public static void setPictureUrl(@NonNull Bundle b, @NonNull String pictureUrl)
//    {
//        b.putString(BUNDLE_KEY_PICTURE, pictureUrl);
//    }
//
//    public static void setDefaultPictureUrl(@NonNull Bundle b)
//    {
//        b.putString(BUNDLE_KEY_PICTURE, BASE_ART_WORK);
//    }
//
//    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
////        if (Session.getActiveSession() != null)
////        {
////            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
////        }
//    }
}
