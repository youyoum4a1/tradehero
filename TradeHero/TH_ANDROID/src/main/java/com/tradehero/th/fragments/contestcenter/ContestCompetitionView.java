package com.tradehero.th.fragments.contestcenter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.squareup.widgets.AspectRatioImageView;
import com.squareup.widgets.AspectRatioImageViewCallback;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.inject.HierarchyInjector;
import dagger.Lazy;
import javax.inject.Inject;
import android.support.annotation.Nullable;
import timber.log.Timber;

public class ContestCompetitionView extends AspectRatioImageView
        implements DTOView<ContestPageDTO>
{
    @Inject protected Lazy<Picasso> picasso;
    @Nullable private ContestPageDTO contestPageDTO;
    @Nullable private ProviderDTO providerDTO;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public ContestCompetitionView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ContestCompetitionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        HierarchyInjector.inject(this);
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

    @Override public void display(@Nullable ContestPageDTO dto)
    {
        this.contestPageDTO = dto;
        if (contestPageDTO != null)
        {
            linkWith(((ProviderContestPageDTO) contestPageDTO).providerDTO, true);
        }
    }

    private void linkWith(@Nullable ProviderDTO providerDTO, boolean andDisplay)
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

            int joinBannerResId = providerDTO.specificResources == null ? 0 : providerDTO.specificResources.getJoinBannerResId(providerDTO.isUserEnrolled);
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
