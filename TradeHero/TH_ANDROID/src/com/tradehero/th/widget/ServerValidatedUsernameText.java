package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserAvailabilityRequester;
import retrofit.RetrofitError;

import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: tho Date: 8/27/13 Time: 10:25 AM Copyright (c) TradeHero */
public class ServerValidatedUsernameText extends ServerValidatedText
{
    private boolean isValidInServer = true;

    private Map<String, UserAvailabilityRequester> alreadyRequested = new HashMap<>();

    public ServerValidatedUsernameText(Context context)
    {
        super(context);
    }

    public ServerValidatedUsernameText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ServerValidatedUsernameText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected boolean validate()
    {
        boolean superValidate = super.validate();

        if (!superValidate)
        {
            // We need to reset the value as otherwise it will prompt username taken even when field is empty
            isValidInServer = true;
            return false;
        }

        String displayName = getText().toString();

        if (displayName != null && alreadyRequested.containsKey(displayName))
        {
            isValidInServer = alreadyRequested.get(displayName).isAvailable();
            alreadyRequested.get(displayName).askServerIfNeeded();
        }
        else if (displayName != null)
        {
            createNewRequester();
        }
        return superValidate && isValidInServer;
    }

    private void createNewRequester ()
    {
        String displayName = getText().toString();
        if (!alreadyRequested.containsKey(displayName))
        {
            UserAvailabilityRequester requester = new UserAvailabilityRequester(displayName) {

                @Override public void notifyAvailabilityChanged()
                {
                    if (this.getDisplayName().equals(getText().toString()))
                    {
                        handleReturnFromServer (this.isAvailable());
                    }
                }

                @Override public void notifyIsQuerying(boolean isQuerying)
                {
                    handleServerRequest(isQuerying);
                }

                @Override public void notifyNetworkError(RetrofitError retrofitError)
                {
                    super.notifyNetworkError(retrofitError);
                    handleNetworkError(retrofitError);
                }
            };
            alreadyRequested.put(displayName, requester);
            requester.askServerIfNeeded();
        }
    }

    private void handleReturnFromServer (boolean newIsValidFromServer)
    {
        boolean hasChanged = isValidInServer != newIsValidFromServer;
        isValidInServer = newIsValidFromServer;

        if (hasChanged)
        {
            this.post (new Runnable()
            {
                @Override public void run()
                {
                    setValid(validate());
                }
            });
        }
    }

    @Override public ValidationMessage getCurrentValidationMessage()
    {
        if (!isValidInServer)
        {
            return new ValidationMessage(this, false, getContext().getString(R.string.validation_server_username_not_available));
        }
        return new ValidationMessage(this, isValid(), null);
    }

    public void handleNetworkError (RetrofitError retrofitError)
    {
        hintDefaultStatus();
    }
}
