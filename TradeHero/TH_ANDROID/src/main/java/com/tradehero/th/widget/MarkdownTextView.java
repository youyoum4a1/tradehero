package com.tradehero.th.widget;

import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.tradehero.common.text.OnElementClickListener;
import com.tradehero.common.text.RichTextCreator;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.models.intent.THIntentFactory;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.THRouter;
import javax.inject.Inject;

public class MarkdownTextView extends TextView implements OnElementClickListener
{
    @Inject THIntentFactory thIntentFactory;
    @Inject RichTextCreator parser;
    @Inject CurrentUserId currentUserId;
    @Inject THRouter thRouter;

    //<editor-fold desc="Constructors">
    public MarkdownTextView(Context context)
    {
        super(context);
    }

    public MarkdownTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MarkdownTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override protected void onDetachedFromWindow()
    {
        setMovementMethod(null);
        super.onDetachedFromWindow();
    }

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
                //if (matchStrings.length < 3) break;
                //String link = matchStrings[2];
                //Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                //getNavigator().goToPage(thIntentFactory.create(i));

                String USER = "tradehero://user/";
                if (matchStrings.length < 3) break;
                String link = matchStrings[1];
                String link2 = matchStrings[2];
                //"$NASDAQ:GOOG"
                if (link != null && link.startsWith("$"))
                {
                    String str[] = link.substring(1).split(":");
                    if (str.length == 2)
                    {
                        openSecurityProfile(str[0], str[1]);
                    }
                }
                //"tradehero://user/99106"
                else if (link2 != null && link2.startsWith(USER))
                {
                    int uid = Integer.parseInt(link2.substring(USER.length()));
                    openUserProfile(uid);
                }
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

    private DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
    }

    private void openUserProfile(int userId)
    {
        Bundle b = new Bundle();
        thRouter.save(b, new UserBaseKey(userId));

        if (currentUserId.get() != userId)
        {
            getNavigator().pushFragment(PushableTimelineFragment.class, b);
        }
    }
}
