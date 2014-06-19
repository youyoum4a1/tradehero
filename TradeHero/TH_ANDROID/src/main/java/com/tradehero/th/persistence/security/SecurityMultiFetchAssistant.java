package com.tradehero.th.persistence.security;

import com.tradehero.common.persistence.BasicFetchAssistant;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.security.SecurityIntegerIdList;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SecurityMultiFetchAssistant extends BasicFetchAssistant<SecurityIntegerId, SecurityCompactDTO>
{
    private final SecurityCompactCache securityCompactCache;
    private final SecurityServiceWrapper securityServiceWrapper;

    //<editor-fold desc="Constructors">
    public SecurityMultiFetchAssistant(
            @NotNull SecurityCompactCache securityCompactCache,
            @NotNull SecurityServiceWrapper securityServiceWrapper,
            List<SecurityIntegerId> keysToFetch)
    {
        super(keysToFetch);
        this.securityCompactCache = securityCompactCache;
        this.securityServiceWrapper = securityServiceWrapper;
    }
    //</editor-fold>

    public void execute(boolean force)
    {
        boolean ready = true;
        SecurityIntegerIdList integerIds = new SecurityIntegerIdList();
        SecurityCompactDTO cached;
        for (SecurityIntegerId key: new ArrayList<>(fetched.keySet())) // Make a new list to avoid changes
        {
            cached = securityCompactCache.get(key);
            if (force || cached == null)
            {
                ready = false;
                integerIds.add(key);
            }
            else
            {
                fetched.put(key, cached);
            }
        }
        if (!integerIds.isEmpty())
        {
            securityServiceWrapper.getMultipleSecurities(integerIds, createMultiFetchCallback());
        }
        if (ready)
        {
            notifyListener();
        }
    }

    protected void populate(@Nullable Map<Integer, SecurityCompactDTO> values)
    {
        if (values != null)
        {
            for (@Nullable SecurityCompactDTO securityCompactDTO: values.values())
            {
                if (securityCompactDTO != null)
                {
                    fetched.put(securityCompactDTO.getSecurityIntegerId(), securityCompactDTO);
                }
            }
        }
        notifyListener();
    }

    protected Callback<Map<Integer, SecurityCompactDTO>> createMultiFetchCallback()
    {
        return new SecurityMultiFetchAssistantCallback();
    }

    protected class SecurityMultiFetchAssistantCallback implements Callback<Map<Integer, SecurityCompactDTO>>
    {
        @Override public void success(Map<Integer, SecurityCompactDTO> integerSecurityCompactDTOMap, Response response)
        {
            populate(integerSecurityCompactDTOMap);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            notifyListener();
        }
    }
}
