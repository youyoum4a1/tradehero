package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;

/**
 * Created by xavier on 2/5/14.
 */
abstract public class AbstractPartialBottomView<
            PositionDTOType extends PositionDTO,
            ExpandableListItemType extends ExpandableListItem<PositionDTOType>
            >
        extends RelativeLayout
{
    public static final String TAG = AbstractPartialBottomView.class.getSimpleName();

    protected ExpandableListItemType expandableListItem;
    protected PositionDTOType positionDTO;

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
        initViews();
    }

    protected void initViews()
    {
    }

    protected View getExpandingView()
    {
        return this;
    }

    public void linkWith(ExpandableListItemType expandableListItem, boolean andDisplay)
    {
        this.expandableListItem = expandableListItem;
        linkWith(expandableListItem == null ? null : expandableListItem.getModel(), andDisplay);
        if (andDisplay)
        {
            displayExpandingPart();
        }
    }

    public void linkWith(PositionDTOType positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
        if (andDisplay)
        {
            displayModelPart();
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
