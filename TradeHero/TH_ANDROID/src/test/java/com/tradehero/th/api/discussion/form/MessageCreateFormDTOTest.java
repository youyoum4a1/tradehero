package com.tradehero.th.api.discussion.form;

import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.discussion.MessageType;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MessageCreateFormDTOTest
{
    @Test public void testSerialiseCorrectPrivate() throws IOException
    {
        PrivateMessageCreateFormDTO formDTO = new PrivateMessageCreateFormDTO();
        formDTO.message = "test";
        assertEquals("{\"message\":\"test\",\"messageType\":1}", THJsonAdapter.getInstance().toStringBody(formDTO));
    }

    @Test public void testSerialiseCorrecPrivateFromFactory() throws IOException
    {
        MessageCreateFormDTO formDTO = new MessageCreateFormDTOFactory().createEmpty(MessageType.PRIVATE);
        formDTO.message = "test";
        assertEquals("{\"message\":\"test\",\"messageType\":1}", THJsonAdapter.getInstance().toStringBody(formDTO));
    }

    @Test public void testSerialiseCorrectBroadcastFree() throws IOException
    {
        BroadcastFreeMessageCreateFormDTO formDTO = new BroadcastFreeMessageCreateFormDTO();
        formDTO.message = "test";
        assertEquals("{\"message\":\"test\",\"messageType\":2}", THJsonAdapter.getInstance().toStringBody(formDTO));
    }

    @Test public void testSerialiseCorrectBroadcastPaid() throws IOException
    {
        BroadcastPaidMessageCreateFormDTO formDTO = new BroadcastPaidMessageCreateFormDTO();
        formDTO.message = "test";
        assertEquals("{\"message\":\"test\",\"messageType\":3}", THJsonAdapter.getInstance().toStringBody(formDTO));
    }

    @Test public void testSerialiseCorrectBroadcastAll() throws IOException
    {
        BroadcastAllMessageCreateFormDTO formDTO = new BroadcastAllMessageCreateFormDTO();
        formDTO.message = "test";
        assertEquals("{\"message\":\"test\",\"messageType\":4}", THJsonAdapter.getInstance().toStringBody(formDTO));
    }

    @Test public void testSerialiseCorrectBroadcastAllFromFactory() throws IOException
    {
        MessageCreateFormDTO formDTO = new MessageCreateFormDTOFactory().createEmpty(MessageType.BROADCAST_ALL_FOLLOWERS);
        formDTO.message = "test";
        assertEquals("{\"message\":\"test\",\"messageType\":4}", THJsonAdapter.getInstance().toStringBody(formDTO));
    }
}
