package com.tradehero.th.api;

import com.tradehero.th.api.security.SecurityCompactDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 9/6/13 Time: 11:42 AM To change this template use File | Settings | File Templates. */
public interface DTOView<DTOType>
{
    void display (DTOType trend);
}
