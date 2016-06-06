package com.androidth.general.common.text;

import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class ClickableTagProcessor extends RichSpanTextProcessor
{
    @NonNull protected final PublishSubject<UserAction> userActionSubject;

    //<editor-fold desc="Constructors">
    public ClickableTagProcessor()
    {
        userActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    abstract protected class RichClickableSpan extends ClickableSpan
            implements Span
    {
        private final String replacement;
        private final String originalText;
        protected final String[] matchStrings;

        //<editor-fold desc="Constructors">
        public RichClickableSpan(String replacement, String[] matchStrings)
        {
            this.replacement = replacement;
            this.matchStrings = matchStrings;
            this.originalText = matchStrings.length > 0 ? matchStrings[0] : null;
        }
        //</editor-fold>

        @Override public void updateDrawState(@NonNull TextPaint ds)
        {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        @Override public String getOriginalText()
        {
            return originalText;
        }
    }

    @NonNull public Observable<UserAction> getUserActionSubject()
    {
        return userActionSubject.asObservable();
    }

    public static class UserAction
    {
        @NonNull public final String[] matchStrings;

        public UserAction(@NonNull String[] matchStrings)
        {
            this.matchStrings = matchStrings;
        }
    }
}
