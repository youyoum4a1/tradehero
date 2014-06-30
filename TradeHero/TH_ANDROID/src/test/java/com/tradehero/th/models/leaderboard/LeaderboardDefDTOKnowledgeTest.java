package com.tradehero.th.models.leaderboard;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.CountryCodeList;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import java.util.List;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class LeaderboardDefDTOKnowledgeTest
{
    @Inject LeaderboardDefDTOKnowledge leaderboardDefDTOKnowledge;

    @Test public void testUnknownLeaderboardDefWithNoCountryReturnsEmpty()
    {
        assertEquals(
                0,
                leaderboardDefDTOKnowledge.getLeaderboardDefIcon(new LeaderboardDefDTO()).size());
    }

    @Test public void testUnknownLeaderboardDefWithEmptyCountriesReturnsEmpty()
    {
        LeaderboardDefDTO leaderboardDefDTO = new LeaderboardDefDTO();
        leaderboardDefDTO.countryCodes = new CountryCodeList();
        assertEquals(
                0,
                leaderboardDefDTOKnowledge.getLeaderboardDefIcon(leaderboardDefDTO).size());
    }

    @Test public void testUnknownLeaderboardDefWithUnknownCountryReturnsEmpty()
    {
        LeaderboardDefDTO leaderboardDefDTO = new LeaderboardDefDTO();
        leaderboardDefDTO.countryCodes = new CountryCodeList();
        leaderboardDefDTO.countryCodes.add("Not a country");
        assertEquals(
                0,
                leaderboardDefDTOKnowledge.getLeaderboardDefIcon(leaderboardDefDTO).size());
    }

    @Test(expected = NullPointerException.class)
    public void testUnknownLeaderboardDefWithNullCountryNPE()
    {
        LeaderboardDefDTO leaderboardDefDTO = new LeaderboardDefDTO();
        leaderboardDefDTO.countryCodes = new CountryCodeList();
        leaderboardDefDTO.countryCodes.add(null);
        leaderboardDefDTOKnowledge.getLeaderboardDefIcon(leaderboardDefDTO);
    }

    @Test public void testUnknownLeaderboardDefWithCountryCorrect()
    {
        LeaderboardDefDTO leaderboardDefDTO = new LeaderboardDefDTO();
        leaderboardDefDTO.countryCodes = new CountryCodeList();
        leaderboardDefDTO.countryCodes.add("TW");

        List<Integer> icons = leaderboardDefDTOKnowledge.getLeaderboardDefIcon(leaderboardDefDTO);
        assertEquals(
                1,
                icons.size());
        assertEquals(R.drawable.square_tw, (int) icons.get(0));
    }

    @Test public void testUnknownLeaderboardDefWith3CountriesCorrect()
    {
        LeaderboardDefDTO leaderboardDefDTO = new LeaderboardDefDTO();
        leaderboardDefDTO.countryCodes = new CountryCodeList();
        leaderboardDefDTO.countryCodes.add("TW");
        leaderboardDefDTO.countryCodes.add("US");
        leaderboardDefDTO.countryCodes.add("IT");

        List<Integer> icons = leaderboardDefDTOKnowledge.getLeaderboardDefIcon(leaderboardDefDTO);
        assertEquals(
                3,
                icons.size());
        assertEquals(R.drawable.square_tw, (int) icons.get(0));
        assertEquals(R.drawable.square_us, (int) icons.get(1));
        assertEquals(R.drawable.square_it, (int) icons.get(2));
    }
}
