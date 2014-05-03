package com.tradehero.th.api.form;

import com.tradehero.th.api.users.UserAvailabilityDTO;
import com.tradehero.th.network.retrofit.CallbackWithSpecificNotifiers;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.RetrofitError;
import retrofit.client.Response;


public abstract class AbstractUserAvailabilityRequester extends CallbackWithSpecificNotifiers<UserAvailabilityDTO>
{
    protected String displayName;
    private boolean available;
    private boolean queried = false;
    private boolean received = false;

    @Inject protected Lazy<UserService> userService;

    public AbstractUserAvailabilityRequester(String displayName)
    {
        this();
        this.displayName = displayName;
    }

    public AbstractUserAvailabilityRequester()
    {
        DaggerUtils.inject(this);
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
            userService.get().checkDisplayNameAvailable(displayName, this);
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
