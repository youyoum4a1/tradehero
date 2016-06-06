package com.androidth.general.models.push;

import com.androidth.general.api.notification.NotificationDTO;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NotificationGroupHolder extends HashMap<Integer, List<NotificationDTO>>
{
    @Inject public NotificationGroupHolder() { }
}
