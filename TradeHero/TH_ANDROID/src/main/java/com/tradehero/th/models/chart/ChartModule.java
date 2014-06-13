package com.tradehero.th.models.chart;

import com.tradehero.th.models.chart.yahoo.YahooChartDTOFactory;
import dagger.Module;
import dagger.Provides;

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
    public ChartModule()
    {
        super();
    }

    @Provides ChartDTOFactory provideChartDTOFactory(YahooChartDTOFactory chartDTOFactory)
    {
        return chartDTOFactory;
    }
}
