package com.tradehero.th.models.push;

import android.content.Intent;
import android.os.Bundle;
import java.util.Set;

public class IntentLogger
{
    private final Intent intent;

    public IntentLogger(Intent intent)
    {
        this.intent = intent;
    }

    @Override public String toString()
    {
        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            Set<String> keys = extras.keySet();
            StringBuilder sb = new StringBuilder();
            for (String key : keys)
            {
                sb.append("\r\n")
                        .append(key)
                        .append(": ")
                        .append(intent.getExtras().get(key));
            }
            return sb.toString();
        }
        return null;
    }
}