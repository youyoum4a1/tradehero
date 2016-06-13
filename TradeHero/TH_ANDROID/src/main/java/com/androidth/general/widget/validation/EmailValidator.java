package com.androidth.general.widget.validation;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.androidth.general.api.users.EmailDTO;
import com.androidth.general.api.users.UserAvailabilityDTO;
import com.androidth.general.persistence.user.UserEmailAvailabilityCacheRx;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by ayushnvijay on 6/13/16.
 */
public class EmailValidator extends TextValidator {

    @NonNull private final UserEmailAvailabilityCacheRx emailAvailabilityCache;
    @Nullable
    private String originalEmailValue;

    public EmailValidator(@NonNull Resources resources, @NonNull EmailValidationDTO validationDTO, @NonNull UserEmailAvailabilityCacheRx emailAvailabilityCache) {
        super(resources, validationDTO);
        this.emailAvailabilityCache =  emailAvailabilityCache;
    }
    public void setOriginalEmailValue(@Nullable String originalEmailValue)
    {
        this.originalEmailValue = originalEmailValue;
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
                        if (originalEmailValue != null && originalEmailValue.equalsIgnoreCase(text.toString()))
                        {
                            return Observable.just(validationMessage);
                        }
                        return getCheckingAndThenEmailAvailableObservable(text.toString(), validationMessage);
                    }
                });
    }
    @NonNull protected Observable<ValidationMessage> getCheckingAndThenEmailAvailableObservable(
            @NonNull final String email,
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
                        return getEmailAvailableObservable(email, whenValidMessage);
                    }
                });
    }

    @NonNull protected Observable<ValidationMessage> getEmailAvailableObservable(
            @NonNull final String email,
            @NonNull final ValidationMessage whenValidMessage)
    {
        return emailAvailabilityCache.get(new EmailDTO(email))
                .distinctUntilChanged(new Func1<Pair<EmailDTO, UserAvailabilityDTO>, Integer>()
                {
                    @Override public Integer call(Pair<EmailDTO, UserAvailabilityDTO> availabilityPair)
                    {
                        return availabilityPair.first.hashCode() * (availabilityPair.second.available ? 1 : -1);
                    }
                })
                .map(new Func1<Pair<EmailDTO, UserAvailabilityDTO>, ValidationMessage>()
                {
                    @Override public ValidationMessage call(
                            Pair<EmailDTO, UserAvailabilityDTO> emailDTOUserAvailabilityDTOPair)
                    {
                        if (emailDTOUserAvailabilityDTOPair.second.available)
                        {
                            return whenValidMessage;
                        }
                        return new ValidationMessage(ValidatedView.Status.INVALID,
                                ((EmailValidationDTO) validationDTO).emailTakenMessage);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }
}
