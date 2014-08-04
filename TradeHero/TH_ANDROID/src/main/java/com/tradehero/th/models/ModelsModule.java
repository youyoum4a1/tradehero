package com.tradehero.th.models;

import com.tradehero.th.models.discussion.RunnableInvalidateMessageList;
import com.tradehero.th.models.notification.RunnableInvalidateNotificationList;
import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                RunnableInvalidateNotificationList.class,
                RunnableInvalidateMessageList.class,
        },
        complete = false,
        library = true
)public class ModelsModule
{
}
