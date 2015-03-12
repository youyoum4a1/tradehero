package com.tradehero.th.fragments.position.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.models.position.PositionDTOUtils;

public class PositionLockedView extends LinearLayout
        implements DTOView<PositionLockedView.DTO>
{
    @InjectView(R.id.color_indicator) protected ColorIndicator colorIndicator;
    @InjectView(R.id.position_percentage) protected TextView positionPercent;
    @InjectView(R.id.unrealised_pl_value_header) protected TextView unrealisedPLValueHeader;
    @InjectView(R.id.unrealised_pl_value) protected TextView unrealisedPLValue;
    @InjectView(R.id.realised_pl_value_header) protected TextView realisedPLValueHeader;
    @InjectView(R.id.realised_pl_value) protected TextView realisedPLValue;
    @InjectView(R.id.total_invested_value) protected TextView totalInvestedValue;

    //<editor-fold desc="Constructors">
    public PositionLockedView(Context context)
    {
        super(context);
    }

    public PositionLockedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionLockedView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override public void display(DTO dto)
    {
        if (colorIndicator != null)
        {
            colorIndicator.linkWith(dto.roi);
        }
        if (positionPercent != null)
        {
            positionPercent.setText(dto.positionPercent);
        }
        if (unrealisedPLValueHeader != null)
        {
            unrealisedPLValueHeader.setText(dto.unrealisedPLValueHeader);
        }
        if (unrealisedPLValue != null)
        {
            unrealisedPLValue.setText(dto.unrealisedPLValue);
        }
        if (realisedPLValueHeader != null)
        {
            realisedPLValueHeader.setText(dto.realisedPLValueHeader);
        }
        if (realisedPLValue != null)
        {
            realisedPLValue.setText(dto.realisedPLValue);
        }
        if (totalInvestedValue != null)
        {
            totalInvestedValue.setText(dto.totalInvestedValue);
        }
    }

    public static class DTO
    {
        @Nullable public final Double roi;
        @NonNull public final Spanned positionPercent;
        @NonNull public final String unrealisedPLValueHeader;
        @NonNull public final Spanned unrealisedPLValue;
        @NonNull public final String realisedPLValueHeader;
        @NonNull public final Spanned realisedPLValue;
        @NonNull public final String totalInvestedValue;

        public DTO(@NonNull Resources resources, @NonNull PositionDTO positionDTO)
        {
            roi = positionDTO.getROISinceInception();

            positionPercent = PositionDTOUtils.getROISpanned(resources, positionDTO.getROISinceInception());

            //<editor-fold desc="Unrealised PL Value Header">
            if (positionDTO.unrealizedPLRefCcy != null && positionDTO.unrealizedPLRefCcy < 0)
            {
                unrealisedPLValueHeader = resources.getString(R.string.position_unrealised_loss_header);
            }
            else
            {
                unrealisedPLValueHeader = resources.getString(R.string.position_unrealised_profit_header);
            }
            //</editor-fold>

            unrealisedPLValue = PositionDTOUtils.getUnrealisedPLSpanned(resources, positionDTO);

            //<editor-fold desc="Realised PL Value Header">
            if (positionDTO.unrealizedPLRefCcy != null && positionDTO.realizedPLRefCcy < 0)
            {
                realisedPLValueHeader = resources.getString(R.string.position_realised_loss_header);
            }
            else
            {
                realisedPLValueHeader = resources.getString(R.string.position_realised_profit_header);
            }
            //</editor-fold>

            realisedPLValue = PositionDTOUtils.getRealisedPLSpanned(resources, positionDTO);

            totalInvestedValue = PositionDTOUtils.getSumInvested(resources, positionDTO);
        }
    }
}
