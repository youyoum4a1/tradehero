package com.ayondo.academy.fragments.competition.zone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.ayondo.academy.models.graphics.ForUserPhoto;
import javax.inject.Inject;

public class CompetitionZonePortfolioView extends CompetitionZoneListItemView
{
    @Inject @ForUserPhoto protected Transformation zoneIconTransformation;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public CompetitionZonePortfolioView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CompetitionZonePortfolioView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CompetitionZonePortfolioView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @NonNull @Override protected RequestCreator prepareZoneIcon(@NonNull RequestCreator request)
    {
        return super.prepareZoneIcon(request.transform(zoneIconTransformation));
    }
}
