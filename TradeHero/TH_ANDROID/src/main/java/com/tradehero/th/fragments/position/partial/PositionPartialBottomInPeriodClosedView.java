package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.fragments.position.LeaderboardPositionItemAdapter;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.PositionUtils;

/**
 * Created by julien on 1/11/13
 */
public class PositionPartialBottomInPeriodClosedView extends AbstractPositionPartialBottomClosedView<PositionInPeriodDTO>
{
    public static final String TAG = PositionPartialBottomInPeriodClosedView.class.getSimpleName();

    private TextView inPeriodPL;
    private TextView inPeriodAdditionalInvested;
    private TextView inPeriodValueAtStart;
    private TextView inPeriodStartValueDate;
    private TextView inPeriodRoiValue;
    private boolean isTimeRestricted;
    private View inPeriodTitle;
    private View inPeriodPositionContainer;

    //<editor-fold desc="Constructors">
    public PositionPartialBottomInPeriodClosedView(Context context)
    {
        super(context);
    }

    public PositionPartialBottomInPeriodClosedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionPartialBottomInPeriodClosedView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void initViews()
    {
        super.initViews();
        // in period
        inPeriodPL = (TextView) findViewById(R.id.in_period_pl_value);
        inPeriodAdditionalInvested = (TextView) findViewById(R.id.in_period_additional_invested);
        inPeriodValueAtStart = (TextView) findViewById(R.id.in_period_start_value);
        inPeriodStartValueDate = (TextView) findViewById(R.id.in_period_start_value_date);
        inPeriodRoiValue = (TextView) findViewById(R.id.in_period_roi_value);
        inPeriodTitle = findViewById(R.id.position_list_in_period_title);
        inPeriodPositionContainer = findViewById(R.id.position_list_bottom_in_period_container);
    }

    public void linkWith(LeaderboardPositionItemAdapter.ExpandableLeaderboardPositionItem item, boolean andDisplay)
    {
        isTimeRestricted = item.isTimeRestricted();
        linkWith(item.getModel(), andDisplay);
    }

    private void displayInPeriod()
    {
        inPeriodPL.setText(PositionUtils.getInPeriodRealizedPL(getContext(), positionDTO));
        PositionUtils.setROIInPeriod(inPeriodRoiValue, positionDTO);
        inPeriodAdditionalInvested.setText(PositionUtils.getAdditionalInvested(getContext(), positionDTO));
        inPeriodValueAtStart.setText(PositionUtils.getValueAtStart(getContext(), positionDTO));

        inPeriodStartValueDate.setText(DateUtils.getDisplayableDate(getContext(), positionDTO.latestTradeUtc));
        //inPeriodStartValueDate.setText(String.format());
    }

    public void display()
    {
        if (isTimeRestricted)
        {
            displayInPeriod();
            setInPeriodVisibility(true);
        }
        else
        {
            setInPeriodVisibility(false);
        }

        super.display();
    }

    private void setInPeriodVisibility(boolean visibility)
    {
        inPeriodPositionContainer.setVisibility(visibility ? VISIBLE : GONE);
        inPeriodTitle.setVisibility(visibility ? VISIBLE : GONE);
    }
}
