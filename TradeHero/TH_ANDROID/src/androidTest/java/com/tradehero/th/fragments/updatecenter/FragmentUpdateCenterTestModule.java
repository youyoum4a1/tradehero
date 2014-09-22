package com.tradehero.th.fragments.updatecenter;

import com.tradehero.th.fragments.updatecenter.messages.FragmentUpdateCenterMessagesTestModule;
import dagger.Module;

@Module(
        includes = {
                FragmentUpdateCenterMessagesTestModule.class,
        },
        injects = {
        },
        complete = false,
        library = true
)
public class FragmentUpdateCenterTestModule
{
}
