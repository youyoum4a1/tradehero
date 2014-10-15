package com.tradehero.th.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorMessageDTO
{
    @JsonProperty("Message")
    public String message;
}
