package com.tradehero.th.widget;

import dagger.Module;

@Module(
        injects = {
                ServerValidatedUsernameText.class,
                MarkdownTextView.class,
                VotePair.class,
        },
        library = true,
        complete = false
)
public class WidgetModule
{
}
