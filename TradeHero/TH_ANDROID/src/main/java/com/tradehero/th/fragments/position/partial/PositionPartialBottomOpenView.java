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
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.position.PositionDTOUtils;

public class PositionPartialBottomOpenView
        extends AbstractPartialBottomView
{
    @InjectView(R.id.unrealised_pl_value_header) protected TextView unrealisedPLValueHeader;
    @InjectView(R.id.unrealised_pl_value) protected TextView unrealisedPLValue;
    @InjectView(R.id.realised_pl_value_header) protected TextView realisedPLValueHeader;
    @InjectView(R.id.realised_pl_value) protected TextView realisedPLValue;
    @InjectView(R.id.total_invested_value) protected TextView totalInvestedValue;
    @InjectView(R.id.market_value_value) protected TextView marketValueValue;
    @InjectView(R.id.quantity_value) protected TextView quantityValue;
    @InjectView(R.id.average_price_value) protected TextView averagePriceValue;

    protected PositionPartialBottomInPeriodViewHolder inPeriodViewHolder;

    //<editor-fold desc="Constructors">
    public PositionPartialBottomOpenView(Context context)
    {
        super(context);
    }

    public PositionPartialBottomOpenView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionPartialBottomOpenView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        inPeriodViewHolder = new PositionPartialBottomInPeriodViewHolder(this);
    }

    @Override public void display(@NonNull AbstractPartialBottomView.DTO dto)
    {
        super.display(dto);
        if (!(dto instanceof DTO)) {
            return;
        }
        unrealisedPLValueHeader.setText(((DTO) dto).unrealisedPLValueHeader);
        unrealisedPLValue.setText(((DTO) dto).unrealisedPLValue);
        realisedPLValueHeader.setText(((DTO) dto).realisedPLValueHeader);
        realisedPLValue.setText(((DTO) dto).realisedPLValue);
        totalInvestedValue.setText(((DTO) dto).totalInvestedValue);
        marketValueValue.setText(((DTO) dto).marketValue);
        quantityValue.setText(((DTO) dto).quantityValue);
        averagePriceValue.setText(((DTO) dto).averagePriceValue);

        inPeriodViewHolder.display(((DTO) dto).positionPartialBottomInPeriodDTO);
    }

    public static class DTO extends AbstractPartialBottomView.DTO
    {
        @NonNull public final String unrealisedPLValueHeader;
        @NonNull public final Spanned unrealisedPLValue;
        @NonNull public final String realisedPLValueHeader;
        @NonNull public final Spanned realisedPLValue;
        @NonNull public final String totalInvestedValue;
        @NonNull public final String marketValue;
        @NonNull public final String quantityValue;
        @NonNull public final String averagePriceValue;

        @NonNull public final PositionPartialBottomInPeriodViewHolder.DTO positionPartialBottomInPeriodDTO;

        public DTO(@NonNull Resources resources, @NonNull ExpandableListItem<PositionDTO> expandablePositionDTO)
        {
            super(expandablePositionDTO);

            PositionDTO positionDTO = expandablePositionDTO.getModel();

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

            marketValue = PositionDTOUtils.getMarketValue(resources, positionDTO);

            //<editor-fold desc="Quantity Value">
            if (positionDTO.shares != null)
            {
                quantityValue = THSignedNumber.builder(positionDTO.shares)
                        .withOutSign()
                        .build().toString();
            }
            else
            {
                quantityValue = resources.getString(R.string.na);
            }
            //</editor-fold>

            //<editor-fold desc="Average Price Value">
            if (positionDTO.averagePriceRefCcy != null)
            {
                averagePriceValue = THSignedMoney.builder(positionDTO.averagePriceRefCcy)
                        .withOutSign()
                        .currency(positionDTO.getNiceCurrency())
                        .build().toString();
            }
            else
            {
                averagePriceValue = resources.getString(R.string.na);
            }
            //</editor-fold>

            positionPartialBottomInPeriodDTO = new PositionPartialBottomInPeriodViewHolder.DTO(resources, positionDTO);
        }
    }
}
