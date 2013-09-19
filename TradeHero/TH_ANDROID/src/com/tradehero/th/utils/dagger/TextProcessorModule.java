package com.tradehero.th.utils.dagger;

import com.tradehero.common.text.RichTextCreator;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 9/17/13 Time: 3:38 PM Copyright (c) TradeHero */
@Module(
        injects = MarkdownTextView.class
)
public class TextProcessorModule
{
    @Provides RichTextCreator provideMarkdownParser()
    {
        return new RichTextCreator();
    }
}
