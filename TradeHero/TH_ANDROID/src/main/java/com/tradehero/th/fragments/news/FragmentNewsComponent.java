package com.tradehero.th.fragments.news;

import dagger.Component;

@Component
public interface FragmentNewsComponent
{
    void injectNewsHeadlineFragment(NewsHeadlineFragment target);
    void injectShareDialogLayout(ShareDialogLayout target);
    void injectNewsDialogLayout(NewsDialogLayout target);
    void injectNewsHeadlineViewLinear(NewsHeadlineViewLinear target);
    void injectNewsViewLinear(NewsViewLinear target);
    void injectNewsItemCompactViewHolder(NewsItemCompactViewHolder target);
    void injectNewsItemViewHolder(NewsItemViewHolder target);
}
