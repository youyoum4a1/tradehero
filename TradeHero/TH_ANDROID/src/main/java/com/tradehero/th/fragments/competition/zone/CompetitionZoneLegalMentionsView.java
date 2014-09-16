package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
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
    private OnElementClickedListener elementClickedListener;

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

    public void linkWith(CompetitionZoneDTO competitionZoneDTO, boolean andDisplay)
    {
        if (!(competitionZoneDTO instanceof CompetitionZoneLegalDTO))
        {
            throw new IllegalArgumentException("Only accepts CompetitionZoneLegalDTO");
        }
        super.linkWith(competitionZoneDTO, andDisplay);

        if (andDisplay)
        {
            displayRules();
            displayTerms();
        }
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

    public void setOnElementClickedListener(OnElementClickedListener elementClickedListener)
    {
        this.elementClickedListener = elementClickedListener;
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.competition_legal_rules)
    void pushRulesFragment()
    {
        Timber.d("pushRulesFragment");
        notifyElementClicked(CompetitionZoneLegalDTO.LinkType.RULES);
        // Rely on item click listener
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.competition_legal_terms) void pushTermsFragment()
    {
        Timber.d("pushTermsFragment");
        notifyElementClicked(CompetitionZoneLegalDTO.LinkType.TERMS);
    }

    private void notifyElementClicked(CompetitionZoneLegalDTO.LinkType linkType)
    {
        OnElementClickedListener listenerCopy = this.elementClickedListener;
        ((CompetitionZoneLegalDTO) competitionZoneDTO).requestedLink = linkType;
        if (listenerCopy != null)
        {
            listenerCopy.onElementClicked(competitionZoneDTO);
        }
    }

    public static interface OnElementClickedListener
    {
        void onElementClicked(CompetitionZoneDTO competitionZoneDTO);
    }
}
