package com.tradehero.th.auth;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;

public final class AccessTokenForm
{
    @JsonIgnore
    private final Map<String, String> tokenMap;

    @JsonAnyGetter
    public Map<String, String> getTokenMap()
    {
        return tokenMap;
    }

    public AccessTokenForm(AuthData authData)
    {
        this.tokenMap = authData.getTokenMap();
    }
}
