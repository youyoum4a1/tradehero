package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneDTO;

public class CompetitionZoneLegalMentionsView extends AbstractCompetitionZoneListItemView
{
    public static final String TAG = CompetitionZoneLegalMentionsView.class.getSimpleName();

    private TextView rules;
    private TextView terms;

    private CompetitionZoneDTO competitionZoneDTO;

    //<editor-fold desc="Constructors">
    public CompetitionZoneLegalMentionsView(Context context)
    {
        super(context);
    }

    public CompetitionZoneLegalMentionsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CompetitionZoneLegalMentionsView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
    }

    private void initViews()
    {
        rules = (TextView) findViewById(R.id.competition_legal_rules);
        terms = (TextView) findViewById(R.id.competition_legal_terms);

        // TODO click listeners
    }

    public void linkWith(CompetitionZoneDTO competitionZoneDTO, boolean andDisplay)
    {
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
}
