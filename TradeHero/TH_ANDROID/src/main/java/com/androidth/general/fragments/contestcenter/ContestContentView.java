package com.androidth.general.fragments.contestcenter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.BindView;
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

    @BindView(R.id.img_provider) ImageView imgActionProvider;
    @BindView(R.id.tv_action_name) TextView tvActionName;
    @BindView(R.id.tv_action_date) TextView tvActionDate;
    @BindView(R.id.tv_action_money) TextView tvActionMoney;
    @BindView(R.id.tv_action_duration_type) TextView tvActionDurationType;
    @BindView(R.id.tv_action_rank) TextView tvActionRank;
    @BindView(R.id.tv_action_roi) TextView tvActionRoi;

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
