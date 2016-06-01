package com.tradehero.common.utils;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.portfolio.PortfolioDTO;
import com.ayondo.academy.api.position.GetPositionsDTO;
import com.ayondo.academy.base.TestTHApp;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;

import static com.tradehero.util.TestUtil.getResourceAsByteArray;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ConverterDeserialisationTest
{
    private static final String JSON_MIME_UTF8 = "application/json; charset=utf-8";

    @Inject Converter converter;

    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    @Test public void shouldCreatePortfolioDTO1() throws Exception
    {
        byte[] jsonFile = getResourceAsByteArray(PortfolioDTO.class, "PortfolioDTOBody1.json");
        TypedByteArray typedInput = new TypedByteArray(JSON_MIME_UTF8, jsonFile);
        Object converted = converter.fromBody(typedInput, PortfolioDTO.class);
        assertThat(converted, instanceOf(PortfolioDTO.class));
        assertThat(((PortfolioDTO)converted).id, equalTo(274889));
    }

    @Test public void shouldCreateGetPositionsDTO1() throws Exception
    {
        byte[] jsonFile = getResourceAsByteArray(GetPositionsDTO.class, "GetPositionsDTOBody1.json");
        TypedByteArray typedInput = new TypedByteArray(JSON_MIME_UTF8, jsonFile);

        Object converted = converter.fromBody(typedInput, GetPositionsDTO.class);
        assertThat(converted, instanceOf(GetPositionsDTO.class));
    }

    @Test public void shouldCreateGetPositionsDTO2() throws Exception
    {
        byte[] jsonFile = getResourceAsByteArray(GetPositionsDTO.class, "GetPositionsDTOBody2.json");
        TypedByteArray typedInput = new TypedByteArray(JSON_MIME_UTF8, jsonFile);

        Object converted = converter.fromBody(typedInput, GetPositionsDTO.class);
        assertThat(converted, instanceOf(GetPositionsDTO.class));
        assertThat(((GetPositionsDTO) converted).openPositionsCount, equalTo(10));
    }
}
