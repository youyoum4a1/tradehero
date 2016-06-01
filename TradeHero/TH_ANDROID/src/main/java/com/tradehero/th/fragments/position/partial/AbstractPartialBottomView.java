package com.ayondo.academy.fragments.position.partial;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import com.ayondo.academy.adapters.ExpandableListItem;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.api.position.PositionDTO;

public class AbstractPartialBottomView
        extends RelativeLayout
    implements DTOView<AbstractPartialBottomView.DTO>
{
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
        ButterKnife.bind(this);
    }

    @Override public void display(@NonNull DTO dto)
    {
        setVisibility(dto.expandablePositionDTO.isExpanded() ? VISIBLE : GONE);
    }

    public static class DTO
    {
        @NonNull public final ExpandableListItem<PositionDTO> expandablePositionDTO;

        public DTO(@NonNull ExpandableListItem<PositionDTO> expandablePositionDTO)
        {
            this.expandablePositionDTO = expandablePositionDTO;
        }
    }
}
