package com.tradehero.th.models.sms.twilio;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.R;
import com.tradehero.th.models.sms.SMSId;
import com.tradehero.th.models.sms.SMSSentConfirmationDTO;
import java.util.Date;

public class TwilioSMSSentConfirmationDTO implements SMSSentConfirmationDTO
{
    @JsonProperty("account_sid") public TwilioAccountSid accountSid;
    @JsonProperty("api_version") public String apiVersion;
    @JsonProperty("body") public String body;
    @JsonProperty("num_segments") public int numSegments;
    @JsonProperty("num_media") public int numMedia;
    @JsonProperty("date_created") public Date dateCreated;
    @JsonProperty("date_sent") public Date dateSent;
    @JsonProperty("date_updated") public Date dateUpdated;
    @JsonProperty("direction") public String direction;
    @JsonProperty("error_code") public TwilioErrorCode errorCode;
    @JsonProperty("error_message") public String errorMessage;
    @JsonProperty("from") public String from;
    @JsonProperty("price") public String price;
    @JsonProperty("price_unit") public String priceUnit;
    @JsonProperty("sid") public TwilioSMSId sid;
    @JsonProperty("status") public TwilioSMSStatus status;
    @JsonProperty("to") public String to;
    @JsonProperty("uri") public String uri;

    public TwilioSMSSentConfirmationDTO()
    {
        super();
    }

    @NonNull @Override public SMSId getSMSId()
    {
        return sid;
    }

    @NonNull @Override public String getTo()
    {
        return to;
    }

    @NonNull @Override public String getMessageBody()
    {
        return body;
    }

    @Override public int getStatusStringRes()
    {
        if (status == null)
        {
            return R.string.na;
        }
        return status.textId;
    }

    @Override public boolean isFinalStatus()
    {
        return status != null && status.finalStatus;
    }
}
