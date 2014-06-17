package com.tradehero.th.fragments.updatecenter.notifications;

import android.view.LayoutInflater;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.service.NotificationServiceWrapper;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;
import retrofit.Callback;
import retrofit.client.Header;
import retrofit.client.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricMavenTestRunner.class)
public class NotificationsViewTest
{
    NotificationsView notifView;

    @Before public void setUp()
    {
        ActivityController<DashboardActivity> activityController = Robolectric.buildActivity(DashboardActivity.class).create().start();
        DashboardActivity activity = activityController.get();
        notifView = (NotificationsView) LayoutInflater.from(activity).inflate(R.layout.notifications_center, null).findViewById(R.id.notifications_list);
    }

    @Test public void testMarkAsReadNotCrash()
    {
        notifView.notificationServiceWrapper = mock(NotificationServiceWrapper.class);
        when(notifView.notificationServiceWrapper.markAsRead(any(NotificationKey.class), any(Callback.class)))
        .then(new Answer<BaseMiddleCallback<Response>>()
        {
            @Override public BaseMiddleCallback<Response> answer(InvocationOnMock invocation) throws Throwable
            {
                Object[] args = invocation.getArguments();
                Response successResponse = new Response("http://whatever", 200, "Good", new ArrayList<Header>(), null);
                ((Callback) args[1]).success(successResponse, null);
                return null;
            }
        });

        notifView.reportNotificationRead(1);
    }

}
