package com.ayondo.academy.fragments.competition.zone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.ayondo.academy.widget.list.BaseListHeaderView;

public class CompetitionZoneHeaderView extends BaseListHeaderView
    implements DTOView<CompetitionZoneDTO>
{
    //<editor-fold desc="Constructors">
    public CompetitionZoneHeaderView(Context context)
    {
        super(context);
    }

    public CompetitionZoneHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CompetitionZoneHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void display(@NonNull CompetitionZoneDTO dto)
    {
        setHeaderTextContent(dto.title);
    }
}
