package com.tradehero.th.ui;

import android.content.Context;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        library = true,
        complete = false,
        overrides = true
)
public class GraphicTestModule
{
    @Provides @Singleton Picasso providePicasso(Context context)
    {
        return new Picasso.Builder(context).build();
    }
}
