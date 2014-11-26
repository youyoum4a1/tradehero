package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
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
import com.tradehero.th.api.competition.ProviderPrizePoolDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZonePrizePoolDTO;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedNumber;
import javax.inject.Inject;

public class CompetitionZonePrizePoolView extends AbstractCompetitionZoneListItemView
{
    @InjectView(R.id.background) ImageView background;
    @InjectView(R.id.prize_pool_current_prize) TextView currentPrizePool;
    @InjectView(R.id.prize_pool_next_prize) TextView nextPrizePool;
    @InjectView(R.id.prize_pool_player_needed) TextView playersNeeded;
    @Inject Picasso picasso;
    @Inject DashboardNavigator navigator;
    private ProviderPrizePoolDTO providerPrizePoolDTO;

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
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void linkWith(CompetitionZoneDTO competitionZoneDTO, boolean andDisplay)
    {
        if (!(competitionZoneDTO instanceof CompetitionZonePrizePoolDTO))
        {
            throw new IllegalArgumentException("Only accepts CompetitionZonePrizePoolDTO");
        }
        super.linkWith(competitionZoneDTO, andDisplay);
        providerPrizePoolDTO = ((CompetitionZonePrizePoolDTO) competitionZoneDTO).providerPrizePoolDTO;

        if (andDisplay)
        {
            displayText();
        }
    }

    private void displayText()
    {
        picasso.load(providerPrizePoolDTO.background)
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
        currentPrizePool.setText(providerPrizePoolDTO.current);
        nextPrizePool.setText(getContext().getString(R.string.provider_prize_pool_new_players_need, providerPrizePoolDTO.extra));
        playersNeeded.setText(THSignedNumber.builder(providerPrizePoolDTO.newPlayerNeeded).build().toString());
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.invite_friend)
    public void inviteFriendClicked(View view)
    {
        pushInvitationFragment();
    }

    private void pushInvitationFragment()
    {
        navigator.pushFragment(FriendsInvitationFragment.class);
    }
}
