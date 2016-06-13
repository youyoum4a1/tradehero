package com.androidth.general.widget.validation;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class TextValidator implements View.OnFocusChangeListener, TextWatcher
{
    @NonNull protected final BehaviorSubject<ValidationMessage> validationMessageSubject;
    @NonNull protected final Resources resources;
    @NonNull protected final ValidationDTO validationDTO;
    protected boolean hasHadInteraction;
    protected boolean hasTextChanged;
    @NonNull protected CharSequence text;
    @Nullable Subscription delayedValidationSubscription;

    //<editor-fold desc="Constructors">
    public TextValidator(@NonNull Resources resources, @NonNull ValidationDTO validationDTO)
    {
        this.resources = resources;
        this.validationDTO = validationDTO;
        this.validationMessageSubject = BehaviorSubject.create();
        this.hasHadInteraction = false;
        this.hasTextChanged = false;
        this.text = "";
    }
    //</editor-fold>

    public void setText(@NonNull CharSequence text)
    {
        this.text = text;
        performValidation();
    }

    @NonNull public Observable<ValidationMessage> getValidationMessageObservable()
    {
        return validationMessageSubject.asObservable();
    }

    @Override public void onFocusChange(View v, boolean hasFocus)
    {
        if (!hasFocus)
        {
            // This means the player has moved away
            // It assumes that this method is not called as part of the constructor.
            hasHadInteraction = true;
        }
        else hasHadInteraction = false;
        //validate();
        performValidation();
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }

    @Override public void onTextChanged(CharSequence text, int start, int before, int count)
    {
        /*if (!hasHadInteraction && text.length() > 0)
        {
            hasHadInteraction = true;
        }
        setText(text);*/
        if(text.length() > 0){
            hasTextChanged = true;
        }
        setText(text);
    }
    private void performValidation(){
        if(hasTextChanged && hasHadInteraction){
            validate();
        }
    }

    @Override public void afterTextChanged(Editable s)
    {
    }

    protected void delayedValidate()
    {
        if (delayedValidationSubscription != null)
        {
            delayedValidationSubscription.unsubscribe();
        }
        delayedValidationSubscription = Observable.just(getValidationMessage())
                .delay(validationDTO.validateDelayMilliseconds, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<ValidationMessage>()
                        {
                            @Override public void call(ValidationMessage validationMessage)
                            {
                                validationMessageSubject.onNext(validationMessage);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                Timber.e(throwable, "Failed to delay validation");
                            }
                        });
    }

    public void validate()
    {
        validationMessageSubject.onNext(getValidationMessage());
    }

    @NonNull protected ValidationMessage getValidationMessage()
    {
        ValidatedView.Status status = ValidatedView.Status.QUIET;
        String message = null;
        if (needsToHintValidStatus() && needsToValidate())
        {
            if (!isMinSizeValid())
            {
                status = ValidatedView.Status.INVALID;
                message = resources.getString(validationDTO.invalidMinTextLengthMessage, validationDTO.minTextLength);
            }
            else if (!isMaxSizeValid())
            {
                status = ValidatedView.Status.INVALID;
                message = resources.getString(validationDTO.invalidMaxTextLengthMessage, validationDTO.maxTextLength);
            }
            else if (!isPatternValid())
            {
                status = ValidatedView.Status.INVALID;
                message = validationDTO.invalidPatternMessage;
            }
            else
            {
                status = ValidatedView.Status.VALID;
            }
        }
        return new ValidationMessage(status, message);
    }

    protected boolean needsToHintValidStatus()
    {
        return hasHadInteraction || !validationDTO.validateOnlyIfHadInteraction || hasTextChanged;
    }

    public boolean needsToValidate()
    {
        return !(TextUtils.isEmpty(text) && validationDTO.validateOnlyIfNotEmpty);
    }

    protected boolean isMinSizeValid()
    {
        return validationDTO.minTextLength <= text.length();
    }

    protected boolean isMaxSizeValid()
    {
        return text.length() <= validationDTO.maxTextLength;
    }

    protected boolean isPatternValid()
    {
        return validationDTO.validatePattern == null
                || validationDTO.validatePattern.matcher(text).matches();
    }
    public boolean isFocussedChanged(){

        return hasHadInteraction;
    }
    public boolean isTextChanged(){
        return hasTextChanged;
    }
    public void setFocus(boolean bool){
        this.hasHadInteraction = bool;
    }
    public void setHasTextChanged(boolean bool){
        this.hasTextChanged = bool;
    }

}
