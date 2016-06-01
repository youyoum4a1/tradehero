package com.tradehero.common.text;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.widget.TextView;
import com.ayondo.academy.api.users.CurrentUserId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;

public class RichTextCreator
{
    private String originalText;
    @NonNull private List<RichTextProcessor> processors;

    //<editor-fold desc="Constructors">
    public RichTextCreator(@NonNull CurrentUserId currentUserId)
    {
        this(getBuiltInProcessors(currentUserId));
    }

    public RichTextCreator(@NonNull RichTextProcessor[] processors)
    {
        this.processors = new LinkedList<>();
        Collections.addAll(this.processors, processors);
    }
    //</editor-fold>

    @NonNull private static RichTextProcessor[] getBuiltInProcessors(@NonNull CurrentUserId currentUserId)
    {
        return new RichTextProcessor[] {
                new UserTagProcessor(currentUserId),
                new BoldTagProcessor(),
                new ItalicTagProcessor(),
                new SecurityTagProcessor(),
                new LinkTagProcessor(currentUserId),
                new ColorTagProcessor(),
                new SpecialCharacterProcessor(),
                new BackTickTagProcessor(),
        };
    }

    @NonNull public RichTextCreator load(@NonNull CharSequence text)
    {
        return load(text.toString());
    }

    @NonNull public RichTextCreator load(@NonNull String text)
    {
        setText(text);
        return this;
    }

    private void setText(@NonNull String text)
    {
        this.originalText = text;
    }

    public void apply(@NonNull TextView textView)
    {
        textView.setText(create());
    }

    @NonNull public Spanned create()
    {
        SpannableStringBuilder richText = new SpannableStringBuilder(originalText);

        for (RichTextProcessor processor : processors)
        {
            richText = processor.process(richText);
        }

        return richText;
    }

    @NonNull public Observable<ClickableTagProcessor.UserAction> getUserActionObservable()
    {
        List<Observable<ClickableTagProcessor.UserAction>> observables = new ArrayList<>();
        for (RichTextProcessor processor : processors)
        {
            if (processor instanceof ClickableTagProcessor)
            {
                observables.add(((ClickableTagProcessor) processor).getUserActionSubject());
            }
        }
        return Observable.merge(observables);
    }
}
