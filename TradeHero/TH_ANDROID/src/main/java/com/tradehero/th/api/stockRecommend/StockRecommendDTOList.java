package com.tradehero.th.api.stockRecommend;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.users.UserBaseDTO;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class StockRecommendDTOList {
    private List<UserBaseDTO> users;
    private List<SecurityCompactDTO> securities;
    private List<TimelineItemDTO> enhancedItems;
    private List<TradeDTO> trades;

    public UserBaseDTO getUserById(int userId) {
        for (UserBaseDTO user: users) {
            if (user != null && user.id == userId) {
                return user;
            }
        }
        return null;
    }

    public List<TimelineItemDTO> getEnhancedItems() {
        if (enhancedItems == null) {
            return null;
        }
        return Collections.unmodifiableList(enhancedItems);
    }

    public int getSize() {
        if (enhancedItems == null) {
            return 0;
        }
        return enhancedItems.size();
    }

    public TimelineItemDTO getItem(int position) {
        if (enhancedItems == null) {
            return null;
        }

        return enhancedItems.get(position);
    }
}
