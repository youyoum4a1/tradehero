package com.tradehero.th.fragments.updatecenter.messages;

import android.view.LayoutInflater;
import android.view.View;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.persistence.message.MessageHeaderCacheRx;
import java.io.IOException;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

@RunWith(THRobolectricTestRunner.class)
public class MessageItemViewTest
{
    @Inject @ForApp ObjectMapper objectMapper;
    @Inject MessageHeaderCacheRx messageHeaderCache;
    private View messageItemView;
    private MessageHeaderDTO messageHeaderDTO;

    @Before public void setUp() throws IOException
    {
        ActivityController<DashboardActivityExtended> activityController = Robolectric.buildActivity(DashboardActivityExtended.class).create().start();
        DashboardActivity activity = activityController.get();
        messageItemView = LayoutInflater.from(activity).inflate(R.layout.message_center_listview_item, null);

        messageHeaderDTO = objectMapper.readValue(
                getClass().getResourceAsStream("/com/tradehero/th/api/discussion" + "/MessageHeaderDTOUnreadBody1.json"),
                MessageHeaderDTO.class);
    }

    @After public void tearDown()
    {
        messageHeaderCache.invalidateAll();
    }

    @Test public void testUnreadShowsFlag()
    {
        messageHeaderCache.onNext(messageHeaderDTO.getDTOKey(), messageHeaderDTO);
        messageItemView.display(messageHeaderDTO);
        assertThat(messageItemView.mUnreadFlag.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test public void testReadHidesFlag()
    {
        messageHeaderDTO.unread = false;
        messageHeaderCache.onNext(messageHeaderDTO.getDTOKey(), messageHeaderDTO);
        messageItemView.display(messageHeaderDTO);
        assertThat(messageItemView.mUnreadFlag.getVisibility()).isEqualTo(View.GONE);
    }
}
