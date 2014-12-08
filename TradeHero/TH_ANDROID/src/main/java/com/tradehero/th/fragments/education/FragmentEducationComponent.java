package com.tradehero.th.fragments.education;

import dagger.Component;

@Component
public interface FragmentEducationComponent
{
    void injectVideoCategoriesFragment(VideoCategoriesFragment target);
    void injectVideoCategoryView(VideoCategoryView target);
    void injectVideoView(VideoView target);
}
