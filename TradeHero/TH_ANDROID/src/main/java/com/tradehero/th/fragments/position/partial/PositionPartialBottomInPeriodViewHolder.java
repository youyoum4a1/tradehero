package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.thm.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.fragments.position.LeaderboardPositionItemAdapter;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DateUtils;
import javax.inject.Inject;

public class PositionPartialBottomInPeriodViewHolder
{
    @InjectView(R.id.in_period_pl_value_header) protected TextView inPeriodPLHeader;
    @InjectView(R.id.in_period_pl_value) protected TextView inPeriodPL;
    @InjectView(R.id.in_period_additional_invested) protected TextView inPeriodAdditionalInvested;
    @InjectView(R.id.in_period_start_value) protected TextView inPeriodValueAtStart;
    @InjectView(R.id.in_period_start_value_date) protected TextView inPeriodStartValueDate;
    @InjectView(R.id.in_period_roi_value) protected TextView inPeriodRoiValue;
    @InjectView(R.id.position_list_in_period_title) protected View inPeriodTitle;
    @InjectView(R.id.position_list_bottom_in_period_container) protected View inPeriodPositionContainer;
    @InjectView(R.id.position_list_overall_title) protected View overallTitle;

    private final Context context;
    private LeaderboardPositionItemAdapter.ExpandableLeaderboardPositionItem expandableListItem;
    private PositionDTO positionDTO;

    @Inject protected PositionDTOUtils positionDTOUtils;

    public PositionPartialBottomInPeriodViewHolder(Context context, View container)
    {
        super();
        this.context = context;
        ButterKnife.inject(this, container);
        DaggerUtils.inject(this);
    }

    public boolean isShowingInPeriod()
    {
        return positionDTO instanceof PositionInPeriodDTO;
    }

    public void linkWith(LeaderboardPositionItemAdapter.ExpandableLeaderboardPositionItem expandableListItem, boolean andDisplay)
    {
        this.expandableListItem = expandableListItem;
        linkWith(expandableListItem == null ? null : expandableListItem.getModel(), andDisplay);
        if (andDisplay)
        {
        }
    }

    public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
        if (andDisplay)
        {
            displayInPeriodModelPart();
            displayModelPart();
        }
    }

    public void displayInPeriodModelPart()
    {
        displayInPeriodContainer();
        displayInPeriodTitle();
        displayOverallTitle();
    }

    public void displayInPeriodContainer()
    {
        if (inPeriodPositionContainer != null)
        {
            inPeriodPositionContainer.setVisibility(isShowingInPeriod() ? View.VISIBLE : View.GONE);
        }
    }

    public void displayInPeriodTitle()
    {
        if (inPeriodTitle != null)
        {
            inPeriodTitle.setVisibility(isShowingInPeriod() ? View.VISIBLE : View.GONE);
        }
    }

    public void displayOverallTitle()
    {
        if (overallTitle != null)
        {
            overallTitle.setVisibility(isShowingInPeriod() ? View.VISIBLE : View.GONE);
        }
    }

    public void displayModelPart()
    {
        displayInPeriodPLHeader();
        displayInPeriodPL();
        displayInPeriodRoiValue();
        displayInPeriodAdditionalInvested();
        displayInPeriodValueAtStart();
        displayInPeriodStartValueDate();
    }

    public void displayInPeriodPLHeader()
    {
        if (inPeriodPLHeader != null)
        {
            if (positionDTO instanceof PositionInPeriodDTO && ((PositionInPeriodDTO) positionDTO).totalPLInPeriodRefCcy != null)
            {
                inPeriodPLHeader.setText(
                        ((PositionInPeriodDTO) positionDTO).totalPLInPeriodRefCcy >= 0 ?
                                R.string.position_in_period_profit :
                                R.string.position_in_period_loss);
            }
        }
    }

    public void displayInPeriodPL()
    {
        if (inPeriodPL != null)
        {
            if (positionDTO instanceof PositionInPeriodDTO)
            {
                inPeriodPL.setText(positionDTOUtils.getInPeriodRealizedPL(context.getResources(), (PositionInPeriodDTO) positionDTO));
            }
        }
    }

    public void displayInPeriodRoiValue()
    {
        if (positionDTO instanceof PositionInPeriodDTO)
        {
            positionDTOUtils.setROIInPeriod(inPeriodRoiValue, (PositionInPeriodDTO) positionDTO);
        }
    }

    public void displayInPeriodAdditionalInvested()
    {
        if (inPeriodAdditionalInvested != null)
        {
            if (positionDTO instanceof PositionInPeriodDTO)
            {
                inPeriodAdditionalInvested.setText(positionDTOUtils.getAdditionalInvested(context.getResources(), (PositionInPeriodDTO) positionDTO));
            }
        }
    }

    public void displayInPeriodValueAtStart()
    {
        if (inPeriodValueAtStart != null)
        {
            if (positionDTO instanceof PositionInPeriodDTO)
            {
                inPeriodValueAtStart.setText(positionDTOUtils.getValueAtStart(context.getResources(), (PositionInPeriodDTO) positionDTO));
            }
        }
    }

    public void displayInPeriodStartValueDate()
    {
        if (inPeriodStartValueDate != null && positionDTO != null)
        {
            inPeriodStartValueDate.setText(context.getString(
                    R.string.position_in_period_as_of,
                    DateUtils.getDisplayableDate(context.getResources(), positionDTO.latestTradeUtc)));
        }
    }
}
