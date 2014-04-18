package com.tradehero.th.api.social;

import com.tradehero.common.persistence.DTO;
import javax.xml.stream.events.DTD;

/** Created with IntelliJ IDEA. User: xavier Date: 10/1/13 Time: 12:29 PM To change this template use File | Settings | File Templates. */
public class HeroIdExtWrapper implements DTO
{
    public HeroIdList heroIdList;

    /**count of heros that I've paid money to follow them currently  */
    public int herosCountGetPaid;
    /**count of heros that I've not paid money  follow them currently  */
    public int herosCountNotGetPaid;


}
