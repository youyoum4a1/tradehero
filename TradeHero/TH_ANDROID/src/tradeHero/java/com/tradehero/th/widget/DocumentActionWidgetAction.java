package com.androidth.general.widget;

import android.support.annotation.NonNull;
import android.view.View;

public class DocumentActionWidgetAction
{
    @NonNull public final View clicked;
    @NonNull public final DocumentActionWidgetActionType actionType;

    public DocumentActionWidgetAction(@NonNull View clicked, @NonNull DocumentActionWidgetActionType actionType)
    {
        this.clicked = clicked;
        this.actionType = actionType;
    }
}
