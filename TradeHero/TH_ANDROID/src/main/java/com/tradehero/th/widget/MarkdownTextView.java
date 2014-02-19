package com.tradehero.th.widget;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.tradehero.common.text.OnElementClickListener;
import com.tradehero.common.text.RichTextCreator;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/17/13 Time: 11:18 AM Copyright (c) TradeHero */
public class MarkdownTextView extends TextView implements OnElementClickListener
{
    @Inject RichTextCreator parser;
    @Inject CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    public MarkdownTextView(Context context)
    {
        this(context, null);
    }

    public MarkdownTextView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public MarkdownTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void setText(CharSequence text, BufferType type)
    {
        if (parser != null && text != null)
        {
            text = parser.load(text.toString().trim()).create();
        }
        super.setText(text, BufferType.SPANNABLE);
    }

    @Override public void onClick(View textView, String data, String key, String[] matchStrings)
    {
        switch (key)
        {
            case "user":
                int userId = Integer.parseInt(matchStrings[2]);
                openUserProfile(userId);
                break;
            case "security":
                if (matchStrings.length < 3) break;
                String exchange = matchStrings[1];
                String symbol = matchStrings[2];
                openSecurityProfile(exchange, symbol);
                break;

            case "link":
                if (matchStrings.length < 3) break;
                String link = matchStrings[2];

                break;
        }
    }

    private void openSecurityProfile(String exchange, String symbol)
    {
        SecurityId securityId = new SecurityId(exchange, symbol);
        Bundle args = new Bundle();
        args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        getNavigator().pushFragment(BuySellFragment.class, args);
    }

    private Navigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
    }

    private void openUserProfile(int userId)
    {
        Bundle b = new Bundle();
        b.putInt(PushableTimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userId);

        if (currentUserId.get() != userId)
        {
            getNavigator().pushFragment(PushableTimelineFragment.class, b);
        }
    }
}
