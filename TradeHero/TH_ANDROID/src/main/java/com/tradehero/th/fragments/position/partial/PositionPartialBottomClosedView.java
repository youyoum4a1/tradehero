package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.utils.DateUtils;

public class PositionPartialBottomClosedView extends AbstractPartialBottomView
{
    @InjectView(R.id.realised_pl_value_header) protected TextView realisedPLValueHeader;
    @InjectView(R.id.realised_pl_value) protected TextView realisedPLValue;
    @InjectView(R.id.roi_value) protected TextView roiValue;
    @InjectView(R.id.total_invested_value) protected TextView totalInvestedValue;
    @InjectView(R.id.opened_date) protected TextView openedDate;
    @InjectView(R.id.closed_date) protected TextView closedDate;
    @InjectView(R.id.period_value) protected TextView periodHeld;

    protected PositionPartialBottomInPeriodViewHolder inPeriodViewHolder;

    //<editor-fold desc="Constructors">
    public PositionPartialBottomClosedView(Context context)
    {
        super(context);
    }

    public PositionPartialBottomClosedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionPartialBottomClosedView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        // in period
        inPeriodViewHolder = new PositionPartialBottomInPeriodViewHolder(this);
    }

    @Override public void display(@NonNull AbstractPartialBottomView.DTO dto)
    {
        super.display(dto);

        if (realisedPLValueHeader != null)
        {
            realisedPLValueHeader.setText(((DTO) dto).realisedPLValueHeader);
        }
        if (realisedPLValue != null)
        {
            realisedPLValue.setText(((DTO) dto).realisedPLValue);
        }
        if (roiValue != null)
        {
            roiValue.setText(((DTO) dto).roiValue);
        }
        if (totalInvestedValue != null)
        {
            totalInvestedValue.setText(((DTO) dto).totalInvestedValue);
        }
        if (openedDate != null)
        {
            openedDate.setText(((DTO) dto).openedDate);
        }
        if (closedDate != null)
        {
            closedDate.setText(((DTO) dto).closedDate);
        }
        if (periodHeld != null)
        {
            periodHeld.setText(((DTO) dto).periodHeld);
        }

        if (inPeriodViewHolder != null)
        {
            inPeriodViewHolder.display(((DTO) dto).positionPartialBottomInPeriodDTO);
        }
    }

    public static class DTO extends AbstractPartialBottomView.DTO
    {
        @NonNull public final String realisedPLValueHeader;
        @NonNull public final Spanned realisedPLValue;
        @NonNull public final Spanned roiValue;
        @NonNull public final String totalInvestedValue;
        @NonNull public final String openedDate;
        @NonNull public final String closedDate;
        @NonNull public final String periodHeld;

        @NonNull public final PositionPartialBottomInPeriodViewHolder.DTO positionPartialBottomInPeriodDTO;

        public DTO(@NonNull Resources resources, @NonNull ExpandableListItem<PositionDTO> expandablePositionDTO)
        {
            super(expandablePositionDTO);

            PositionDTO positionDTO = expandablePositionDTO.getModel();

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

            roiValue = PositionDTOUtils.getROISpanned(resources, positionDTO.getROISinceInception());

            totalInvestedValue = PositionDTOUtils.getSumInvested(resources, positionDTO);

            openedDate = DateUtils.getDisplayableDate(resources, positionDTO.earliestTradeUtc);

            closedDate = DateUtils.getDisplayableDate(resources, positionDTO.latestTradeUtc);

            //<editor-fold desc="Period Held">
            if (positionDTO.earliestTradeUtc != null && positionDTO.latestTradeUtc != null)
            {
                int nDays = DateUtils.getNumberOfDaysBetweenDates(positionDTO.earliestTradeUtc, positionDTO.latestTradeUtc);
                periodHeld = resources.getQuantityString(R.plurals.position_period_held_day, nDays, nDays);
            }
            else
            {
                periodHeld = resources.getString(R.string.na);
            }
            //</editor-fold>

            positionPartialBottomInPeriodDTO = new PositionPartialBottomInPeriodViewHolder.DTO(resources, positionDTO);
        }
    }
}
