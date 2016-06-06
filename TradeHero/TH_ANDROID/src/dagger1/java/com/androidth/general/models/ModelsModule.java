package com.androidth.general.models;

import com.androidth.general.models.discussion.RunnableInvalidateMessageList;
import com.androidth.general.models.notification.RunnableInvalidateNotificationList;
import dagger.Module;

@Module(
        includes = {
                GameLiveModelsModule.class,
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
