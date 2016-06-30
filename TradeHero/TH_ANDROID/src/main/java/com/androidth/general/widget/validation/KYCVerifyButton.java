package com.androidth.general.widget.validation;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageButton;
import com.androidth.general.R;

public class KYCVerifyButton extends ImageButton
{
    public VerifyButtonState state;

    public KYCVerifyButton(Context context)
    {
        super(context);
    }

    public KYCVerifyButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public KYCVerifyButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) public KYCVerifyButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public VerifyButtonState getState()
    {
        return state;
    }

    public void setState(VerifyButtonState state)
    {
        this.state = state;

        switch (state) {
            case BEGIN:
                this.setImageResource(R.drawable.ic_grey_alert);
                break;
            case VALIDATE:
            case PENDING:
                this.setImageResource(R.drawable.grey_tick);
                break;
            case FINISH:
                this.setImageResource(R.drawable.green_tick);
                break;
            case ERROR:
                this.setImageResource(R.drawable.red_alert);
                break;
        }
    }
}
