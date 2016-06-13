package com.androidth.general.widget.validation;

import dagger.Module;

@Module(
        injects = {
                DisplayNameValidatedText.class,
                MatchingPasswordText.class,
                PasswordValidatedText.class,
                EmailValidatedText.class
        },
        library = true,
        complete = false
)
public class WidgetValidationModule
{
}
