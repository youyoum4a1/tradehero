package com.tradehero.th.utils.math;

import android.graphics.Point;
import android.graphics.PointF;

public class PointFMath extends PointF
{
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
