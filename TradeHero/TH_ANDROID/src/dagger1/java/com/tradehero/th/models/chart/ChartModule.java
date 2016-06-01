package com.ayondo.academy.models.chart;

import com.ayondo.academy.models.chart.yahoo.YahooChartDTOFactory;
import dagger.Module;
import dagger.Provides;

@Module(
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
