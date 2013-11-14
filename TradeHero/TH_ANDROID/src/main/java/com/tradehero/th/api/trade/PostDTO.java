package com.tradehero.th.api.trade;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.Date;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:17 PM To change this template use File | Settings | File Templates. */
public class PostDTO
{
    public static final String TAG = PostDTO.class.getSimpleName();

    public int id;
    public String text;
    public Date date_time;

    public UserProfileCompactDTO user;
    public TradeDTO trade;
    public SecurityCompactDTO security;
}
