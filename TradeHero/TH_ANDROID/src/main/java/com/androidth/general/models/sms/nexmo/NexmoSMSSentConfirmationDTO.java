package com.androidth.general.models.sms.nexmo;

import android.support.annotation.NonNull;

import com.androidth.general.models.sms.SMSId;
import com.androidth.general.models.sms.SMSSentConfirmationDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class NexmoSMSSentConfirmationDTO implements SMSSentConfirmationDTO
{
    @JsonProperty("message-count") public Integer messageCount;
    @JsonProperty("messages") public ArrayList<HashMap<String, String>> messages;

    public NexmoSMSSentConfirmationDTO()
    {
        super();
    }

    @NonNull @Override public SMSId getSMSId()
    {
        return null;
    }

    @NonNull @Override public String getMessageId()
    {
        String messageId = "";
        if(messages!=null && messages.size()>0 && messages.get(0).containsKey("message-id")){
            messageId = messages.get(0).get("message-id");
        }
        return messageId;
    }

    @NonNull @Override public String getTo()
    {
        String to = null;
        if(messages!=null && messages.size()>0 && messages.get(0).containsKey("to")){
            to = messages.get(0).get("to");
        }
        return to;
    }

    @NonNull @Override public String getMessageBody()
    {
        return "";
    }

    @Override public int getStatusStringRes()
    {
        int status = -1;
        if(messages!=null && messages.size()>0 && messages.get(0).containsKey("status")){
            status = Integer.parseInt(messages.get(0).get("status"));
        }
        return status;
    }

    @Override public boolean isFinalStatus()
    {
        return messages != null && messageCount!=null && messageCount>0 && messages.size()>0;
//        return true;
    }

    @Override
    public boolean isSuccessful() {
//        return status!=null && status.success;
        return true;
    }
}
