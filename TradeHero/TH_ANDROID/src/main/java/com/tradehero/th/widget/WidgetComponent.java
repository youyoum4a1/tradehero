package com.tradehero.th.widget;

import com.tradehero.th.fragments.onboarding.hero.SelectableUserViewRelative;
import dagger.Component;

@Component
public interface WidgetComponent
{
    void injectServerValidatedUsernameText(ServerValidatedUsernameText target);
    void injectMarkdownTextView(MarkdownTextView target);
    void injectVotePair(VotePair target);
    void injectXpToast(XpToast target);
    void injectSelectableUserViewRelative(SelectableUserViewRelative target);
}
