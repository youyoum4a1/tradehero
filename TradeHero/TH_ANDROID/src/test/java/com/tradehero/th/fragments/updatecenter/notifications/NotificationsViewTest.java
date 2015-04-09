package com.tradehero.th.fragments.updatecenter.notifications;

import android.view.LayoutInflater;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.network.service.NotificationServiceWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

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
