package com.tradehero.th.network.service;

import com.android.internal.util.Predicate;
import com.tradehero.THRobolectricTestRunner;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class ServiceWrapperTest extends AbstractServiceTestBase
{
    @Test public void canGetAllServices()
    {
        int serviceCount = 27;
        assertThat(getAllServices().size()).isEqualTo(serviceCount );
        assertThat(getAllServiceAsyncs().size()).isEqualTo(serviceCount);
        assertThat(getAllServiceWrappers().size()).isEqualTo(serviceCount);
    }
}
