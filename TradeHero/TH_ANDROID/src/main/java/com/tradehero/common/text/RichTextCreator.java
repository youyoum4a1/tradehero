package com.tradehero.common.text;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.widget.TextView;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class RichTextCreator
{
    private static final Map<String, Spanned> cachedTexts = new HashMap<>();
    private String originalText;
    private SpannableStringBuilder richText;
    private List<RichTextProcessor> processors;
    private final boolean useBuiltInTextProcessors;

    //<editor-fold desc="Constructors">
    @Inject public RichTextCreator()
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
                new ColorTagProcessor(),
                new SpecialCharacterProcessor()
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
        if (!needProcess())
        {
            return richText;
        }

        String cachedKey = getCachedText();
        Spanned cachedText = cachedTexts.get(cachedKey);

        if (cachedText != null)
        {
            return cachedText;
        }

        for (RichTextProcessor processor: processors)
        {
            richText = processor.process(richText);
        }

        cachedTexts.put(cachedKey, richText);
        return richText;
    }

    private boolean needProcess()
    {
        return processors != null && !processors.isEmpty();
    }

    private String getCachedText()
    {
        StringBuilder cachedKey = new StringBuilder();
        cachedKey.append(richText);
        for (RichTextProcessor processor: processors)
        {
            cachedKey.append('_').append(processor.key());
        }
        return cachedKey.toString();
    }

    public void apply(TextView textView)
    {
        Spanned richText = create();
        textView.setText(richText);
    }
}
