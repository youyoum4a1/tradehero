package com.androidth.general.common.text;

import android.support.annotation.NonNull;
import android.view.View;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class LinkTagProcessor extends ClickableTagProcessor
{
    private static final String THMarkdownRegexLink = "\\[(.+?)\\]\\((.+?)\\)";/* "[text](link)" = add link to text */
    private static final String USER = "tradehero://user/";
    private static final String HTTP = "http";

    @NonNull protected final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    public LinkTagProcessor(@NonNull CurrentUserId currentUserId)
    {
        super();
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @NonNull @Override public String key()
    {
        return "link";
    }

    @NonNull @Override public String getExtractionPattern(@NonNull MatchResult matchResult)
    {
        return "$1";
    }

    @NonNull @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownRegexLink);
    }

    @NonNull @Override protected Span getSpanElement(String replacement, String[] matchStrings)
    {
        return new LinkClickableSpan(replacement, matchStrings);
    }

    protected class LinkClickableSpan extends RichClickableSpan
    {
        //<editor-fold desc="Constructors">
        public LinkClickableSpan(String replacement, String[] matchStrings)
        {
            super(replacement, matchStrings);
        }
        //</editor-fold>

        @Override public void onClick(View view)
        {
            if (matchStrings.length >= 3)
            {
                String link = matchStrings[1];
                String link2 = matchStrings[2];
                //"$NASDAQ:GOOG"
                if (link != null && link.startsWith("$"))
                {
                    String str[] = link.substring(1).split(":");
                    if (str.length == 2)
                    {
                        userActionSubject.onNext(new SecurityTagProcessor.SecurityUserAction(matchStrings, new SecurityId(str[0], str[1])));
                    }
                }
                //"tradehero://user/99106"
                else if (link2 != null && link2.startsWith(USER))
                {
                    int uid = Integer.parseInt(link2.substring(USER.length()));
                    if (uid != currentUserId.get())
                    {
                        userActionSubject.onNext(new UserTagProcessor.ProfileUserAction(matchStrings, new UserBaseKey(uid)));
                    }
                }
                //"http"
                else if (link2 != null && link2.startsWith(HTTP))
                {
                    userActionSubject.onNext(new LinkTagProcessor.WebUserAction(matchStrings, link2));
                }
            }
        }
    }

    public class WebUserAction extends UserAction
    {
        public final String link;

        public WebUserAction(String[] matchStrings, String link)
        {
            super(matchStrings);
            this.link = link;
        }
    }
}