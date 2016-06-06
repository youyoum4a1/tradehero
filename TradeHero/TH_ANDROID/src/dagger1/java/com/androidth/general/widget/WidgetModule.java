package com.androidth.general.widget;

import com.androidth.general.widget.validation.WidgetValidationModule;
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
