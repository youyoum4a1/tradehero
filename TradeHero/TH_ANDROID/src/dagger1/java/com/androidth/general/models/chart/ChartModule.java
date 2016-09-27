package com.androidth.general.models.chart;

import com.androidth.general.models.chart.reuters.ReutersChartDTOFactory;
import com.androidth.general.models.chart.yahoo.YahooChartDTOFactory;
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
