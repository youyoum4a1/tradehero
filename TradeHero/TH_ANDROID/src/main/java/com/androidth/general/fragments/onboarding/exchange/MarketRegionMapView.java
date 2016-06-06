package com.androidth.general.fragments.onboarding.exchange;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.androidth.general.R;
import com.androidth.general.api.market.MarketRegion;
import com.androidth.general.models.market.MarketRegionDisplayUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class MarketRegionMapView extends FrameLayout
{
    private static final float ON_CLICK_ALPHA = 1f;
    private static final int HALF_CUBE_APPROXIMATION = 20;

    @Bind(R.id.back_image) ImageView backImage;
    @Bind(R.id.front_image) ImageView frontImage;
    @NonNull private Map<MarketRegion, MapHitBoxView> feedbackHitBoxes;
    @NonNull private PublishSubject<MarketRegion> marketRegionClickedBehavior;
    @NonNull private PublishSubject<Boolean> switchClickedBehavior;
    @Nullable Bitmap mapBitmap;
    int imW;
    int imH;

    //<editor-fold desc="Constructors">
    public MarketRegionMapView(Context context)
    {
        super(context);
        this.marketRegionClickedBehavior = PublishSubject.create();
        this.switchClickedBehavior = PublishSubject.create();
        this.feedbackHitBoxes = new HashMap<>();
    }

    public MarketRegionMapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.marketRegionClickedBehavior = PublishSubject.create();
        this.switchClickedBehavior = PublishSubject.create();
        this.feedbackHitBoxes = new HashMap<>();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
        int childCount = getChildCount();
        View child;
        for (int index = 0; index < childCount; index++)
        {
            child = getChildAt(index);
            try
            {
                if (child instanceof  MapHitBoxView)
                {
                    ((MapHitBoxView) child).loadImage();
                    feedbackHitBoxes.put(((MapHitBoxView) child).params.region, (MapHitBoxView) child);
                }
            } catch (OutOfMemoryError e)
            {
                Timber.e(e, "Could not load image for child %d", index);
            }
        }
    }

    /**
     * Loads the necessary map images. It provides the opportunity to catch an OutOfMemoryError as the images are large.
     */
    public void loadBackMap()
    {
        backImage.setImageResource(R.drawable.map_white);
    }

    /**
     * It provides the opportunity to catch an OutOfMemoryError as the images are large.
     *
     * @param enabledRegions The regions that are to be enabled, but ignored here
     */
    public void enable(@NonNull Collection<? extends MarketRegion> enabledRegions)
    {
        backImage.setImageResource(R.drawable.map);
        frontImage.setImageResource(R.drawable.map_hitboxes);
        showClicked(frontImage);
        mapBitmap = ((BitmapDrawable) frontImage.getDrawable()).getBitmap();
        imW = mapBitmap.getWidth();
        imH = mapBitmap.getHeight();
    }

    @Override public boolean onTouchEvent(@NonNull MotionEvent event)
    {
        int eventX = (int) Math.floor(event.getX());
        int eventY = (int) Math.floor(event.getY());
        if (mapBitmap == null || eventX >= imW || eventY >= imH || eventX < 0 || eventY < 0)
        {
            return false;
        }
        int pixel = mapBitmap.getPixel(eventX, eventY);
        MarketRegion candidateRegion = MarketRegionDisplayUtil.getBestApproxMarketRegion(getResources(), pixel, HALF_CUBE_APPROXIMATION);
        if (candidateRegion != null && !candidateRegion.equals(MarketRegion.OTHER))
        {
            showClicked(candidateRegion);
            return false;
        }
        else
        {
            switchClickedBehavior.onNext(true);
        }
        return true;
    }

    public void showClicked(@NonNull MarketRegion candidateRegion)
    {
        marketRegionClickedBehavior.onNext(candidateRegion);
        View hitBoxView = feedbackHitBoxes.get(candidateRegion);
        if (hitBoxView == null)
        {
            hitBoxView = frontImage;
        }
        for (MapHitBoxView candidate : feedbackHitBoxes.values())
        {
            if (candidate != hitBoxView)
            {
                candidate.animate().cancel();
                candidate.setSelected(false);
            }
        }
        showClicked(hitBoxView);
    }

    private void showClicked(@NonNull View hitBoxView)
    {
        float targetAlpha = (hitBoxView instanceof MapHitBoxView) ? ((MapHitBoxView) hitBoxView).params.alphaSelected : 0f;
        hitBoxView.setAlpha(ON_CLICK_ALPHA);
        hitBoxView.animate().alpha(targetAlpha)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .start();
    }

    @NonNull public Observable<MarketRegion> getMarketRegionClickedObservable()
    {
        return marketRegionClickedBehavior.asObservable();
    }

    @NonNull public Observable<Boolean> getSwitchClickedObservable()
    {
        return switchClickedBehavior.asObservable();
    }
}
