package com.androidth.general.api.live1b;



public class BaseMessageResponseDTO {

    public String RequestId;
    public String RequestCompletedAtUtcTicks;
    public long ErrorCode;
    public String Description;

    public BaseMessageResponseDTO(){}

    @Override
    public String toString() {
        return "BaseMessageResponseDTO{" +
                "RequestID='" + RequestId + '\'' +
                ", RequestCompletedAtUtc=" + RequestCompletedAtUtcTicks +
                ", ErrorCode=" + ErrorCode +
                ", Description='" + Description + '\'' +
                '}';
    }
}
