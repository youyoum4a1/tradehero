package com.androidth.general.api.live1b;


import java.util.Date;

public class BaseMessageResponseDTO {

    public String requestID;
    public Date requestCompletedAtUtc;
    public long errorCode;
    public String description;

    public BaseMessageResponseDTO(){}
}
