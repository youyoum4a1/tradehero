package com.ayondo.academy.fragments.position.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.ayondo.academy.R;
import com.ayondo.academy.adapters.ExpandableListItem;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.api.position.PositionDTO;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.fragments.position.partial.AbstractPartialBottomView;
import com.ayondo.academy.fragments.position.partial.PositionPartialBottomClosedView;
import com.ayondo.academy.fragments.position.partial.PositionPartialBottomOpenView;
import com.ayondo.academy.fragments.position.partial.PositionPartialTopView;
import com.ayondo.academy.inject.HierarchyInjector;
import javax.inject.Inject;
import timber.log.Timber;

public class PositionView extends LinearLayout
        implements DTOView<PositionView.DTO>
{
    @Bind(R.id.position_partial_top) protected PositionPartialTopView topView;
    @Bind(R.id.expanding_layout) protected AbstractPartialBottomView/*<PositionDTO, ExpandableListItem<PositionDTO>>*/ bottomView;

    @Inject Picasso picasso;
    private PositionPartialTopView.ViewHolder topViewHolder;

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
        ButterKnife.bind(this);
        HierarchyInjector.inject(this);
        topViewHolder = new PositionPartialTopView.ViewHolder(topView, picasso);
    }

    @Override public void display(DTO dto)
    {
        topViewHolder.onDisplay(dto.topViewDTO);
        if (bottomView != null)
        {
            bottomView.display(dto.bottomViewDTO);
        }
    }

    public void showCaret(boolean show)
    {
        if (show)
        {
            topViewHolder.showCaret();
        }
        else
        {
            topViewHolder.hideCaret();
        }
    }

    public static class DTO
    {
        @NonNull public final PositionPartialTopView.DTO topViewDTO;
        @NonNull public final AbstractPartialBottomView.DTO bottomViewDTO;

        public DTO(@NonNull Resources resources,
                @NonNull CurrentUserId currentUserId,
                @NonNull ExpandableListItem<PositionDTO> expandablePositionDTO,
                @NonNull SecurityCompactDTO securityCompactDTO)
        {
            PositionDTO positionDTO = expandablePositionDTO.getModel();

            topViewDTO = new PositionPartialTopView.DTO(resources, currentUserId, positionDTO, securityCompactDTO);

            Boolean isClosed = positionDTO.isClosed();
            Boolean isOpen = positionDTO.isOpen();
            if (isClosed != null && isClosed)
            {
                bottomViewDTO = new PositionPartialBottomClosedView.DTO(resources, expandablePositionDTO);
            }
            else if (isOpen != null && isOpen)
            {
                bottomViewDTO = new PositionPartialBottomOpenView.DTO(resources, expandablePositionDTO);
            }
            else
            {
                Timber.e(new Exception(), "Position neither closed nor open %s", positionDTO);
                bottomViewDTO = new AbstractPartialBottomView.DTO(expandablePositionDTO);
            }
        }
    }
}
