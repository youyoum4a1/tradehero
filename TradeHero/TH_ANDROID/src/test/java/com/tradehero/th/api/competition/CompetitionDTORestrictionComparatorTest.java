package com.ayondo.academy.api.competition;

import com.ayondo.academy.api.leaderboard.def.LeaderboardDefDTO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class CompetitionDTORestrictionComparatorTest
{
    private List<CompetitionDTO> competitionDTOs;
    private SimpleDateFormat dateFormat;

    @Before
    public void setUp()
    {
        competitionDTOs = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    }

    @After
    public void tearDown()
    {
        competitionDTOs = null;
        dateFormat = null;
    }

    @Test public void testShouldReturnCorrectOrder()
    {
        CompetitionDTO mock1 = new CompetitionDTO();
        CompetitionDTO mock2 = new CompetitionDTO();
        CompetitionDTO mock3 = new CompetitionDTO();
        CompetitionDTO mock4 = new CompetitionDTO();
        CompetitionDTO mock5 = new CompetitionDTO();

        mock1.leaderboard = new LeaderboardDefDTO();
        mock2.leaderboard = new LeaderboardDefDTO();
        mock3.leaderboard = new LeaderboardDefDTO();
        mock4.leaderboard = new LeaderboardDefDTO();
        mock5.leaderboard = new LeaderboardDefDTO();

        try
        {
            //Use the id as the expected position
            mock1.id = 0;
            mock1.leaderboard.fromUtcRestricted = dateFormat.parse("2014-06-01 00:00:00");
            mock1.leaderboard.toUtcRestricted = dateFormat.parse("2014-09-30 00:00:00");

            mock2.id = 4;
            mock2.leaderboard.fromUtcRestricted = dateFormat.parse("2014-06-01 00:00:00");
            mock2.leaderboard.toUtcRestricted = dateFormat.parse("2014-06-30 00:00:00");

            mock3.id = 3;
            mock3.leaderboard.fromUtcRestricted = dateFormat.parse("2014-07-01 00:00:00");
            mock3.leaderboard.toUtcRestricted = dateFormat.parse("2014-07-31 00:00:00");

            mock4.id = 2;
            mock4.leaderboard.fromUtcRestricted = dateFormat.parse("2014-08-01 00:00:00");
            mock4.leaderboard.toUtcRestricted = dateFormat.parse("2014-08-31 00:00:00");

            mock5.id = 1;
            mock5.leaderboard.fromUtcRestricted = dateFormat.parse("2014-09-01 00:00:00");
            mock5.leaderboard.toUtcRestricted = dateFormat.parse("2014-09-30 00:00:00");
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        competitionDTOs.add(mock3);
        competitionDTOs.add(mock4);
        competitionDTOs.add(mock2);
        competitionDTOs.add(mock1);
        competitionDTOs.add(mock5);

        Collections.sort(competitionDTOs, new CompetitionDTORestrictionComparator());

        for (int i = 0; i < competitionDTOs.size(); i++)
        {
            CompetitionDTO mock = competitionDTOs.get(i);
            assertThat(mock.id).isEqualTo(i);
        }
    }
}
