package com.ayondo.academy.api.auth;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ayondo.academy.auth.AuthData;
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

    @Override public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        String separator = "";
        for (Map.Entry<String, String> entry : tokenMap.entrySet())
        {
            sb.append(separator).append(entry.getKey()).append(':').append(entry.getValue());
            separator = ", ";
        }
        sb.append(']');
        return "AccessTokenForm{" +
                "tokenMap=" + sb +
                '}';
    }
}
