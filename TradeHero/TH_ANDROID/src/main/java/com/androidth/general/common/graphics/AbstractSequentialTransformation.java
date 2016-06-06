package com.androidth.general.common.graphics;

import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;
import java.util.ArrayList;
import java.util.List;

abstract public class AbstractSequentialTransformation implements Transformation
{
    protected List<Transformation> transformationList;

    //<editor-fold desc="Constructors">
    public AbstractSequentialTransformation ()
    {
        this.transformationList = new ArrayList<>();
    }
    //</editor-fold>

    public void add(Transformation transformation)
    {
        transformationList.add(transformation);
    }

    public Bitmap transform (Bitmap original)
    {
        Bitmap previous;
        Bitmap converted = original;
        for(Transformation transformation: transformationList)
        {
            previous = converted;
            converted = transformation.transform(previous);
            if (previous != converted)
            {
                previous.recycle();
            }
        }

        if (original != converted)
        {
            original.recycle();
        }
        return converted;
    }
}
