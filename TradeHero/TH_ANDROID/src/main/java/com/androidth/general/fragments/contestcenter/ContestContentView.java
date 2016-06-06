package com.androidth.general.fragments.contestcenter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.squareup.picasso.Picasso;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.utils.DateUtils;
import dagger.Lazy;
import javax.inject.Inject;

public class ContestContentView extends RelativeLayout
        implements DTOView<ContestPageDTO>
{
    @Inject protected Lazy<Picasso> picasso;
    private ContestPageDTO communityPageDTO;
    @Nullable private ProviderDTO providerDTO;

    @Bind(R.id.img_provider) ImageView imgActionProvider;
    @Bind(R.id.tv_action_name) TextView tvActionName;
    @Bind(R.id.tv_action_date) TextView tvActionDate;
    @Bind(R.id.tv_action_money) TextView tvActionMoney;
    @Bind(R.id.tv_action_duration_type) TextView tvActionDurationType;
    @Bind(R.id.tv_action_rank) TextView tvActionRank;
    @Bind(R.id.tv_action_roi) TextView tvActionRoi;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public ContestContentView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ContestContentView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
        HierarchyInjector.inject(this);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        displayView();
    }

    @Override public void display(@Nullable ContestPageDTO dto)
    {
        this.communityPageDTO = dto;
        if (communityPageDTO != null)
        {
            linkWith(((ProviderContestPageDTO) communityPageDTO).providerDTO);
        }
    }

    private void linkWith(@Nullable ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
        displayView();
    }

    protected void displayView()
    {
        if (providerDTO != null)
        {
            setVisibility(View.VISIBLE);
            picasso.get().load(providerDTO.logoUrl).into(imgActionProvider);
            tvActionDurationType.setText(providerDTO.durationType);
            tvActionMoney.setText(providerDTO.totalPrize);
            tvActionName.setText(providerDTO.name);
            tvActionDate.setText(DateUtils.getDisplayableDate(getResources(), providerDTO.startDateUtc, providerDTO.endDateUtc));
        }
    }
}
