package com.tradehero.th.models.chart;

import com.tradehero.th.models.chart.yahoo.YahooChartDTOFactory;
import dagger.Module;
import dagger.Provides;

/**
 * Created by xavier on 8/2/14.
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
public class ChartModule
{
    public static final String TAG = ChartModule.class.getSimpleName();

    public ChartModule()
    {
        super();
    }

    @Provides ChartDTOFactory provideChartDTOFactory()
    {
        return new YahooChartDTOFactory();
    }
}
