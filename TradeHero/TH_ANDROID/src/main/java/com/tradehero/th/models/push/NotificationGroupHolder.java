package com.ayondo.academy.models.push;

import com.ayondo.academy.api.notification.NotificationDTO;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NotificationGroupHolder extends HashMap<Integer, List<NotificationDTO>>
{
    @Inject public NotificationGroupHolder() { }
}
