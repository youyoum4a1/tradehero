package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZonePrizePoolDTO;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;

public class CompetitionZonePrizePoolView extends AbstractCompetitionZoneListItemView
{
    @InjectView(R.id.background) ImageView background;
    @InjectView(R.id.prize_pool_current_prize) TextView currentPrizePool;
    @InjectView(R.id.prize_pool_next_prize) TextView nextPrizePool;
    @InjectView(R.id.prize_pool_player_needed) TextView playersNeeded;
    @Inject Picasso picasso;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public CompetitionZonePrizePoolView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CompetitionZonePrizePoolView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CompetitionZonePrizePoolView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        HierarchyInjector.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        picasso.cancelRequest(background);
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull CompetitionZoneDTO competitionZoneDTO)
    {
        super.display(competitionZoneDTO);
        CompetitionZonePrizePoolDTO dto = (CompetitionZonePrizePoolDTO) competitionZoneDTO;
        if (currentPrizePool != null)
        {
            currentPrizePool.setText(dto.currentPrizePool);
        }
        if (nextPrizePool != null)
        {
            nextPrizePool.setText(dto.nextPrizePool);
        }
        if (playersNeeded != null)
        {
            playersNeeded.setText(dto.playersNeeded);
        }
        if (background != null)
        {
            picasso.cancelRequest(background);
            picasso.load(dto.backgroundUrl)
                    .fit()
                    .into(background, new Callback()
                    {
                        @Override
                        public void onSuccess()
                        {
                            setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        }

                        @Override
                        public void onError()
                        {
                            setBackgroundColor(getResources().getColor(R.color.white));
                        }
                    });
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.invite_friend)
    public void inviteFriendClicked(View view)
    {
        if (competitionZoneDTO != null)
        {
            userActionSubject.onNext(new UserAction(competitionZoneDTO));
        }
    }

    public static class UserAction extends AbstractCompetitionZoneListItemView.UserAction
    {
        //<editor-fold desc="Constructors">
        public UserAction(@NonNull CompetitionZoneDTO competitionZoneDTO)
        {
            super(competitionZoneDTO);
        }
        //</editor-fold>
    }
}
