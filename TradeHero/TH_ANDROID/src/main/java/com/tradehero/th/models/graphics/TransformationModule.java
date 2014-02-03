package com.tradehero.th.models.graphics;

import com.squareup.picasso.Transformation;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.th.graphics.ForUserPhoto;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

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
    public Transformation provideUserImageTransformation2()
    {
        return new RoundedShapeTransformation();
    }
}
