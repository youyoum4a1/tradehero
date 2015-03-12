package com.tradehero.th.fragments.position.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.position.partial.AbstractPartialBottomView;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomClosedView;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomOpenView;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import timber.log.Timber;

public class PositionView extends LinearLayout
        implements DTOView<PositionView.DTO>
{
    @InjectView(R.id.position_partial_top) protected PositionPartialTopView topView;
    @InjectView(R.id.expanding_layout) protected AbstractPartialBottomView/*<PositionDTO, ExpandableListItem<PositionDTO>>*/ bottomView;

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

    @Override public void display(DTO dto)
    {
        if (topView != null)
        {
            topView.display(dto.topViewDTO);
        }
        if (bottomView != null)
        {
            bottomView.display(dto.bottomViewDTO);
        }
    }

    public static class DTO
    {
        @NonNull public final PositionPartialTopView.DTO topViewDTO;
        @NonNull public final AbstractPartialBottomView.DTO bottomViewDTO;

        public DTO(@NonNull Resources resources,
                @NonNull ExpandableListItem<PositionDTO> expandablePositionDTO,
                @NonNull SecurityCompactDTO securityCompactDTO)
        {
            PositionDTO positionDTO = expandablePositionDTO.getModel();

            topViewDTO = new PositionPartialTopView.DTO(resources, positionDTO, securityCompactDTO);

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
