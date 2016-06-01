package com.ayondo.academy.widget;

import com.ayondo.academy.widget.validation.WidgetValidationModule;
import dagger.Module;

@Module(
        includes = {
                WidgetValidationModule.class,
        },
        injects = {
                MarkdownTextView.class,
                VotePair.class,
                XpToast.class,
        },
        library = true,
        complete = false
)
public class WidgetModule
{
}
