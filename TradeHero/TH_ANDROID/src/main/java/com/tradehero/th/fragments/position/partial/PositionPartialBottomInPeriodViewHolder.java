package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.fragments.position.LeaderboardPositionItemAdapter;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.PositionUtils;

/**
 * Created by xavier on 2/5/14.
 */
public class PositionPartialBottomInPeriodViewHolder
{
    public static final String TAG = PositionPartialBottomInPeriodViewHolder.class.getSimpleName();

    public TextView inPeriodPL;
    public TextView inPeriodAdditionalInvested;
    public TextView inPeriodValueAtStart;
    public TextView inPeriodStartValueDate;
    public TextView inPeriodRoiValue;
    public View inPeriodTitle;
    public View inPeriodPositionContainer;
    public View overallTitle;

    private final Context context;
    private LeaderboardPositionItemAdapter.ExpandableLeaderboardPositionItem expandableListItem;
    private PositionInPeriodDTO positionInPeriodDTO;

    public PositionPartialBottomInPeriodViewHolder(Context context, View container)
    {
        super();
        this.context = context;
        initViews(container);
    }

    public void initViews(View container)
    {
        inPeriodPL = (TextView) container.findViewById(R.id.in_period_pl_value);
        inPeriodAdditionalInvested = (TextView) container.findViewById(R.id.in_period_additional_invested);
        inPeriodValueAtStart = (TextView) container.findViewById(R.id.in_period_start_value);
        inPeriodStartValueDate = (TextView) container.findViewById(R.id.in_period_start_value_date);
        inPeriodRoiValue = (TextView) container.findViewById(R.id.in_period_roi_value);
        inPeriodTitle = container.findViewById(R.id.position_list_in_period_title);
        inPeriodPositionContainer = container.findViewById(R.id.position_list_bottom_in_period_container);
        overallTitle = container.findViewById(R.id.position_list_overall_title);
    }

    public boolean isShowingInPeriod()
    {
        return positionInPeriodDTO != null && positionInPeriodDTO.isProperInPeriod();
    }

    public void linkWith(LeaderboardPositionItemAdapter.ExpandableLeaderboardPositionItem expandableListItem, boolean andDisplay)
    {
        this.expandableListItem = expandableListItem;
        linkWith(expandableListItem == null ? null : expandableListItem.getModel(), andDisplay);
        if (andDisplay)
        {
        }
    }

    public void linkWith(PositionInPeriodDTO positionDTO, boolean andDisplay)
    {
        this.positionInPeriodDTO = positionDTO;
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
        displayInPeriodPL();
        displayInPeriodRoiValue();
        displayInPeriodAdditionalInvested();
        displayInPeriodValueAtStart();
        displayInPeriodStartValueDate();
    }

    public void displayInPeriodPL()
    {
        if (inPeriodPL != null)
        {
            inPeriodPL.setText(PositionUtils.getInPeriodRealizedPL(context, positionInPeriodDTO));
        }
    }

    public void displayInPeriodRoiValue()
    {
        PositionUtils.setROIInPeriod(inPeriodRoiValue, positionInPeriodDTO);
    }

    public void displayInPeriodAdditionalInvested()
    {
        if (inPeriodAdditionalInvested != null)
        {
            inPeriodAdditionalInvested.setText(PositionUtils.getAdditionalInvested(context, positionInPeriodDTO));
        }
    }

    public void displayInPeriodValueAtStart()
    {
        if (inPeriodValueAtStart != null)
        {
            inPeriodValueAtStart.setText(PositionUtils.getValueAtStart(context, positionInPeriodDTO));
        }
    }

    public void displayInPeriodStartValueDate()
    {
        if (inPeriodStartValueDate != null && positionInPeriodDTO != null)
        {
            inPeriodStartValueDate.setText(context.getString(
                    R.string.position_in_period_as_of,
                    DateUtils.getDisplayableDate(context, positionInPeriodDTO.latestTradeUtc)));
        }
    }
}
