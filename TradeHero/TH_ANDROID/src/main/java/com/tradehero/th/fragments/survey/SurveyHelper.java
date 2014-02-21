package com.tradehero.th.fragments.survey;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/6/14 Time: 5:33 PM Copyright (c) TradeHero
 */
public class SurveyHelper
{
    @Inject Lazy<UserProfileCache> userProfileCache;

    @Inject SurveyHelper(CurrentUserId currentUserId)
    {
        currentUserId.toUserBaseKey();
    }

    public List<Object> injectItems(List<SecurityCompactDTO> showItems)
    {
        List<Object> newItems = Collections.emptyList();
        newItems.addAll(showItems);
        newItems.add(0, new Object());
        return newItems;
    }
}
