package com.tradehero.th.api.security;

import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.timeline.PublishableFormDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 10:09 AM To change this template use File | Settings | File Templates. */
public class TransactionFormDTO extends PublishableFormDTO
{
    public String signedQuoteDto;
    public int quantity;
    public int portfolio;
}
