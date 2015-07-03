package com.tradehero.th.models.sms.twilio;

/**
 * https://www.twilio.com/docs/api/rest/message#sms-status-values
 */
public enum TwilioSMSStatus
{
    /**
     * The API request to send an message was successful and the message is queued to be sent out.
     */
    queued,

    /**
     * Twilio is in the process of dispatching your message to the nearest upstream carrier in the network.
     */
    sending,

    /**
     * The message was successfully accepted by the nearest upstream carrier.
     */
    sent,

    /**
     * The inbound message has been received by Twilio and is currently being processed.
     */
    receiving,

    /**
     * On inbound messages only. The message was received by one of your Twilio numbers.
     */
    received,

    /**
     * Twilio has received confirmation of message delivery from the upstream carrier, and,
     * where available, the destination handset.
     */
    delivered,

    /**
     * Twilio has received a delivery receipt indicating that the message was not delivered.
     * This can happen for a number of reasons including carrier content filtering, availability
     * of the destination handset, etc.
     */
    undelivered,

    /**
     * The message could not be sent. This can happen for various reasons including queue overflows,
     * account suspensions and media errors (in the case of MMS).
     * Twilio does not charge you for failed messages.
     */
    failed,
}
