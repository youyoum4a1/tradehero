package com.tradehero.th.fragments.competition.zone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneLegalDTO;

public class CompetitionZoneLegalMentionsView extends AbstractCompetitionZoneListItemView
{
    public static final String TAG = CompetitionZoneLegalMentionsView.class.getSimpleName();

    private TextView rules;
    private TextView terms;
    private ProviderId providerId;
    private OnElementClickedListener elementClickedListener;

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
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (rules != null)
        {
            rules.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    pushRulesFragment();
                }
            });
        }
        if (terms != null)
        {
            terms.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    pushTermsFragment();
                }
            });
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (rules != null)
        {
            rules.setOnClickListener(null);
        }
        if (terms != null)
        {
            terms.setOnClickListener(null);
        }
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

    public void linkWith(ProviderId providerId, boolean andDisplay)
    {
        this.providerId = providerId;

        if (andDisplay)
        {
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

    private void pushRulesFragment()
    {
        THLog.d(TAG, "pushRulesFragment");
        notifyElementClicked(CompetitionZoneLegalDTO.LinkType.RULES);
        // Rely on item click listener
    }

    private void pushTermsFragment()
    {
        THLog.d(TAG, "pushTermsFragment");
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
