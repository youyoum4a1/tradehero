package com.tradehero.th.models.graphics;

import com.squareup.picasso.Transformation;
import com.tradehero.common.graphics.RoundedShapeTransformation;
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

    @Provides @Named(TransformationUsage.USER_PHOTO)
    public Transformation provideUserImageTransformation()
    {
        return new RoundedShapeTransformation();
    }
}
