package com.tradehero.th.fragments.onboarding.exchange;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.market.MarketRegion;
import com.tradehero.th.models.market.MarketRegionDisplayUtil;
import java.util.Collection;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class MarketRegionMapView extends FrameLayout
{
    private static final float ON_CLICK_ALPHA = 0.3f;
    private static final int HALF_CUBE_APPROXIMATION = 20;

    @InjectView(R.id.back_image) ImageView backImage;
    @InjectView(R.id.front_image) ImageView frontImage;
    @NonNull private BehaviorSubject<MarketRegion> marketRegionClickedBehavior;
    @Nullable Bitmap mapBitmap;
    int imW;
    int imH;

    //<editor-fold desc="Constructors">
    public MarketRegionMapView(Context context)
    {
        super(context);
        this.marketRegionClickedBehavior = BehaviorSubject.create();
    }

    public MarketRegionMapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.marketRegionClickedBehavior = BehaviorSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
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
        mapBitmap = ((BitmapDrawable) frontImage.getDrawable()).getBitmap();
        imW = mapBitmap.getWidth();
        imH = mapBitmap.getHeight();
    }

    @Override public boolean onTouchEvent(@NonNull MotionEvent event)
    {
        int eventX = (int) Math.floor(event.getX());
        int eventY = (int) Math.floor(event.getY());
        if (mapBitmap == null || eventX > imW || eventY > imH)
        {
            return false;
        }
        int pixel = mapBitmap.getPixel(eventX, eventY);
        MarketRegion candidateRegion = MarketRegionDisplayUtil.getBestApproxMarketRegion(getResources(), pixel, HALF_CUBE_APPROXIMATION);
        if (candidateRegion != null && !candidateRegion.equals(MarketRegion.OTHER))
        {
            marketRegionClickedBehavior.onNext(candidateRegion);
            frontImage.setAlpha(ON_CLICK_ALPHA);
            frontImage.animate().alpha(0f)
                    .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                    .start();
            return false;
        }
        return true;
    }

    @NonNull public Observable<MarketRegion> getMarketRegionClickedObservable()
    {
        return marketRegionClickedBehavior.asObservable();
    }
}
