package com.tradehero.th.api.discussion.form;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.discussion.MessageType;
import java.io.ByteArrayOutputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import retrofit.converter.Converter;
import retrofit.mime.TypedOutput;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class MessageCreateFormDTOTest
{
    @Inject Converter converter;
    @Inject MessageCreateFormDTOFactory messageCreateFormDTOFactory;

    @Before public void setUp()
    {
    }

    private String asString(TypedOutput typedOutput) throws Exception
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        typedOutput.writeTo(bytes);
        return new String(bytes.toByteArray());
    }

    @Test public void testSerialiseCorrectPrivate() throws Exception
    {
        PrivateMessageCreateFormDTO formDTO = new PrivateMessageCreateFormDTO();
        formDTO.message = "test";
        assertEquals("{\"message\":\"test\",\"senderUserId\":0,\"recipientUserId\":0,\"messageType\":1}", asString(converter.toBody(formDTO)));
    }

    @Test public void testSerialiseCorrecPrivateFromFactory() throws Exception
    {
        MessageCreateFormDTO formDTO = messageCreateFormDTOFactory.createEmpty(MessageType.PRIVATE);
        formDTO.message = "test";
        assertEquals("{\"message\":\"test\",\"senderUserId\":0,\"recipientUserId\":0,\"messageType\":1}", asString(converter.toBody(formDTO)));
    }

    @Test public void testSerialiseCorrectBroadcastFree() throws Exception
    {
        BroadcastFreeMessageCreateFormDTO formDTO = new BroadcastFreeMessageCreateFormDTO();
        formDTO.message = "test";
        assertEquals("{\"message\":\"test\",\"senderUserId\":0,\"messageType\":2}", asString(converter.toBody(formDTO)));
    }

    @Test public void testSerialiseCorrectBroadcastPaid() throws Exception
    {
        BroadcastPaidMessageCreateFormDTO formDTO = new BroadcastPaidMessageCreateFormDTO();
        formDTO.message = "test";
        assertEquals("{\"message\":\"test\",\"senderUserId\":0,\"messageType\":3}", asString(converter.toBody(formDTO)));
    }

    @Test public void testSerialiseCorrectBroadcastAll() throws Exception
    {
        BroadcastAllMessageCreateFormDTO formDTO = new BroadcastAllMessageCreateFormDTO();
        formDTO.message = "test";
        assertEquals("{\"message\":\"test\",\"senderUserId\":0,\"messageType\":4}", asString(converter.toBody(formDTO)));
    }

    @Test public void testSerialiseCorrectBroadcastAllFromFactory() throws Exception
    {
        MessageCreateFormDTO formDTO = messageCreateFormDTOFactory.createEmpty(MessageType.BROADCAST_ALL_FOLLOWERS);
        formDTO.message = "test";
        assertEquals("{\"message\":\"test\",\"senderUserId\":0,\"messageType\":4}", asString(converter.toBody(formDTO)));
    }
}
