package com.ayondo.academy.fragments.authentication;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;
import com.ayondo.academy.R;
import com.ayondo.academy.api.social.SocialNetworkEnum;

public class AuthenticationButton extends Button
{
    private SocialNetworkEnum type;

    //region constructors
    public AuthenticationButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public AuthenticationButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
    }
    //endregion

    private void init(Context context, AttributeSet attrs)
    {
        if (!isInEditMode())
        {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AuthenticationButton);
            int indexEnum = a.getInt(R.styleable.AuthenticationButton_type, -1);
            if (indexEnum == -1)
            {
                throw new IllegalArgumentException("There was no Type defined");
            }
            if (indexEnum >= SocialNetworkEnum.values().length)
            {
                throw new IllegalArgumentException("IndexEnum of " + indexEnum + " is too large");
            }
            type = SocialNetworkEnum.fromIndex(a.getInt(R.styleable.AuthenticationButton_type, -1));
            a.recycle();
        }
    }

    public SocialNetworkEnum getType()
    {
        return type;
    }
}
