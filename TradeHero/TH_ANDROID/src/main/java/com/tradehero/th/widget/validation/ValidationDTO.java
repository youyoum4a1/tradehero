package com.ayondo.academy.widget.validation;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.ayondo.academy.R;
import java.util.regex.Pattern;

public class ValidationDTO
{
    private static final int DEFAULT_VALIDATE_DELAY_MILLI_SECONDS = 200;
    private static final boolean DEFAULT_VALIDATE_ONLY_IF_HAD_INTERACTION = true;
    private static final boolean DEFAULT_VALIDATE_ONLY_IF_NOT_EMPTY = false;

    public final int minTextLength;
    public final int maxTextLength;
    public final long validateDelayMilliseconds;
    @Nullable public final Pattern validatePattern;
    public final int invalidMinTextLengthMessage;
    public final int invalidMaxTextLengthMessage;
    public final int invalidBetweenTextLengthMessage;
    @NonNull public final String invalidPatternMessage;
    @IdRes @Nullable public final  Integer progressIndicatorId;
    public final boolean validateOnlyIfHadInteraction;
    public final boolean validateOnlyIfNotEmpty;

    //<editor-fold desc="Constructors">
    public ValidationDTO(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ValidatedText);
        minTextLength = a.getInt(R.styleable.ValidatedText_minTextLength, 0);
        maxTextLength = a.getInt(R.styleable.ValidatedText_maxTextLength, Integer.MAX_VALUE);
        validateDelayMilliseconds = a.getInt(R.styleable.ValidatedText_delayValidationByMilliSec, DEFAULT_VALIDATE_DELAY_MILLI_SECONDS);
        String validatePatternString = a.getString(R.styleable.ValidatedText_validatePattern);
        validatePattern = validatePatternString != null ? Pattern.compile(validatePatternString) : null;
        String message;
        invalidMinTextLengthMessage =R.string.validation_size_too_short;
        invalidMaxTextLengthMessage = R.string.validation_size_too_long;
        invalidBetweenTextLengthMessage =  R.string.validation_size_in_between;
        message = a.getString(R.styleable.ValidatedText_invalidPatternMessage);
        invalidPatternMessage = message != null ? message : context.getString(R.string.validation_incorrect_pattern);
        int resourceId = a.getResourceId(R.styleable.ValidatedText_progressIndicator, 0);
        progressIndicatorId = resourceId > 0 ? resourceId : null;
        validateOnlyIfHadInteraction = a.getBoolean(R.styleable.ValidatedText_validateOnlyIfHadInteraction, DEFAULT_VALIDATE_ONLY_IF_HAD_INTERACTION);
        validateOnlyIfNotEmpty = a.getBoolean(R.styleable.ValidatedText_validateOnlyIfNotEmpty, DEFAULT_VALIDATE_ONLY_IF_NOT_EMPTY);
        a.recycle();
    }
    //</editor-fold>
}
