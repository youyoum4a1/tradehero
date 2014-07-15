package com.tradehero.th.utils.metrics.events;

import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import java.util.Map;

class SecurityEvent extends AnalyticsEvent
{
    private static final String SECURITY_ID_FORMAT = "%s:%s";

    private final SecurityId securityId;

    public SecurityEvent(String name, SecurityId securityId)
    {
        super(name);
        this.securityId = securityId;
    }

    @Override public Map<String, String> getAttributes()
    {
        Map<String, String> attributes = super.getAttributes();
        if (securityId != null)
        {
            attributes.put(AnalyticsConstants.SECURITY_SYMBOL_MAP_KEY, String.format(SECURITY_ID_FORMAT, securityId.getExchange(),
                    securityId.getSecuritySymbol()));
        }
        return attributes;
    }
}
