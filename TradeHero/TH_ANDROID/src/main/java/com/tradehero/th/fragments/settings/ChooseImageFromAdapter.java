package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.widget.ArrayAdapter;
import com.tradehero.th.fragments.settings.photo.ChooseImageFromDTO;

public class ChooseImageFromAdapter extends ArrayAdapter<ChooseImageFromDTO>
{
    public ChooseImageFromAdapter(Context context)
    {
        super(context, 0);
    }
}
