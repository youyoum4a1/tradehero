package com.tradehero.th.api.form;

import com.tradehero.th.api.users.UserAvailabilityDTO;
import com.tradehero.th.network.CallbackWithSpecificNotifiers;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.UserService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 8/28/13 Time: 4:24 PM Copyright (c) TradeHero */
public abstract class UserAvailabilityRequester extends CallbackWithSpecificNotifiers<UserAvailabilityDTO>
{
    private String displayName;
    private boolean available;
    private boolean queried = false;
    private boolean received = false;

    @Inject UserService userService;

    public UserAvailabilityRequester(String displayName)
    {
        this.displayName = displayName;
    }

    public UserAvailabilityRequester()
    {
    }

    public boolean isAvailable()
    {
        return available;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public boolean isReceived()
    {
        return received;
    }

    public boolean needsToAskAgain()
    {
        // we want to check again with the server if the name was available as it could have been taken in the mean time.
        return !queried || (received && available);
    }

    public void askServerIfNeeded()
    {
        if (needsToAskAgain())
        {
            notifyIsQuerying(true);
            userService.checkDisplayNameAvailable(displayName, this);
            queried = true;
        }
    }

    @Override public void success(UserAvailabilityDTO userAvailabilityDTO, Response response)
    {
        super.success(userAvailabilityDTO, response);
        queried = true;
        received = true;
        available = userAvailabilityDTO.available;
        notifyAvailabilityChanged();
    }

    @Override public void failure(RetrofitError error)
    {
        super.failure(error);
        queried = false;
        received = false;
    }

    public abstract void notifyAvailabilityChanged();
}
