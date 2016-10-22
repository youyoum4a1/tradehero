package com.androidth.general.models.chart;

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

    @Provides ChartDTOFactory provideChartDTOFactory(ChartDTOFactory chartDTOFactory)
    {
        return chartDTOFactory;
    }
}
