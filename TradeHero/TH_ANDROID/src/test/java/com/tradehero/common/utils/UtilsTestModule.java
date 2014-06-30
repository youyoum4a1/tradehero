package com.tradehero.common.utils;

import com.tradehero.th.utils.THRouterTest;
import dagger.Module;

@Module(
        injects = {
                ConverterDeserialisationTest.class,
                ConverterSerialisationTest.class,
                RetrofitHelperTest.class,
                THRouterTest.class
        },
        complete = false,
        library = true
)
public class UtilsTestModule
{
}
