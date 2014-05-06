package com.tradehero.th.api.security;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SecurityCompactDTOFactoryTest
{
    public static final String TAG = SecurityCompactDTOFactoryTest.class.getSimpleName();

    @Test public void nullReturnsNull()
    {
        assertNull(new SecurityCompactDTOFactory().clonePerType((SecurityCompactDTO) null));
        assertNull(new SecurityCompactDTOFactory().clonePerType((List<SecurityCompactDTO>) null));
    }

    @Test public void securityCompactUnchanged()
    {
        SecurityCompactDTO securityCompactDTO = new SecurityCompactDTO();
        securityCompactDTO.securityType = SecurityType.EQUITY.value;
        SecurityCompactDTO cloned = new SecurityCompactDTOFactory().clonePerType(securityCompactDTO);
        assertSame(securityCompactDTO, cloned);
        assertEquals(SecurityCompactDTO.class, cloned.getClass());
    }

    @Test public void warrantIsCreated()
    {
        SecurityCompactDTO securityCompactDTO = new SecurityCompactDTO();
        securityCompactDTO.securityType = SecurityType.WARRANT.value;
        SecurityCompactDTO cloned = new SecurityCompactDTOFactory().clonePerType(securityCompactDTO);
        assertNotSame(securityCompactDTO, cloned);
        assertFalse(SecurityCompactDTO.class.equals(cloned.getClass()));
        assertEquals(WarrantDTO.class, cloned.getClass());
    }

    @Test public void listCanMix()
    {
        SecurityCompactDTO basicSecurityCompactDTO = new SecurityCompactDTO();
        basicSecurityCompactDTO.securityType = SecurityType.EQUITY.value;

        SecurityCompactDTO warrantSecurityCompactDTO = new SecurityCompactDTO();
        warrantSecurityCompactDTO.securityType = SecurityType.WARRANT.value;

        List<SecurityCompactDTO> list = new ArrayList<>();
        list.add(basicSecurityCompactDTO);
        list.add(warrantSecurityCompactDTO);

        List<SecurityCompactDTO> cloned = new SecurityCompactDTOFactory().clonePerType(list);

        assertEquals(2, cloned.size());
        assertTrue(SecurityCompactDTO.class.equals(cloned.get(0).getClass()));
        assertSame(basicSecurityCompactDTO, cloned.get(0));
        assertFalse(SecurityCompactDTO.class.equals(cloned.get(1).getClass()));
        assertTrue(WarrantDTO.class.equals(cloned.get(1).getClass()));
        assertNotSame(warrantSecurityCompactDTO, cloned.get(1));
    }
}
