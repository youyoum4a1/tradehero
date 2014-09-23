package com.tradehero.th.widget;

import com.tradehero.th.fragments.onboarding.hero.SelectableUserViewRelative;
import dagger.Module;

@Module(
        injects = {
                ServerValidatedUsernameText.class,
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
