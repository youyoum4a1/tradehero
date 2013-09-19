package com.tradehero.common.text;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.widget.TextView;
import java.util.LinkedList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/17/13 Time: 11:30 AM Copyright (c) TradeHero */
public class RichTextCreator
{
    private String originalText;
    private SpannableStringBuilder richText;
    private List<RichTextProcessor> processors;
    private boolean useBuiltInTextProcessors;

    //<editor-fold desc="Constructors">
    public RichTextCreator()
    {
        this(true);
    }

    public RichTextCreator(boolean useBuiltInTextProcessors)
    {
        this.useBuiltInTextProcessors = useBuiltInTextProcessors;

        if (useBuiltInTextProcessors)
        {
            RichTextProcessor[] builtInProcessors = getBuildInProcessors();
            for (RichTextProcessor processor: builtInProcessors)
            {
                register(processor);
            }
        }
    }
    //</editor-fold>

    private RichTextProcessor[] getBuildInProcessors()
    {
        return new RichTextProcessor[] {
                new UserTagProcessor(),
                new BoldTagProcessor(),
                new ItalicTagProcessor(),
                new SecurityTagProcessor(),
                new LinkTagProcessor(),
                new ColorTagProcessor()
        };
    }

    private void register(RichTextProcessor processor)
    {
        if (processor == null)
        {
            throw new IllegalArgumentException("processor cannot be null");
        }

        if (processors  == null)
        {
            processors = new LinkedList<>();
        }
        processors.add(processor);
    }

    public RichTextCreator load(CharSequence text)
    {
        return load(text.toString());
    }

    public RichTextCreator load(String text)
    {
        setText(text);
        return this;
    }

    private void setText(String text)
    {
        this.originalText = text;
        richText = new SpannableStringBuilder(text);
    }

    public Spanned create()
    {
        for (RichTextProcessor processor: processors)
        {
            richText = processor.process(richText);
        }

        return richText;
    }

    public void apply(TextView textView)
    {
        Spanned richText= create();
        textView.setText(richText);
    }
}
