package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.thm.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.models.graphics.ForExtraTileBackground;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

public class ProviderTileView extends ImageView
    implements DTOView<ProviderDTO>
{
    @Inject Lazy<ProviderListCache> providerListCache;
    @Inject Lazy<Picasso> picasso;
    @Inject @ForExtraTileBackground Transformation backgroundTransformation;
    private ProviderDTO providerDTO;

    //<editor-fold desc="Constructors">
    public ProviderTileView(Context context)
    {
        super(context);
    }

    public ProviderTileView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ProviderTileView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        ProviderDTOList providerList = providerListCache.get().get(new ProviderListKey());

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
                        .transform(backgroundTransformation)
                        .fit()
                        .into(this);
            }
        }
        else
        {
            //if (getHeight() > 0 && getWidth() > 0)
            {
                picasso.get().load(R.drawable.white_rounded_background_xml)
                        .transform(backgroundTransformation)
                        .fit()
                        .into(this);
            }
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    public int getProviderId()
    {
        return providerDTO != null ? providerDTO.id : 0;
    }
}
