package com.ayondo.academy.fragments.updatecenter.notifications;

import android.view.LayoutInflater;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.R;
import com.ayondo.academy.activities.DashboardActivity;
import com.ayondo.academy.network.service.NotificationServiceWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.mockito.Mockito.mock;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
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
        //noinspection unchecked
        // FIXME
        //when(notifView.notificationServiceWrapper.markAsRead(any(UserBaseKey.class), any(NotificationKey.class), any(Callback.class)))
        //.then(new Answer<BaseMiddleCallback<Response>>()
        //{
        //    @Override public BaseMiddleCallback<Response> answer(InvocationOnMock invocation) throws Throwable
        //    {
        //        Object[] args = invocation.getArguments();
        //        Response successResponse = new Response("http://whatever", 200, "Good", new ArrayList<Header>(), null);
        //        //noinspection unchecked
        //        ((Callback) args[2]).success(successResponse, null);
        //        return null;
        //    }
        //});

        notifView.reportNotificationRead(1);
    }

}
