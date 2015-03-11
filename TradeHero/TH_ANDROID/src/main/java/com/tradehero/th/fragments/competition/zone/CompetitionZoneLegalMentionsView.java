package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneLegalDTO;
import timber.log.Timber;

public class CompetitionZoneLegalMentionsView extends AbstractCompetitionZoneListItemView
{
    @InjectView(R.id.competition_legal_rules) TextView rules;
    @InjectView(R.id.competition_legal_terms) TextView terms;

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
        ButterKnife.inject(this);
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

    @Override public void display(CompetitionZoneDTO competitionZoneDTO)
    {
        if (!(competitionZoneDTO instanceof CompetitionZoneLegalDTO))
        {
            throw new IllegalArgumentException("Only accepts CompetitionZoneLegalDTO");
        }
        super.display(competitionZoneDTO);
        displayRules();
        displayTerms();
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayRules();
        displayTerms();
    }

    public void displayRules()
    {
        TextView rulesCopy = this.rules;
        if (rulesCopy != null)
        {
            if (competitionZoneDTO != null)
            {
                rulesCopy.setText(competitionZoneDTO.title);
            }
        }
    }

    public void displayTerms()
    {
        TextView termsCopy = this.terms;
        if (termsCopy != null)
        {
            if (competitionZoneDTO != null)
            {
                termsCopy.setText(competitionZoneDTO.description);
            }
        }
    }
    //</editor-fold>

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
        userActionSubject.onNext(new UserAction(competitionZoneDTO, linkType));
    }

    public static enum LinkType
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
