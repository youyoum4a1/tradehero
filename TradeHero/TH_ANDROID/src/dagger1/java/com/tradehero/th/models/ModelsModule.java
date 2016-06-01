package com.ayondo.academy.models;

import com.ayondo.academy.models.discussion.RunnableInvalidateMessageList;
import com.ayondo.academy.models.notification.RunnableInvalidateNotificationList;
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
