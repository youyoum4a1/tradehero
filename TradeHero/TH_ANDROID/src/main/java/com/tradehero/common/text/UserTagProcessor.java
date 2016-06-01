package com.tradehero.common.text;

import android.support.annotation.NonNull;
import android.view.View;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class UserTagProcessor extends ClickableTagProcessor
{
    private static final String THMarkdownURegexUser = "<@(.+?),(\\d+)@>";

    @NonNull protected final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    public UserTagProcessor(@NonNull CurrentUserId currentUserId)
    {
        super();
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    //  <@dom,123@> = user link for userId 123*/
    @NonNull @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownURegexUser);
    }

    @NonNull @Override public String getExtractionPattern(@NonNull MatchResult matchResult)
    {
        return "$1";
    }

    @NonNull @Override public String key()
    {
        return "user";
    }

    @NonNull @Override protected Span getSpanElement(String replacement, String[] matchStrings)
    {
        return new UserClickableSpan(replacement, matchStrings);
    }

    protected class UserClickableSpan extends RichClickableSpan
    {
        //<editor-fold desc="Constructors">
        public UserClickableSpan(String replacement, String[] matchStrings)
        {
            super(replacement, matchStrings);
        }
        //</editor-fold>

        @Override public void onClick(View view)
        {
            int userId = Integer.parseInt(matchStrings[2]);
            if (userId != currentUserId.get())
            {
                userActionSubject.onNext(new UserTagProcessor.ProfileUserAction(matchStrings, new UserBaseKey(userId)));
            }
        }
    }

    public static class ProfileUserAction extends UserAction
    {
        @NonNull public final UserBaseKey userBaseKey;

        public ProfileUserAction(@NonNull String[] matchStrings, @NonNull UserBaseKey userBaseKey)
        {
            super(matchStrings);
            this.userBaseKey = userBaseKey;
        }
    }
}
