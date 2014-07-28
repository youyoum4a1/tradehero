package com.tradehero.th.fragments.trending;

import dagger.Module;

@Module(
        injects = {
                AbstractTransactionDialogFragmentTest.class,
                BuyDialogFragmentTest.class,
        },
        complete = false,
        library = true
)
public class FragmentTrendingTestModule
{
}
