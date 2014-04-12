package com.tradehero.th.api.social;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.social.key.HeroId;
import java.util.ArrayList;

/** Created with IntelliJ IDEA. User: xavier Date: 11/20/13 Time: 7:43 PM To change this template use File | Settings | File Templates. */
public class HeroIdList extends ArrayList<HeroId> implements DTO
{
    public static final String TAG = HeroIdList.class.getSimpleName();
}
