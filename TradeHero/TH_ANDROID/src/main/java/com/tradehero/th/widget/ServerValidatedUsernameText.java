package com.tradehero.th.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import com.tradehero.th.R;
import com.tradehero.th.api.users.DisplayNameDTO;
import com.tradehero.th.api.users.UserAvailabilityDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.user.UserAvailabilityCacheRx;
import javax.inject.Inject;
import retrofit.RetrofitError;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ServerValidatedUsernameText extends ServerValidatedText
{
    @Inject UserAvailabilityCacheRx userAvailabilityCache;
    @Nullable private Subscription userAvailabilitySubscription;
    private boolean isValidInServer = true;
    private String originalUsernameValue;

    //<editor-fold desc="Constructors">
    public ServerValidatedUsernameText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onDetachedFromWindow()
    {
        detachUserAvailabilityCache();
        super.onDetachedFromWindow();
    }

    private void detachUserAvailabilityCache()
    {
        Subscription copy = userAvailabilitySubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        userAvailabilitySubscription = null;
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
                    userAvailabilityCache.getCachedValue(new DisplayNameDTO(displayName));
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
        return super.getCurrentValidationMessage();
    }

    protected void queryCache(@Nullable String displayName)
    {
        if (displayName != null)
        {
            handleServerRequest(true);
            DisplayNameDTO key = new DisplayNameDTO(displayName);
            detachUserAvailabilityCache();
            userAvailabilitySubscription = userAvailabilityCache.get(key)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createValidatedUserNameObserver());
        }
    }

    private void handleReturnFromServer(boolean newIsValidFromServer)
    {
        boolean hasChanged = isValidInServer != newIsValidFromServer;
        isValidInServer = newIsValidFromServer;

        if (hasChanged)
        {
            setValid(validate());
        }
    }

    public void handleNetworkError(RetrofitError retrofitError)
    {
        hintDefaultStatus();
    }

    @NonNull protected Observer<Pair<DisplayNameDTO, UserAvailabilityDTO>> createValidatedUserNameObserver()
    {
        return new ValidatedUserNameAvailabilityObserver();
    }

    protected class ValidatedUserNameAvailabilityObserver implements Observer<Pair<DisplayNameDTO, UserAvailabilityDTO>>
    {
        @Override public void onNext(Pair<DisplayNameDTO, UserAvailabilityDTO> pair)
        {
            handleServerRequest(false);
            handleReturnFromServer(pair.second.available);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            handleServerRequest(false);
            if (e instanceof RetrofitError && ((RetrofitError) e).isNetworkError())
            {
                handleNetworkError((RetrofitError) e);
            }
        }
    }
}
