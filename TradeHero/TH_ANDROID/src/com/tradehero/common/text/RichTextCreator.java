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

    //<editor-fold desc="Constructors">
    public RichTextCreator()
    {
    }

    public RichTextCreator(String text)
    {
        this(text, false);
    }

    public RichTextCreator(String text, boolean withBuiltInTextProcessors)
    {
        this.originalText = text;
        if (withBuiltInTextProcessors)
        {
            RichTextProcessor[] builtInProcessors = getBuildInProcessors();
            for (RichTextProcessor processor: builtInProcessors)
            {
                register(processor);
            }
        }
        richText = new SpannableStringBuilder(text);
    }

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
    //</editor-fold>

    public static RichTextCreator load(CharSequence text)
    {
        return new RichTextCreator(text.toString(), true);
    }

    public static RichTextCreator load(String text)
    {
        return new RichTextCreator(text, true);
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
        textView.setLinkTextColor(Color.BLUE);
        textView.setText(richText);
    }
}
