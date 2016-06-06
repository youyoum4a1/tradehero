package com.androidth.general.fragments.competition.zone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.androidth.general.api.DTOView;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneDTO;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

abstract public class AbstractCompetitionZoneListItemView extends RelativeLayout
        implements DTOView<CompetitionZoneDTO>
{
    @Nullable protected CompetitionZoneDTO competitionZoneDTO;

    @NonNull protected final PublishSubject<UserAction> userActionSubject;

    //<editor-fold desc="Constructors">
    public AbstractCompetitionZoneListItemView(Context context)
    {
        super(context);
        this.userActionSubject = PublishSubject.create();
    }

    public AbstractCompetitionZoneListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.userActionSubject = PublishSubject.create();
    }

    public AbstractCompetitionZoneListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.userActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @NonNull public Observable<UserAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    public void display(@NonNull CompetitionZoneDTO competitionZoneDTO)
    {
        Timber.d("display %s", competitionZoneDTO);
        this.competitionZoneDTO = competitionZoneDTO;
    }

    public static class UserAction
    {
        @NonNull public final CompetitionZoneDTO competitionZoneDTO;

        //<editor-fold desc="Constructors">
        public UserAction(@NonNull CompetitionZoneDTO competitionZoneDTO)
        {
            this.competitionZoneDTO = competitionZoneDTO;
        }
        //</editor-fold>
    }
}
