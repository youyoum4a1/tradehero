package com.androidth.general.fragments.trending;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderDTOList;
import com.androidth.general.api.competition.key.ProviderListKey;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.graphics.ForExtraTileBackground;
import com.androidth.general.persistence.competition.ProviderListCacheRx;

import butterknife.Bind;
import dagger.Lazy;
import javax.inject.Inject;

public class ProviderTileView extends LinearLayout
    implements DTOView<ProviderDTO>
{
    @Inject Lazy<ProviderListCacheRx> providerListCache;
    @Inject Lazy<Picasso> picasso;
    @Inject @ForExtraTileBackground Transformation backgroundTransformation;

    @Bind(R.id.tile_provider_image) ImageView tileImageView;
    @Bind(R.id.tile_provider_shimmer) ShimmerFrameLayout shimmerFrameLayout;

    private ProviderDTO providerDTO;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public ProviderTileView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ProviderTileView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ProviderTileView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.tile_provider_shimmer);
        tileImageView = (ImageView) findViewById(R.id.tile_provider_image);

        makeItFancy(shimmerFrameLayout);

        HierarchyInjector.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        ProviderDTOList providerList = providerListCache.get().getCachedValue(new ProviderListKey());

        if (providerList != null && providerList.size() > 0)
        {
            int randomProviderId = (int) Math.floor(Math.random() * providerList.size());
            display(providerList.get(randomProviderId));
        }
    }

    @Override public void display(ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
        Log.v("Picasso", "ProviderDTO= "+providerDTO);
        if (providerDTO != null)
        {
            String tileImage = providerDTO.isUserEnrolled ? providerDTO.tileJoinedImageUrl : providerDTO.tileImageUrl;
            //if (getHeight() > 0 && getWidth() > 0)
            {
                if(tileImageView!=null){
                    picasso.get().load(tileImage)
                            .placeholder(R.drawable.white_rounded_background_xml)
                            //.transform(backgroundTransformation)
                            .fit()
                            .into(tileImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.v("Picasso", "Loaded success");
                                }

                                @Override
                                public void onError() {
                                    Log.v("Picasso", "Loaded fail");
                                }
                            });
                }else{

                }

            }
        }
        else
        {
            if(tileImageView!=null){
                picasso.get().load(R.drawable.white_rounded_background_xml)
                        //.transform(backgroundTransformation)
                        .fit()
                        .into(tileImageView);
            }
        }
    }

    public int getProviderId()
    {
        return providerDTO != null ? providerDTO.id : 0;
    }

    private void makeItFancy(ShimmerFrameLayout layout){
        layout.setDropoff(0.6f);
        layout.setBaseAlpha(0.6f);
        layout.setBackgroundColor(Color.TRANSPARENT);
        layout.setDuration(3000);
        layout.setTilt(30.0f);
        shimmerFrameLayout.startShimmerAnimation();
    }
}
