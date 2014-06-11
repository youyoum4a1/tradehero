package com.tradehero.th.fragments.leaderboard.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.models.provider.ProviderSpecificResourcesDTO;
import com.tradehero.th.models.provider.ProviderSpecificResourcesFactory;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

public class LeaderboardCompetitionView extends ImageView
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

    public LeaderboardCompetitionView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        DaggerUtils.inject(this);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
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
            providerDTO = providerCache.get().get(((ProviderCommunityPageDTO) communityPageDTO).providerId);
            if (providerDTO != null)
            {
                linkWith(providerDTO, true);
            }
        }
    }

    private void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        if (providerDTO != null && andDisplay)
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
                        .into(this);
            }
        }
    }
}
