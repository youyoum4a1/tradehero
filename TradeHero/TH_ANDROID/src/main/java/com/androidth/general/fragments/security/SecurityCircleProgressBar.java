package com.androidth.general.fragments.security;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.androidth.general.common.widget.CircleProgressBar;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.rx.TimberOnErrorAction1;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

public class SecurityCircleProgressBar extends CircleProgressBar
        implements DTOView<SecurityCompactDTO>
{
    private static final float LOGO_SCALING_INSIDE = 0.6f;
    @Inject Picasso picasso;

    protected Subscription bgSubscription;

    //<editor-fold desc="Constructors">
    public SecurityCircleProgressBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onDetachedFromWindow()
    {
        if (bgSubscription != null)
        {
            bgSubscription.unsubscribe();
        }
        bgSubscription = null;
        super.onDetachedFromWindow();
    }

    /**
     * Starts the animation and informs when done.
     *
     * @param durationMilliSeconds duration of the animation in milli seconds.
     * @return An observable that shoots when the animation has ended.
     */
    @NonNull public Observable<Boolean> start(final long durationMilliSeconds)
    {
        return Observable.create(
                new Observable.OnSubscribe<Boolean>()
                {
                    @Override public void call(final Subscriber<? super Boolean> subscriber)
                    {
                        final Animation progressAnimation = new Animation()
                        {
                            @Override protected void applyTransformation(float interpolatedTime, Transformation t)
                            {
                                super.applyTransformation(interpolatedTime, t);
                                setProgress((int) (durationMilliSeconds * interpolatedTime));
                            }
                        };
                        Animation.AnimationListener listener = new Animation.AnimationListener()
                        {
                            @Override public void onAnimationStart(Animation animation)
                            {
                            }

                            @Override public void onAnimationEnd(Animation animation)
                            {
                                subscriber.onNext(true);
                                subscriber.onCompleted();
                                progressAnimation.setAnimationListener(null);
                            }

                            @Override public void onAnimationRepeat(Animation animation)
                            {
                            }
                        };
                        progressAnimation.setAnimationListener(listener);
                        subscriber.add(Subscriptions.create(new Action0()
                        {
                            @Override public void call()
                            {
                                progressAnimation.setAnimationListener(null);
                            }
                        }));
                        progressAnimation.setDuration(durationMilliSeconds);
                        setMaxProgress((int) durationMilliSeconds);
                        setProgress((int) durationMilliSeconds);
                        startAnimation(progressAnimation);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @Override public void display(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        final RequestCreator request;
        if (securityCompactDTO.imageBlobUrl != null)
        {
            request = picasso.load(securityCompactDTO.imageBlobUrl);
        }
        else
        {
            request = picasso.load(securityCompactDTO.getExchangeLogoId());
        }

        if (bgSubscription != null)
        {
            bgSubscription.unsubscribe();
        }
        bgSubscription = Observable.create(
                new Observable.OnSubscribe<Boolean>()
                {
                    @Override public void call(final Subscriber<? super Boolean> subscriber)
                    {
                        final Target target = new Target()
                        {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                            {
                                // Do whatever you want with the Bitmap
                                setBitmapBg(bitmap);
                                subscriber.onNext(true);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable)
                            {
                                subscriber.onError(new RuntimeException("Failed to load"));
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable)
                            {
                            }
                        };
                        // Be informed when the subscriber is unsubscribed (could be onDetached)
                        Subscription signal = Subscriptions.create(new Action0()
                        {
                            @Override public void call()
                            {
                                picasso.cancelRequest(target);
                            }
                        });
                        subscriber.add(signal);
                        request.resize((int) (getWidth() * LOGO_SCALING_INSIDE), (int) (getHeight() * LOGO_SCALING_INSIDE))
                                .centerInside()
                                .into(target);
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean done)
                            {
                                // Nothing to do
                            }
                        },
                        new TimberOnErrorAction1("Failed to load image"));
    }
}
