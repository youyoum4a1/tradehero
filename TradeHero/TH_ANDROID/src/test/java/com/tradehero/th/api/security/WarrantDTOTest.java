package com.tradehero.th.api.security;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class WarrantDTOTest
{
    public static final String TAG = WarrantDTOTest.class.getSimpleName();

    public static final String EXT_KEY_WARRANT_TYPE = "warrantType";
    public static final String EXT_KEY_EXPIRY_DATE = "expiryDate";
    public static final String EXT_KEY_STRIKE_PRICE = "strikePrice";
    public static final String EXT_KEY_STRIKE_PRICE_CCY = "strikePriceCcy";
    public static final String EXT_KEY_ISSUER_NAME = "issuerName";
    public static final String EXT_KEY_UNDERLYING_NAME = "underlyingName";
    public static final String EXT_KEY_EXTERNAL_APP_URL = "externalAppURL";
    public static final String EXT_KEY_FALLBACK_EXTERNAL_URL = "fallbackExternalURL";

    private Map<String, Object> getPairs1()
    {
        Map<String, Object> returned = new HashMap<>();
        returned.put(SecurityCompactDTOTest.EXT_KEY_EXCHANGE, "SGX");
        returned.put(EXT_KEY_WARRANT_TYPE, "C");
        returned.put(EXT_KEY_EXPIRY_DATE, "2014-04-30T00:00:00");
        returned.put(EXT_KEY_STRIKE_PRICE, 123d);
        returned.put(EXT_KEY_STRIKE_PRICE_CCY, "SGD");
        returned.put(EXT_KEY_ISSUER_NAME, "Mamacq");
        returned.put(EXT_KEY_UNDERLYING_NAME, "Keppel");
        returned.put(EXT_KEY_EXTERNAL_APP_URL, "http://example.com/1");
        returned.put(EXT_KEY_FALLBACK_EXTERNAL_URL, "http://example.com/2");
        return returned;
    }

    private void putKeys(SecurityCompactDTO securityCompactDTO, Map<String, Object> pairs)
    {
        for (Map.Entry<String, Object> pair: pairs.entrySet())
        {
            securityCompactDTO.put(pair.getKey(), pair.getValue());
        }
    }

    private void assertSecurityCompactHasPairs1(SecurityCompactDTO securityCompactDTO)
    {
        assertEquals("SGX", securityCompactDTO.exchange);
    }

    private void assertWarrantHasPairs1(WarrantDTO warrantDTO)
    {
        assertEquals("C", warrantDTO.warrantType);

        assertNotNull(warrantDTO.expiryDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(warrantDTO.expiryDate);
        assertEquals(2014, calendar.get(Calendar.YEAR));

        assertEquals(Double.valueOf(123d), warrantDTO.strikePrice);
        assertEquals("SGD", warrantDTO.strikePriceCcy);
        assertEquals("Mamacq", warrantDTO.issuerName);
        assertEquals("Keppel", warrantDTO.underlyingName);
        assertEquals("http://example.com/1", warrantDTO.externalAppURL);
        assertEquals("http://example.com/2", warrantDTO.fallbackExternalURL);
    }

    @Test public void canPopulateExtraParts()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.putAll(getPairs1(), WarrantDTO.class);
        assertWarrantHasPairs1(warrantDTO);
    }

    @Test public void canConstructFromExtendedSecurityCompact()
    {
        SecurityCompactDTO securityCompactDTO = new SecurityCompactDTO();
        putKeys(securityCompactDTO, getPairs1());
        WarrantDTO created = new WarrantDTO(securityCompactDTO);

        assertSecurityCompactHasPairs1(created);
        assertWarrantHasPairs1(created);
    }
}
