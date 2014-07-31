package com.tradehero.th.fragments.contestcenter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DateUtils;
import dagger.Lazy;
import javax.inject.Inject;

public class ContestContentView extends RelativeLayout
        implements DTOView<ContestPageDTO>
{
    @Inject protected Lazy<Picasso> picasso;
    @Inject protected Lazy<ProviderCache> providerCache;
    private ContestPageDTO communityPageDTO;
    private ProviderDTO providerDTO;

    @InjectView(R.id.img_provider) ImageView imgActionProvider;
    @InjectView(R.id.tv_action_name) TextView tvActionName;
    @InjectView(R.id.tv_action_date) TextView tvActionDate;
    @InjectView(R.id.tv_action_money) TextView tvActionMoney;
    @InjectView(R.id.tv_action_duration_type) TextView tvActionDurationType;
    @InjectView(R.id.tv_action_rank) TextView tvActionRank;
    @InjectView(R.id.tv_action_roi) TextView tvActionRoi;

    //<editor-fold desc="Constructors">
    public ContestContentView(Context context)
    {
        super(context);
    }

    public ContestContentView(Context context, AttributeSet attrs)
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
        displayView();
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    @Override public void display(ContestPageDTO dto)
    {
        this.communityPageDTO = dto;
        if (communityPageDTO != null)
        {
            linkWith(((ProviderContestPageDTO) communityPageDTO).providerDTO, true);
        }
    }

    private void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        this.providerDTO = providerDTO;
        if (andDisplay)
        {
            displayView();
        }
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
