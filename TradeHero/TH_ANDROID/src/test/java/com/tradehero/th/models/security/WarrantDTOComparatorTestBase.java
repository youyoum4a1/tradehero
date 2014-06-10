package com.tradehero.th.models.security;

import com.tradehero.th.api.security.WarrantDTO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class WarrantDTOComparatorTestBase
{
    //<editor-fold desc="Creators">
    protected WarrantDTO getAC()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.underlyingName = "A";
        warrantDTO.warrantType = "C";
        return warrantDTO;
    }

    protected WarrantDTO getAP()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.underlyingName = "A";
        warrantDTO.warrantType = "P";
        return warrantDTO;
    }

    protected WarrantDTO getANull()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.underlyingName = "A";
        return warrantDTO;
    }

    protected WarrantDTO getZC()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.underlyingName = "Z";
        warrantDTO.warrantType = "C";
        return warrantDTO;
    }

    protected WarrantDTO getZP()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.underlyingName = "Z";
        warrantDTO.warrantType = "P";
        return warrantDTO;
    }

    protected WarrantDTO getZNull()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.underlyingName = "Z";
        return warrantDTO;
    }

    protected WarrantDTO getNullC()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.warrantType = "C";
        return warrantDTO;
    }

    protected WarrantDTO getNullP()
    {
        WarrantDTO warrantDTO = new WarrantDTO();
        warrantDTO.warrantType = "P";
        return warrantDTO;
    }

    protected WarrantDTO getNullNull()
    {
        return new WarrantDTO();
    }

    protected WarrantDTO getNull()
    {
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Own Asserts">
    protected void assertEqualsWarrant(WarrantDTO expected, WarrantDTO actual)
    {
        if (expected == null)
        {
            assertNull(actual);
        }
        else
        {
            assertNotNull(actual);
            assertEqualsUnderlying(expected, actual);
            assertEqualsType(expected, actual);
        }
    }

    protected void assertEqualsUnderlying(WarrantDTO expected, WarrantDTO actual)
    {
        if (expected.underlyingName == null)
        {
            assertNull(actual.underlyingName);
        }
        else
        {
            assertNotNull(actual.underlyingName);
            assertEquals(expected.underlyingName, actual.underlyingName);
        }
    }

    protected void assertEqualsType(WarrantDTO expected, WarrantDTO actual)
    {
        if (expected.warrantType == null)
        {
            assertNull(actual.warrantType);
        }
        else
        {
            assertNotNull(actual.warrantType);
            assertEquals(expected.warrantType, actual.warrantType);
        }
    }
    //</editor-fold>
}
