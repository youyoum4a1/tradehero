package com.ayondo.academy.api.billing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ayondo.academyRobolectricTestRunner;
import com.tradehero.common.annotation.ForApp;
import com.ayondo.academy.api.BaseApiTestClass;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class AmazonPurchaseInProcessDTOTest extends BaseApiTestClass
{
    @Inject @ForApp ObjectMapper normalMapper;
    private InputStream purchaseInProcessBody1Stream;

    @Before public void setUp()
    {
        purchaseInProcessBody1Stream = getClass().getResourceAsStream(getPackagePath() + "/AmazonPurchaseInProcessDTOBody1.json");
    }

    @Test public void testCanDeserialise() throws IOException
    {
        AmazonPurchaseInProcessDTO dto = normalMapper.readValue(purchaseInProcessBody1Stream, AmazonPurchaseInProcessDTO.class);

        assertThat(dto.amazonPurchaseToken).isEqualTo("receiptId1");
        assertThat(dto.amazonUserId).isEqualTo("amazonUserId319057");
        assertThat(dto.applicablePortfolioId).isEqualTo(new OwnedPortfolioId(319057, 621916));
        assertThat(dto.userToFollow).isNull();
    }

}
