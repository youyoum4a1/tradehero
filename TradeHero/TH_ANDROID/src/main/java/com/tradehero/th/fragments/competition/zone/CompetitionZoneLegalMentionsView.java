package com.ayondo.academy.fragments.competition.zone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.ayondo.academy.R;
import com.ayondo.academy.fragments.competition.zone.dto.CompetitionZoneDTO;
import timber.log.Timber;

public class CompetitionZoneLegalMentionsView extends AbstractCompetitionZoneListItemView
{
    @Bind(R.id.competition_legal_rules) TextView rules;
    @Bind(R.id.competition_legal_terms) TextView terms;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public CompetitionZoneLegalMentionsView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CompetitionZoneLegalMentionsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CompetitionZoneLegalMentionsView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull CompetitionZoneDTO competitionZoneDTO)
    {
        super.display(competitionZoneDTO);
        if (rules != null)
        {
            rules.setText(competitionZoneDTO.title);
        }
        if (terms != null)
        {
            terms.setText(competitionZoneDTO.description);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.competition_legal_rules)
    void pushRulesFragment()
    {
        Timber.d("pushRulesFragment");
        notifyElementClicked(LinkType.RULES);
        // Rely on item click listener
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.competition_legal_terms)
    void pushTermsFragment()
    {
        Timber.d("pushTermsFragment");
        notifyElementClicked(LinkType.TERMS);
    }

    private void notifyElementClicked(@NonNull LinkType linkType)
    {
        if (competitionZoneDTO != null)
        {
            userActionSubject.onNext(new UserAction(competitionZoneDTO, linkType));
        }
    }

    public enum LinkType
    {
        RULES,
        TERMS
    }

    public static class UserAction extends AbstractCompetitionZoneListItemView.UserAction
    {
        @NonNull public final LinkType linkType;

        //<editor-fold desc="Constructors">
        public UserAction(@NonNull CompetitionZoneDTO competitionZoneDTO, @NonNull LinkType linkType)
        {
            super(competitionZoneDTO);
            this.linkType = linkType;
        }
        //</editor-fold>
    }
}
