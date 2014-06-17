package com.tradehero.th.fragments.leaderboard.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.squareup.widgets.AspectRatioImageView;
import com.squareup.widgets.AspectRatioImageViewCallback;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.models.provider.ProviderSpecificResourcesDTO;
import com.tradehero.th.models.provider.ProviderSpecificResourcesFactory;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

public class LeaderboardCompetitionView extends AspectRatioImageView
        implements DTOView<CommunityPageDTO>
{
    @Inject protected Lazy<Picasso> picasso;
    @Inject protected Lazy<ProviderCache> providerCache;
    @Inject protected ProviderSpecificResourcesFactory providerSpecificResourcesFactory;
    private CommunityPageDTO communityPageDTO;
    private ProviderDTO providerDTO;

    //<editor-fold desc="Constructors">
    public LeaderboardCompetitionView(Context context)
    {
        super(context);
    }

    public LeaderboardCompetitionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        DaggerUtils.inject(this);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        displayImageView();
    }

    @Override protected void onDetachedFromWindow()
    {
        setImageDrawable(null);
        super.onDetachedFromWindow();
    }

    @Override public void display(CommunityPageDTO dto)
    {
        this.communityPageDTO = dto;
        if (communityPageDTO != null)
        {
            ProviderDTO cachedProviderDTO = providerCache.get().get(((ProviderCommunityPageDTO) communityPageDTO).providerId);
            if (cachedProviderDTO != null)
            {
                linkWith(cachedProviderDTO, true);
            }
        }
    }

    private void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        this.providerDTO = providerDTO;
        if (andDisplay)
        {
            displayImageView();
        }
    }

    protected void displayImageView()
    {
        if (providerDTO != null)
        {
            setVisibility(View.VISIBLE);

            ProviderSpecificResourcesDTO providerSpecificResourcesDTO = providerSpecificResourcesFactory.createResourcesDTO(providerDTO);
            int joinBannerResId = providerSpecificResourcesDTO == null ? 0 : providerSpecificResourcesDTO.getJoinBannerResId(providerDTO.isUserEnrolled);
            if (joinBannerResId != 0)
            {
                try
                {
                    setImageResource(joinBannerResId);
                }
                catch (OutOfMemoryError e)
                {
                    Timber.e(e, "providerId %d", providerDTO.id);
                }
            }
            else
            {
                picasso.get()
                        .load(providerDTO.getStatusSingleImageUrl())
                        .into(this, new AspectRatioImageViewCallback(this));
            }
        }
    }
}
