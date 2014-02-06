package com.tradehero.th.models.graphics;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.squareup.picasso.Transformation;
import com.tradehero.common.graphics.AbstractSequentialTransformation;
import com.tradehero.common.graphics.FastBlurTransformation;
import com.tradehero.common.graphics.GradientTransformation;
import com.tradehero.common.graphics.GrayscaleTransformation;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.th.R;
import dagger.Module;
import dagger.Provides;

/**
 * Created by xavier on 1/30/14.
 */
@Module(
        staticInjections =
                {
                },
        injects =
                {
                },
        complete = false,
        library = true
)
public class TransformationModule
{
    public static final String TAG = TransformationModule.class.getSimpleName();

    public TransformationModule()
    {
        super();
    }

    @Provides @ForUserPhoto
    public Transformation provideUserImageTransformation()
    {
        return new RoundedShapeTransformation();
    }

    @Provides @ForUserPhoto
    public Drawable provideDefaultUserPhoto(Context context, @ForUserPhoto Transformation userImageTransformation)
    {
        return new BitmapDrawable (
                context.getResources(),
                userImageTransformation.transform(
                        BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.superman_facebook)));
    }

    @Provides @ForUserPhotoBackground
    public Transformation provideUserImageBackgroundTransformation(Context context)
    {
        AbstractSequentialTransformation transformation = new AbstractSequentialTransformation()
        {
            @Override public String key()
            {
                return "toGaussianGrayscale11";
            }
        };
        transformation.add(new GrayscaleTransformation());
        transformation.add(new FastBlurTransformation(30));
        transformation.add(new GradientTransformation(
                context.getResources().getColor(R.color.profile_view_gradient_top),
                context.getResources().getColor(R.color.profile_view_gradient_bottom)));
        return transformation;
    }
}
