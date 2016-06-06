package com.androidth.general.models.sms.twilio;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * https://www.twilio.com/docs/api/rest/message#error-values
 */
public enum TwilioErrorCode
{
    /**
     * Queue overflow
     * You tried to send too many messages too quickly and your message queue overflowed. Try sending your message again after waiting
     * some time.
     */
    C30001(30001),

    /**
     * Account suspended
     * Your account was suspended between the time of message send and delivery. Please contact Twilio.
     */
    C30002(30002),

    /**
     * Unreachable destination handset
     * The destination handset you are trying to reach is switched off or otherwise unavailable.
     */
    C30003(30003),

    /**
     * Message blocked
     * The destination number you are trying to reach is blocked from receiving this message (e.g. due to blacklisting).
     */
    C30004(30004),

    /**
     * Unknown destination handset
     * The destination number you are trying to reach is unknown and may no longer exist.
     */
    C30005(30005),

    /**
     * Landline or unreachable carrier
     * The destination number is unable to receive this message. Potential reasons could include trying to reach a
     * landline or, in the case of short codes, an unreachable carrier.
     */
    C30006(30006),

    /**
     * Carrier violation
     * Your message content was flagged as going against carrier guidelines.
     */
    C30007(30007),

    /**
     * Unknown error The error does not fit into any of the above categories.
     */
    C30008(30008),;

    public final int code;

    TwilioErrorCode(int code)
    {
        this.code = code;
    }

    @JsonCreator @Nullable public static TwilioErrorCode create(@Nullable Integer code)
    {
        if (code != null)
        {
            for (TwilioErrorCode errorCode : values())
            {
                if (code.equals(errorCode.code))
                {
                    return errorCode;
                }
            }
        }
        return null;
    }
}
