package com.androidth.general.utils.metrics.events;

import com.androidth.general.api.security.SecurityId;
import com.androidth.general.utils.SecurityUtils;

import java.util.HashMap;
import java.util.Map;

class SecurityEvent //extends THAnalyticsEvent
{
    static final String SECURITY_SYMBOL_MAP_KEY = "symbol";

    private final SecurityId securityId;

    //<editor-fold desc="Constructors">
    public SecurityEvent(SecurityId securityId)
    {
        //super(name);
        this.securityId = securityId;
    }
    //</editor-fold>

    public Map<String, String> getAttributes()
    {
        Map<String, String> attributes = new HashMap<>();
        if (securityId != null)
        {
            attributes.put(SECURITY_SYMBOL_MAP_KEY, SecurityUtils.getDisplayableSecurityName(securityId));
        }
        return attributes;
    }
}