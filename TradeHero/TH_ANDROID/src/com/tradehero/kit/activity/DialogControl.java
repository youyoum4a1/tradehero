package com.tradehero.kit.activity;

/**
 * Created with IntelliJ IDEA. User: nguyentruongtho.sg@gmail.com Date: 6/30/13 Time: 12:30 AM
 * Copyright @ tradehero All Rights reserved
 */

public abstract interface DialogControl
{
    public abstract void hideWaitDialog();

    public abstract boolean shouldBeWaiting();
}
