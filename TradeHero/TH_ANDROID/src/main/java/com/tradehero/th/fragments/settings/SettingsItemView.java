package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.th.R;

public class SettingsItemView extends LinearLayout
{
    private TextView textView;

    //<editor-fold desc="Constructors">
    public SettingsItemView(Context context)
    {
        this(context, null);
    }

    public SettingsItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
    }

    public SettingsItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        init();
    }

    private void init()
    {
        textView = (TextView) findViewById(R.id.settingsItem_textView);
    }

    public void display(String text)
    {
        textView.setText(text);
    }
}
