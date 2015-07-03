package com.tradehero.th.models.sms.twilio;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.models.sms.SMSSentConfirmationDTO;
import java.util.Date;

public class TwilioSMSSentConfirmationDTO implements SMSSentConfirmationDTO
{
    public final TwilioAccountSid accountSid;
    public final String apiVersion;
    public final String body;
    public final int numSegments;
    public final int numMedia;
    public final Date dateCreated;
    public final Date dateSent;
    public final Date dateUpdated;
    public final String direction;
    public final TwilioErrorCode errorCode;
    public final String errorMessage;
    public final String from;
    public final String price;
    public final String priceUnit;
    public final TwilioSMSId sid;
    public final TwilioSMSStatus status;
    public final String to;
    public final String uri;

    public TwilioSMSSentConfirmationDTO(
            @JsonProperty("account_sid") TwilioAccountSid accountSid,
            @JsonProperty("api_version") String apiVersion,
            @JsonProperty("body") String body,
            @JsonProperty("num_segments") int numSegments,
            @JsonProperty("num_media") int numMedia,
            @JsonProperty("date_created") Date dateCreated,
            @JsonProperty("date_sent") Date dateSent,
            @JsonProperty("date_updated") Date dateUpdated,
            @JsonProperty("direction") String direction,
            @JsonProperty("error_code") TwilioErrorCode errorCode,
            @JsonProperty("error_message") String errorMessage,
            @JsonProperty("from") String from,
            @JsonProperty("price") String price,
            @JsonProperty("price_unit") String priceUnit,
            @JsonProperty("sid") TwilioSMSId sid,
            @JsonProperty("status") TwilioSMSStatus status,
            @JsonProperty("to") String to,
            @JsonProperty("uri") String uri)
    {
        this.accountSid = accountSid;
        this.apiVersion = apiVersion;
        this.body = body;
        this.numSegments = numSegments;
        this.numMedia = numMedia;
        this.dateCreated = dateCreated;
        this.dateSent = dateSent;
        this.dateUpdated = dateUpdated;
        this.direction = direction;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.from = from;
        this.price = price;
        this.priceUnit = priceUnit;
        this.sid = sid;
        this.status = status;
        this.to = to;
        this.uri = uri;
    }

    @NonNull @Override public String getTo()
    {
        return to;
    }

    @NonNull @Override public String getMessageBody()
    {
        return body;
    }
}
