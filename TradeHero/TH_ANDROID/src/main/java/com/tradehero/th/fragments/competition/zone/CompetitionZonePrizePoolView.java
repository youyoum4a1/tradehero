package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
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
import javax.inject.Inject;
import timber.log.Timber;

public class CompetitionZonePrizePoolView extends AbstractCompetitionZoneListItemView
{
    @InjectView(R.id.background) ImageView background;
    @InjectView(R.id.value) TextView value;
    @InjectView(R.id.text2) TextView text2;
    @InjectView(R.id.value2) TextView value2;
    @InjectView(R.id.invite_friend) Button inviteFriendButton;
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
            throw new IllegalArgumentException("Only accepts CompetitionZoneLegalDTO");
        }
        super.linkWith(competitionZoneDTO, andDisplay);
        providerPrizePoolDTO = ((CompetitionZonePrizePoolDTO) competitionZoneDTO).providerPrizePoolDTO;

        if (andDisplay)
        {
            displayText();
        }
    }

    private void displayText() {
        picasso.load(providerPrizePoolDTO.background)
                .into(background, new Callback() {
                    @Override
                    public void onSuccess() {
                        Timber.d("lyl success");
                        setBackground(background.getDrawable());
                    }

                    @Override
                    public void onError() {
                        Timber.d("lyl fail");

                    }
                });
        value.setText(providerPrizePoolDTO.current);
        text2.setText(getContext().getString(R.string.new_players_need, providerPrizePoolDTO.extra));
        value2.setText(providerPrizePoolDTO.newPlayerNeeded);
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayText();
    }

    @OnClick(R.id.invite_friend)
    public void clickInviteFriend()
    {
        pushInvitationFragment();
    }

    private void pushInvitationFragment()
    {
        navigator.pushFragment(FriendsInvitationFragment.class);
    }

    //</editor-fold>

}
