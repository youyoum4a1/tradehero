package com.tradehero.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ToggleButton;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import org.jetbrains.annotations.NotNull;

public class SocialLinkToggleButton extends ToggleButton
{
    @NotNull private SocialNetworkEnum socialNetworkEnum;

    //<editor-fold desc="Constructors">
    public SocialLinkToggleButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }
    //</editor-fold>

    private void init(@NotNull Context context, @NotNull AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SocialLinkToggleButton);
        socialNetworkEnum = SocialNetworkEnum.fromIndex(a.getInt(R.styleable.SocialLinkToggleButton_networkType, -1));
        a.recycle();
    }

    @NotNull public SocialNetworkEnum getSocialNetworkEnum()
    {
        return socialNetworkEnum;
    }
}
