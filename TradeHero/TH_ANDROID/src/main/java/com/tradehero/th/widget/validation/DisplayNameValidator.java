package com.ayondo.academy.widget.validation;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.ayondo.academy.api.users.DisplayNameDTO;
import com.ayondo.academy.api.users.UserAvailabilityDTO;
import com.ayondo.academy.persistence.user.UserAvailabilityCacheRx;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class DisplayNameValidator extends TextValidator
{
    @NonNull private final UserAvailabilityCacheRx userAvailabilityCache;
    @Nullable private String originalUsernameValue;

    public DisplayNameValidator(
            @NonNull Resources resources,
            @NonNull DisplayNameValidationDTO validationDTO,
            @NonNull UserAvailabilityCacheRx userAvailabilityCache)
    {
        super(resources, validationDTO);
        this.userAvailabilityCache = userAvailabilityCache;
    }

    public void setOriginalUsernameValue(@Nullable String originalUsernameValue)
    {
        this.originalUsernameValue = originalUsernameValue;
    }

    @NonNull @Override public Observable<ValidationMessage> getValidationMessageObservable()
    {
        return super.getValidationMessageObservable()
                .flatMap(new Func1<ValidationMessage, Observable<ValidationMessage>>()
                {
                    @Override public Observable<ValidationMessage> call(final ValidationMessage validationMessage)
                    {
                        if (!validationMessage.getValidStatus().equals(ValidatedView.Status.VALID))
                        {
                            return Observable.just(validationMessage);
                        }
                        if (originalUsernameValue != null && originalUsernameValue.equalsIgnoreCase(text.toString()))
                        {
                            return Observable.just(validationMessage);
                        }
                        return getCheckingAndThenDisplayNameAvailableObservable(text.toString(), validationMessage);
                    }
                });
    }

    @NonNull protected Observable<ValidationMessage> getCheckingAndThenDisplayNameAvailableObservable(
            @NonNull final String displayName,
            @NonNull final ValidationMessage whenValidMessage)
    {
        return Observable.from(new Integer[]{0, 1})
                .flatMap(new Func1<Integer, Observable<ValidationMessage>>()
                {
                    @Override public Observable<ValidationMessage> call(Integer integer)
                    {
                        if (integer == 0)
                        {
                            return Observable.just(new ValidationMessage(ValidatedView.Status.CHECKING, ""));
                        }
                        return getDisplayNameAvailableObservable(displayName, whenValidMessage);
                    }
                });
    }

    @NonNull protected Observable<ValidationMessage> getDisplayNameAvailableObservable(
            @NonNull final String displayName,
            @NonNull final ValidationMessage whenValidMessage)
    {
        return userAvailabilityCache.get(new DisplayNameDTO(displayName))
                .distinctUntilChanged(new Func1<Pair<DisplayNameDTO, UserAvailabilityDTO>, Integer>()
                {
                    @Override public Integer call(Pair<DisplayNameDTO, UserAvailabilityDTO> availabilityPair)
                    {
                        return availabilityPair.first.hashCode() * (availabilityPair.second.available ? 1 : -1);
                    }
                })
                .map(new Func1<Pair<DisplayNameDTO, UserAvailabilityDTO>, ValidationMessage>()
                {
                    @Override public ValidationMessage call(
                            Pair<DisplayNameDTO, UserAvailabilityDTO> displayNameDTOUserAvailabilityDTOPair)
                    {
                        if (displayNameDTOUserAvailabilityDTOPair.second.available)
                        {
                            return whenValidMessage;
                        }
                        return new ValidationMessage(ValidatedView.Status.INVALID,
                                ((DisplayNameValidationDTO) validationDTO).displayNameTakenMessage);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }
}
