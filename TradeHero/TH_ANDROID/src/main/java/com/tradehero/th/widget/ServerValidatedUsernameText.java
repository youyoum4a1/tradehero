package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.R;
import com.tradehero.th.api.form.AbstractUserAvailabilityRequester;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import retrofit.RetrofitError;


public class ServerValidatedUsernameText extends ServerValidatedText
{
    private boolean isValidInServer = true;

    public String getOriginalUsernameValue() {
        return originalUsernameValue;
    }

    public void setOriginalUsernameValue(String originalUsernameValue) {
        this.originalUsernameValue = originalUsernameValue;
    }

    private String originalUsernameValue;
    private Map<String, AbstractUserAvailabilityRequester> alreadyRequested = new HashMap<>();

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
        boolean sameDisplayName = (this.originalUsernameValue != null && this.originalUsernameValue.equalsIgnoreCase(displayName));
        if (sameDisplayName)
        {
            isValidInServer = true;
            return true;
        }

        if (alreadyRequested.containsKey(displayName))
        {
            isValidInServer = alreadyRequested.get(displayName).isAvailable();
            alreadyRequested.get(displayName).askServerIfNeeded();
        }
        else
        {
            createNewRequester();
        }
        return isValidInServer;
    }

    private void createNewRequester ()
    {
        String displayName = getText().toString();
        if (!alreadyRequested.containsKey(displayName))
        {
            AbstractUserAvailabilityRequester requester = createUserAvailabilityRequester(displayName);
            alreadyRequested.put(displayName, requester);
            requester.askServerIfNeeded();
        }
    }

    private AbstractUserAvailabilityRequester createUserAvailabilityRequester(String displayName)
    {
        return new UserAvailabilityRequester(this, displayName);
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
        return super.getCurrentValidationMessage();// new ValidationMessage(this, isValid(), null);
    }

    public void handleNetworkError (RetrofitError retrofitError)
    {
        hintDefaultStatus();
    }

    public static class UserAvailabilityRequester extends AbstractUserAvailabilityRequester
    {
        private ServerValidatedUsernameText text;

        @Inject
        public UserAvailabilityRequester()
        {
        }

        public UserAvailabilityRequester(ServerValidatedUsernameText text, String displayName)
        {
            this.displayName = displayName;
            this.text = text;
        }

        @Override public void notifyAvailabilityChanged()
        {
            if (this.getDisplayName().equals(text.getText().toString()))
            {
                text.handleReturnFromServer (this.isAvailable());
            }
        }

        @Override public void notifyIsQuerying(boolean isQuerying)
        {
            text.handleServerRequest(isQuerying);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            super.failure(retrofitError);
            if (retrofitError.isNetworkError())
            {
                text.handleNetworkError(retrofitError);
            }
        }
    }
}
