package com.tradehero.th.utils.metrics.events;

import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import java.util.Map;

public final class SharingOptionsEvent extends SecurityEvent
{
    private static final String HAS_COMMENT_MAP_KEY = "hasComment";
    private static final String PRICE_SELECT_METHOD = "lastSelectBy";

    private static final String SHARE_TO_FACEBOOK_MAP_KEY = "shareToFacebook";
    private static final String SHARE_TO_LINKEDIN_MAP_KEY = "shareToLinkedIn";
    private static final String SHARE_TO_TWITTER_MAP_KEY = "shareToTwitter";
    private static final String SHARE_TO_WECHAT_MAP_KEY = "shareToWeChat";
    private static final String SHARE_TO_WEIBO_MAP_KEY = "shareToWeibo";

    private final ProviderId providerId;
    private final String priceSelectMethod;
    private final boolean hasComment;
    private final boolean facebookEnabled;
    private final boolean twitterEnabled;
    private final boolean linkedInEnabled;
    private final boolean weChatEnabled;
    private final boolean weiboEnabled;

    private SharingOptionsEvent(boolean isBuyEvent, SecurityId securityId, ProviderId providerId, String priceSelectMethod, boolean hasComment,
            boolean facebookEnabled,
            boolean twitterEnabled,
            boolean linkedInEnabled,
            boolean weChatEnabled,
            boolean weiboEnabled)
    {
        super(isBuyEvent ? AnalyticsConstants.Trade_Buy : AnalyticsConstants.Trade_Sell, securityId);
        this.providerId = providerId;
        this.priceSelectMethod = priceSelectMethod;
        this.hasComment = hasComment;
        this.facebookEnabled = facebookEnabled;
        this.twitterEnabled = twitterEnabled;
        this.linkedInEnabled = linkedInEnabled;
        this.weChatEnabled = weChatEnabled;
        this.weiboEnabled = weiboEnabled;
    }

    @Override public Map<String, String> getAttributes()
    {
        Map<String, String> attributes = super.getAttributes();
        attributes.put(HAS_COMMENT_MAP_KEY, hasComment ? "1" : "0");
        attributes.put(PRICE_SELECT_METHOD, priceSelectMethod);

        attributes.put(SHARE_TO_FACEBOOK_MAP_KEY, facebookEnabled ? "1" : "0");
        attributes.put(SHARE_TO_TWITTER_MAP_KEY, twitterEnabled ? "1" : "0");
        attributes.put(SHARE_TO_LINKEDIN_MAP_KEY, linkedInEnabled ? "1" : "0");
        attributes.put(SHARE_TO_WECHAT_MAP_KEY, weChatEnabled ? "1" : "0");
        attributes.put(SHARE_TO_WEIBO_MAP_KEY, weiboEnabled ? "1" : "0");

        attributes.put(ProviderEvent.PROVIDER_ID_MAP_KEY, providerId.toString());
        return attributes;
    }

    public static class Builder
    {
        private ProviderId providerId;
        private SecurityId securityId;
        private boolean isBuyEvent;
        private boolean hasComment;
        private boolean facebookEnabled;
        private boolean twitterEnabled;
        private boolean linkedInEnabled;
        private boolean weChatEnabled;
        private boolean weiboEnabled;
        private String priceSelectionMethod;

        public Builder setBuyEvent(boolean isBuyEvent)
        {
            this.isBuyEvent = isBuyEvent;
            return this;
        }

        public Builder setSecurityId(SecurityId securityId)
        {
            this.securityId = securityId;
            return this;
        }

        public Builder setProviderId(ProviderId providerId)
        {
            this.providerId = providerId;
            return this;
        }

        public Builder setPriceSelectionMethod(String priceSelectionMethod)
        {
            this.priceSelectionMethod = priceSelectionMethod;
            return this;
        }

        public Builder hasComment(boolean hasComment)
        {
            this.hasComment = hasComment;
            return this;
        }

        public Builder facebookEnabled(boolean enabled)
        {
            this.facebookEnabled = enabled;
            return this;
        }

        public Builder twitterEnabled(boolean enabled)
        {
            this.twitterEnabled = enabled;
            return this;
        }

        public Builder linkedInEnabled(boolean enabled)
        {
            this.linkedInEnabled = enabled;
            return this;
        }

        public Builder wechatEnabled(boolean enabled)
        {
            this.weChatEnabled = enabled;
            return this;
        }

        public Builder weiboEnabled(boolean enabled)
        {
            this.weiboEnabled = enabled;
            return this;
        }

        public SharingOptionsEvent build()
        {
            return new SharingOptionsEvent(isBuyEvent, securityId, providerId, priceSelectionMethod, hasComment, facebookEnabled, twitterEnabled,
                    linkedInEnabled,
                    weChatEnabled,weiboEnabled);
        }
    }
}
