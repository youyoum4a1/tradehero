package com.tradehero.th.utils.metrics.events;

import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.utils.SecurityUtils;
import java.util.Map;

class SecurityEvent extends AnalyticsEvent
{
    static final String SECURITY_SYMBOL_MAP_KEY = "symbol";

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
            attributes.put(SECURITY_SYMBOL_MAP_KEY, SecurityUtils.getDisplayableSecurityName(securityId));
        }
        return attributes;
    }
}
