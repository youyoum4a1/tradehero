package com.tradehero.th.models.push;

import com.tradehero.th.models.push.baidu.baidu.BaiduTestModule;
import dagger.Module;

@Module(
        includes = {
                BaiduTestModule.class
        },
        complete = false,
        library = true,
        overrides = true
)
public class FlavorPushTestModule
{
}
