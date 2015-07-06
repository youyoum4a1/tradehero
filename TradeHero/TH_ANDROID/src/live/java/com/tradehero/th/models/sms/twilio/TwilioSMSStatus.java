package com.tradehero.th.models.sms.twilio;

import android.support.annotation.StringRes;
import com.tradehero.th.R;

/**
 * https://www.twilio.com/docs/api/rest/message#sms-status-values
 */
public enum TwilioSMSStatus
{
    /**
     * The API request to send an message was successful and the message is queued to be sent out.
     */
    queued(R.string.sms_verification_button_twilio_queued, false),

    /**
     * Twilio is in the process of dispatching your message to the nearest upstream carrier in the network.
     */
    sending(R.string.sms_verification_button_twilio_sending, false),

    /**
     * The message was successfully accepted by the nearest upstream carrier.
     */
    sent(R.string.sms_verification_button_twilio_sent, false),

    /**
     * The inbound message has been received by Twilio and is currently being processed.
     */
    receiving(R.string.sms_verification_button_twilio_receiving, false),

    /**
     * On inbound messages only. The message was received by one of your Twilio numbers.
     */
    received(R.string.sms_verification_button_twilio_received, false),

    /**
     * Twilio has received confirmation of message delivery from the upstream carrier, and,
     * where available, the destination handset.
     */
    delivered(R.string.sms_verification_button_twilio_delivered, true),

    /**
     * Twilio has received a delivery receipt indicating that the message was not delivered.
     * This can happen for a number of reasons including carrier content filtering, availability
     * of the destination handset, etc.
     */
    undelivered(R.string.sms_verification_button_twilio_undelivered, true),

    /**
     * The message could not be sent. This can happen for various reasons including queue overflows,
     * account suspensions and media errors (in the case of MMS).
     * Twilio does not charge you for failed messages.
     */
    failed(R.string.sms_verification_button_twilio_failed, true),
    ;

    @StringRes public final int textId;
    public final boolean finalStatus;

    TwilioSMSStatus(@StringRes int textId, boolean finalStatus)
    {
        this.textId = textId;
        this.finalStatus = finalStatus;
    }
}
