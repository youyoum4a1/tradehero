package com.tradehero.th.fragments.position.partial;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.utils.DateUtils;

public class PositionPartialBottomInPeriodViewHolder implements DTOView<PositionPartialBottomInPeriodViewHolder.DTO>
{
    @InjectView(R.id.position_list_bottom_in_period_container) @Optional protected View inPeriodPositionContainer;
    @InjectView(R.id.position_list_in_period_title) @Optional protected View inPeriodTitle;
    @InjectView(R.id.position_list_overall_title) @Optional protected View overallTitle;
    @InjectView(R.id.in_period_pl_value_header) @Optional protected TextView inPeriodPLHeader;
    @InjectView(R.id.in_period_pl_value) @Optional protected TextView inPeriodPL;
    @InjectView(R.id.in_period_additional_invested) @Optional protected TextView inPeriodAdditionalInvested;
    @InjectView(R.id.in_period_start_value) @Optional protected TextView inPeriodValueAtStart;
    @InjectView(R.id.in_period_start_value_date) @Optional protected TextView inPeriodStartValueDate;
    @InjectView(R.id.in_period_roi_value) @Optional protected TextView inPeriodRoiValue;

    //<editor-fold desc="Constructors">
    public PositionPartialBottomInPeriodViewHolder(@NonNull View container)
    {
        super();
        ButterKnife.inject(this, container);
    }
    //</editor-fold>

    @Override public void display(DTO dto)
    {
        if (inPeriodPositionContainer != null)
        {
            inPeriodPositionContainer.setVisibility(dto.inPeriodVisibility);
        }
        if (inPeriodTitle != null)
        {
            inPeriodTitle.setVisibility(dto.inPeriodVisibility);
        }
        if (overallTitle != null)
        {
            overallTitle.setVisibility(dto.inPeriodVisibility);
        }

        if (inPeriodPLHeader != null)
        {
            inPeriodPLHeader.setText(dto.inPeriodPLHeader);
        }
        if (inPeriodPL != null)
        {
            inPeriodPL.setText(dto.inPeriodPL);
        }
        if (inPeriodAdditionalInvested != null)
        {
            inPeriodAdditionalInvested.setText(dto.inPeriodAdditionalInvested);
        }
        if (inPeriodValueAtStart != null)
        {
            inPeriodValueAtStart.setText(dto.inPeriodValueAtStart);
        }
        if (inPeriodStartValueDate != null)
        {
            inPeriodStartValueDate.setText(dto.inPeriodValueStartDate);
        }
        if (inPeriodRoiValue != null)
        {
            inPeriodRoiValue.setText(dto.inPeriodRoiValue);
        }
    }

    public static class DTO
    {
        @NonNull public final PositionDTO positionDTO;

        @ViewVisibilityValue public final int inPeriodVisibility;
        @NonNull public final String inPeriodPLHeader;
        @NonNull public final String inPeriodPL;
        @NonNull public final String inPeriodAdditionalInvested;
        @NonNull public final String inPeriodValueAtStart;
        @NonNull public final String inPeriodValueStartDate;
        @NonNull public final Spanned inPeriodRoiValue;

        public DTO(@NonNull Resources resources, @NonNull PositionDTO positionDTO)
        {
            this.positionDTO = positionDTO;

            inPeriodVisibility = positionDTO instanceof PositionInPeriodDTO ? View.VISIBLE : View.GONE;

            //<editor-fold desc="In Period PL Header">
            if (positionDTO instanceof PositionInPeriodDTO && ((PositionInPeriodDTO) positionDTO).totalPLInPeriodRefCcy != null)
            {
                inPeriodPLHeader = resources.getString(
                        ((PositionInPeriodDTO) positionDTO).totalPLInPeriodRefCcy >= 0 ?
                                R.string.position_in_period_profit :
                                R.string.position_in_period_loss);
            }
            else
            {
                inPeriodPLHeader = resources.getString(R.string.na);
            }
            //</editor-fold>

            //<editor-fold desc="In Period PL">
            if (positionDTO instanceof PositionInPeriodDTO)
            {
                inPeriodPL = PositionDTOUtils.getInPeriodRealizedPL(resources, (PositionInPeriodDTO) positionDTO);
            }
            else
            {
                inPeriodPL = resources.getString(R.string.na);
            }
            //</editor-fold>

            //<editor-fold desc="In Period ROI Value">
            if (positionDTO instanceof PositionInPeriodDTO)
            {
                inPeriodRoiValue = PositionDTOUtils.getROISpanned(resources, ((PositionInPeriodDTO) positionDTO).getROIInPeriod());
            }
            else
            {
                inPeriodRoiValue = new SpannableString(resources.getString(R.string.na));
            }
            //</editor-fold>

            //<editor-fold desc="In Period Additional Invested">
            if (positionDTO instanceof PositionInPeriodDTO)
            {
                inPeriodAdditionalInvested = PositionDTOUtils.getAdditionalInvested(resources, (PositionInPeriodDTO) positionDTO);
            }
            else
            {
                inPeriodAdditionalInvested = resources.getString(R.string.na);
            }
            //</editor-fold>

            //<editor-fold desc="In Period Value At Start">
            if (positionDTO instanceof PositionInPeriodDTO)
            {
                inPeriodValueAtStart = PositionDTOUtils.getValueAtStart(resources, (PositionInPeriodDTO) positionDTO);
            }
            else
            {
                inPeriodValueAtStart = resources.getString(R.string.na);
            }
            //</editor-fold>

            inPeriodValueStartDate = resources.getString(
                    R.string.position_in_period_as_of,
                    DateUtils.getDisplayableDate(resources, positionDTO.latestTradeUtc));
        }
    }
}
