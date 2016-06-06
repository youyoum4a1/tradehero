package com.androidth.general.common.graphics;

import com.squareup.picasso.Transformation;

public interface RecyclerTransformation extends Transformation
{
    void setRecycleOriginal(boolean recycleOriginal);
    boolean isRecycleOriginal();
}
