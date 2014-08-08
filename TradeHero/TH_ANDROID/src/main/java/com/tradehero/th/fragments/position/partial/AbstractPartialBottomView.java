package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class AbstractPartialBottomView
        extends RelativeLayout
{
    protected ExpandableListItem<PositionDTO> expandableListItem;
    protected PositionDTO positionDTO;

    @Inject protected PositionDTOUtils positionDTOUtils;

    //<editor-fold desc="Constructors">
    public AbstractPartialBottomView(Context context)
    {
        super(context);
    }

    public AbstractPartialBottomView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AbstractPartialBottomView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    protected View getExpandingView()
    {
        return this;
    }

    public void linkWith(ExpandableListItem<PositionDTO> expandableListItem, boolean andDisplay)
    {
        this.expandableListItem = expandableListItem;
        linkWith(expandableListItem == null ? null : expandableListItem.getModel(), andDisplay);
        if (andDisplay)
        {
            displayExpandingPart();
        }
    }

    public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
        if (andDisplay)
        {
            // Let children do it
        }
    }

    public void display()
    {
        displayExpandingPart();
        displayModelPart();
    }

    public void displayExpandingPart()
    {
        View expandableView = getExpandingView();
        if (expandableView != null && expandableListItem != null)
        {
            expandableView.setVisibility(expandableListItem.isExpanded() ? VISIBLE : GONE);
        }
    }

    public void displayModelPart()
    {
    }
}
