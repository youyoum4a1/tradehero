package com.androidth.general.widget.validation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.androidth.general.R;

/**
 * Created by ayushnvijay on 6/13/16.
 */
public class EmailValidationDTO extends ValidationDTO {

    @NonNull
    public final String emailTakenMessage;

    public EmailValidationDTO(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);

        this.emailTakenMessage = context.getString(R.string.validation_server_email_already_taken);
    }
}
