package com.tradehero.th.fragments.updatecenter.messages;

import android.view.LayoutInflater;
import android.view.View;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import java.io.IOException;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class MessageItemViewTest
{
    @Inject ObjectMapper objectMapper;
    @Inject MessageHeaderCache messageHeaderCache;
    private MessageItemView messageItemView;
    private MessageHeaderDTO messageHeaderDTO;

    @Before public void setUp() throws IOException
    {
        ActivityController<DashboardActivity> activityController = Robolectric.buildActivity(DashboardActivity.class).create().start();
        DashboardActivity activity = activityController.get();
        messageItemView = (MessageItemView) LayoutInflater.from(activity).inflate(R.layout.message_list_item, null);

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
        messageHeaderCache.put(messageHeaderDTO.getDTOKey(), messageHeaderDTO);
        messageItemView.display(messageHeaderDTO.getDTOKey());
        assertThat(messageItemView.mUnreadFlag.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test public void testReadHidesFlag()
    {
        messageHeaderDTO.unread = false;
        messageHeaderCache.put(messageHeaderDTO.getDTOKey(), messageHeaderDTO);
        messageItemView.display(messageHeaderDTO.getDTOKey());
        assertThat(messageItemView.mUnreadFlag.getVisibility()).isEqualTo(View.GONE);
    }
}
