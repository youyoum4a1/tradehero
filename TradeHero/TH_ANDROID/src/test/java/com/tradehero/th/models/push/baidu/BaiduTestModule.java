package com.tradehero.th.models.push.baidu;

import dagger.Module;

@Module(
        injects = {
                BaiduPushMessageDTOTest.class,
                BaiduPushMessageReceiverTest.class
        },
        complete = false,
        library = true
)
public class BaiduTestModule
{
}
