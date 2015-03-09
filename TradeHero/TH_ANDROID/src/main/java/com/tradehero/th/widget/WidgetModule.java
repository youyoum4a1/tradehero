package com.tradehero.th.widget;

import com.tradehero.th.fragments.onboarding.hero.SelectableUserViewRelative;
import com.tradehero.th.widget.validation.WidgetValidationModule;
import dagger.Module;

@Module(
        includes = {
                WidgetValidationModule.class,
        },
        injects = {
                MarkdownTextView.class,
                VotePair.class,
                XpToast.class,
                SelectableUserViewRelative.class
        },
        library = true,
        complete = false
)
public class WidgetModule
{
}
