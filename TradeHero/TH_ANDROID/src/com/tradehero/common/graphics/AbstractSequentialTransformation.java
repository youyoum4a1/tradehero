package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/19/13 Time: 11:58 AM To change this template use File | Settings | File Templates. */
abstract public class AbstractSequentialTransformation implements Transformation
{
    protected List<Transformation> transformationList;

    public AbstractSequentialTransformation (List<Transformation> transformationList)
    {
        this.transformationList = transformationList;
    }

    public Bitmap transform (Bitmap original)
    {
        Bitmap previous;
        Bitmap converted = original;
        for(Transformation transformation: transformationList)
        {
            previous = converted;
            converted = transformation.transform(previous);
            previous.recycle();
        }
        return converted;
    }
}
