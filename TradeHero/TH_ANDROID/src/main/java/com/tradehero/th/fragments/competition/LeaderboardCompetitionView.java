package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/3/14 Time: 6:31 PM Copyright (c) TradeHero
 */
public class LeaderboardCompetitionView extends ImageView
        implements DTOView<ProviderId>
{
    @Inject protected Lazy<Picasso> picasso;
    @Inject protected Lazy<ProviderCache> providerCache;
    private ProviderId providerId;
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
    }

    @Override public void display(ProviderId dto)
    {
        this.providerId = dto;
        if (providerId != null)
        {
            providerDTO = providerCache.get().get(providerId);
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

            // TODO this is a hack, to overcome the problem of not possible to get 9-patch image from server side
            if (providerDTO.id == 22 && !providerDTO.isUserEnrolled) // phillips competition
            {
                picasso.get()
                        .load(R.drawable.lb_philip_macquarie_large_notjoined)
                        .into(this);
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
