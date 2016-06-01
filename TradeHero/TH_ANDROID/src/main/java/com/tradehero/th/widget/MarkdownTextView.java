package com.ayondo.academy.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;
import com.tradehero.common.text.ClickableTagProcessor;
import com.tradehero.common.text.RichTextCreator;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.inject.HierarchyInjector;
import javax.inject.Inject;
import rx.Observable;

public class MarkdownTextView extends TextView
{
    @Inject CurrentUserId currentUserId;
    RichTextCreator parser;

    //<editor-fold desc="Constructors">
    public MarkdownTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(context, this);
        parser = new RichTextCreator(currentUserId);
        if(isInEditMode())
        {
            setText(getText(), BufferType.SPANNABLE);
        }
    }
    //</editor-fold>

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
        //noinspection ConstantConditions
        if (parser != null && text != null)
        {
            text = parser.load(text.toString().trim()).create();
        }
        super.setText(text, BufferType.SPANNABLE);
    }

    @NonNull public Observable<ClickableTagProcessor.UserAction> getUserActionObservable()
    {
        return parser.getUserActionObservable();
    }
}
