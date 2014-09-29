package com.tradehero.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class AuthenticationButton extends Button
{
    private SocialNetworkEnum type;

    //region constructors
    public AuthenticationButton(Context context)
    {
        super(context);
    }

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
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AuthenticationButton);
        type = SocialNetworkEnum.fromIndex(a.getInt(R.styleable.AuthenticationButton_type, -1));
        a.recycle();
    }

    public SocialNetworkEnum getType()
    {
        return type;
    }
}
