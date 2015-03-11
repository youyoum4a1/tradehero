package com.tradehero.th.fragments.position.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.fragments.position.partial.AbstractPartialBottomView;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import timber.log.Timber;

public class PositionView extends LinearLayout
{
    @InjectView(R.id.position_partial_top) protected PositionPartialTopView topView;
    @InjectView(R.id.expanding_layout) protected AbstractPartialBottomView/*<PositionDTO, ExpandableListItem<PositionDTO>>*/ bottomView;

    protected boolean hasHistoryButton = true;
    protected ExpandableListItem<PositionDTO> expandableListItem;
    protected PositionDTO positionDTO;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public PositionView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PositionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PositionView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void linkWith(ExpandableListItem<PositionDTO> expandableListItem, boolean andDisplay)
    {
        this.expandableListItem = expandableListItem;
        linkWith(expandableListItem == null ? null : expandableListItem.getModel(), andDisplay);
        if (bottomView != null)
        {
            this.bottomView.linkWith(expandableListItem, andDisplay);
        }
        if (andDisplay)
        {
        }
    }

    public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;

        if (this.topView != null)
        {
            this.topView.linkWith(positionDTO, andDisplay);
        }
        if (this.bottomView != null)
        {
            this.bottomView.linkWith(positionDTO, andDisplay);
        }
    }

    public PositionDTO getPositionDTO()
    {
        Timber.d("getPositionDTO %s", positionDTO);
        Timber.d("getPositionDTO %s", positionDTO.getPositionDTOKey());
        return positionDTO;
    }

    public void display()
    {
        displayTopView();
        displayBottomView();
    }

    public void displayTopView()
    {
        if (topView != null)
        {
            topView.display();
        }
    }

    public void displayBottomView()
    {
        if (bottomView != null)
        {
            bottomView.display();
        }
    }

}
