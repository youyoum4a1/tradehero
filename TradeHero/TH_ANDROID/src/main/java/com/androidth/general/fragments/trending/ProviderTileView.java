package com.androidth.general.fragments.trending;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
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
import dagger.Lazy;
import javax.inject.Inject;

public class ProviderTileView extends ImageView
    implements DTOView<ProviderDTO>
{
    @Inject Lazy<ProviderListCacheRx> providerListCache;
    @Inject Lazy<Picasso> picasso;
    @Inject @ForExtraTileBackground Transformation backgroundTransformation;
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
        if (providerDTO != null)
        {
            String tileImage = providerDTO.isUserEnrolled ? providerDTO.tileJoinedImageUrl : providerDTO.tileImageUrl;
            //if (getHeight() > 0 && getWidth() > 0)
            {
                picasso.get().load(tileImage)
                        .placeholder(R.drawable.white_rounded_background_xml)
                        //.transform(backgroundTransformation)
                        .fit()
                        .into(this);
            }
        }
        else
        {
            //if (getHeight() > 0 && getWidth() > 0)
            {
                picasso.get().load(R.drawable.white_rounded_background_xml)
                        //.transform(backgroundTransformation)
                        .fit()
                        .into(this);
            }
        }
    }

    public int getProviderId()
    {
        return providerDTO != null ? providerDTO.id : 0;
    }
}
