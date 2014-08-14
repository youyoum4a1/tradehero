package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.widget.list.BaseListHeaderView;
import org.jetbrains.annotations.NotNull;

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

    @Override public void display(@NotNull CompetitionZoneDTO dto)
    {
        setHeaderTextContent(dto.title);
    }
}
