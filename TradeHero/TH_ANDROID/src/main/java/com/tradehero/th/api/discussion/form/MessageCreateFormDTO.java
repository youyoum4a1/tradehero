package com.ayondo.academy.api.discussion.form;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ayondo.academy.api.discussion.MessageType;

abstract public class MessageCreateFormDTO
{
    public String message;
    public int senderUserId;

    //<editor-fold desc="Constructors">
    public MessageCreateFormDTO()
    {
        super();
    }

    public MessageCreateFormDTO(String message)
    {
        this.message = message;
    }
    //</editor-fold>

    @JsonProperty @NonNull
    abstract public MessageType getMessageType();
}
