package com.tradehero.th.utils.touch;

import android.view.MotionEvent;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 12:24 PM To change this template use File | Settings | File Templates. */
public class PointerCoordsLogger
{
    public static final String TAG = PointerCoordsLogger.class.getSimpleName();

    public static String toString(MotionEvent.PointerCoords pointerCoords)
    {
        if (pointerCoords == null)
        {
            return "null";
        }

        return String.format("[pointerCoords x=%f, y=%f, orientation=%f]", pointerCoords.x, pointerCoords.y, pointerCoords.orientation);
    }
}
