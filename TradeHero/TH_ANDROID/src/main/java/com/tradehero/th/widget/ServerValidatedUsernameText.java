package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.R;
import com.tradehero.th.api.users.DisplayNameDTO;
import com.tradehero.th.api.users.UserAvailabilityDTO;
import com.tradehero.th.persistence.user.UserAvailabilityCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.RetrofitError;

public class ServerValidatedUsernameText extends ServerValidatedText
        implements UserAvailabilityCache.UserAvailabilityListener
{
    @Inject UserAvailabilityCache userAvailabilityCache;
    private boolean isValidInServer = true;
    private String originalUsernameValue;

    //<editor-fold desc="Constructors">
    public ServerValidatedUsernameText(Context context)
    {
        this(context, null);
    }

    public ServerValidatedUsernameText(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ServerValidatedUsernameText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        userAvailabilityCache.unregister(this);
        super.onDetachedFromWindow();
    }

    public String getOriginalUsernameValue()
    {
        return originalUsernameValue;
    }

    public void setOriginalUsernameValue(String originalUsernameValue)
    {
        this.originalUsernameValue = originalUsernameValue;
    }

    @Override protected boolean validate()
    {
        boolean superValidate = super.validate();

        if (!superValidate)
        {
            // We need to reset the value as otherwise it will prompt that the username is taken even when the field is empty
            isValidInServer = true;
            return false;
        }

        String displayName = getText().toString();
        boolean sameDisplayName =
                (this.originalUsernameValue != null && this.originalUsernameValue.equalsIgnoreCase(
                        displayName));
        if (sameDisplayName)
        {
            isValidInServer = true;
            return true;
        }

        if (displayName != null)
        {
            UserAvailabilityDTO cachedAvailability =
                    userAvailabilityCache.get(new DisplayNameDTO(displayName));
            if (cachedAvailability != null)
            {
                isValidInServer = cachedAvailability.available;
            }
            else
            {
                queryCache(displayName);
            }
        }
        return isValidInServer;
    }

    @Override public ValidationMessage getCurrentValidationMessage()
    {
        if (!isValidInServer)
        {
            return new ValidationMessage(this, false,
                    getContext().getString(R.string.validation_server_username_not_available));
        }
        return super.getCurrentValidationMessage();// new ValidationMessage(this, isValid(), null);
    }

    protected void queryCache(String displayName)
    {
        if (displayName != null)
        {
            handleServerRequest(true);
            DisplayNameDTO key = new DisplayNameDTO(displayName);
            userAvailabilityCache.register(key, this);
            userAvailabilityCache.getOrFetchAsync(key, true);
        }
    }

    @Override public void onDTOReceived(DisplayNameDTO key, UserAvailabilityDTO value,
            boolean fromCache)
    {
        if (key.isSameName(getText().toString()))
        {
            handleServerRequest(false);
            handleReturnFromServer(value.available);
        }
    }

    @Override public void onErrorThrown(DisplayNameDTO key, Throwable error)
    {
        if (key.isSameName(getText().toString()))
        {
            handleServerRequest(false);
            if (error instanceof RetrofitError && ((RetrofitError) error).isNetworkError())
            {
                handleNetworkError((RetrofitError) error);
            }
        }
    }

    private void handleReturnFromServer(boolean newIsValidFromServer)
    {
        boolean hasChanged = isValidInServer != newIsValidFromServer;
        isValidInServer = newIsValidFromServer;

        if (hasChanged)
        {
            this.post(new Runnable()
            {
                @Override public void run()
                {
                    setValid(validate());
                }
            });
        }
    }

    public void handleNetworkError(RetrofitError retrofitError)
    {
        hintDefaultStatus();
    }
}
