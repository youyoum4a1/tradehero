package com.tradehero.th.api.trade;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.Date;

public class PostDTO
{
    public int id;
    public String text;
    public Date date_time;

    public UserProfileCompactDTO user;
    public TradeDTO trade;
    public SecurityCompactDTO security;
}
