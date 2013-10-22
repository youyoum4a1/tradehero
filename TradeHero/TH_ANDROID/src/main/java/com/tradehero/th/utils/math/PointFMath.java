package com.tradehero.th.utils.math;

import android.graphics.Point;
import android.graphics.PointF;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 12:08 PM To change this template use File | Settings | File Templates. */
public class PointFMath extends PointF
{
    public static final String TAG = PointFMath.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public PointFMath()
    {
        super();
    }

    public PointFMath(Point p)
    {
        super(p);
    }

    public PointFMath(PointF p)
    {
        super(p.x, p.y);
    }

    public PointFMath(float x, float y)
    {
        super(x, y);
    }
    //</editor-fold>

    public void add(PointF point)
    {
        x += point.x;
        y += point.y;
    }

    public void subtract(PointF point)
    {
        x -= point.x;
        y -= point.y;
    }
}
