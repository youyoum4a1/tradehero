package com.ayondo.academy.api.achievement;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class QuestBonusDTOListTest
{
    private QuestBonusDTOList questBonusDTOList;

    @Before
    public void setUp()
    {
        questBonusDTOList = new QuestBonusDTOList();
    }

    @After
    public void tearDown()
    {
        questBonusDTOList = null;
    }

    @Test
    public void testShouldReturnEmptyWhenListIsEmpty() throws Exception
    {
        List<QuestBonusDTO> questBonusDTOList1 = questBonusDTOList.getNextInclusive(1, 5);

        assertThat(questBonusDTOList1).isNotNull();
        assertThat(questBonusDTOList1).isEmpty();
    }

    @Test
    public void testShouldReturnLessThanSizeWhenNextIsGreaterThanSize() throws Exception
    {
        QuestBonusDTO mock1 = new QuestBonusDTO();
        mock1.level = 1;
        mock1.id = 1;
        mock1.bonus = 100;

        QuestBonusDTO mock2 = new QuestBonusDTO();
        mock2.level = 2;
        mock2.id = 2;
        mock2.bonus = 200;

        QuestBonusDTO mock3 = new QuestBonusDTO();
        mock3.level = 3;
        mock3.id = 3;
        mock3.bonus = 300;

        questBonusDTOList.add(mock1);
        questBonusDTOList.add(mock2);
        questBonusDTOList.add(mock3);

        List<QuestBonusDTO> questBonusDTOList1 = questBonusDTOList.getNextInclusive(1, 5);

        assertThat(questBonusDTOList1).isNotNull();
        assertThat(questBonusDTOList1.size()).isEqualTo(3);
    }

    @Test
    public void testShouldReturnNext()
    {
        QuestBonusDTO mock1 = new QuestBonusDTO();
        mock1.level = 1;
        mock1.id = 1;
        mock1.bonus = 100;

        QuestBonusDTO mock2 = new QuestBonusDTO();
        mock2.level = 2;
        mock2.id = 2;
        mock2.bonus = 200;

        QuestBonusDTO mock3 = new QuestBonusDTO();
        mock3.level = 3;
        mock3.id = 3;
        mock3.bonus = 300;

        QuestBonusDTO mock4 = new QuestBonusDTO();
        mock4.level = 4;
        mock4.id = 4;
        mock4.bonus = 400;

        QuestBonusDTO mock5 = new QuestBonusDTO();
        mock5.level = 5;
        mock5.id = 5;
        mock5.bonus = 500;

        questBonusDTOList.add(mock1);
        questBonusDTOList.add(mock2);
        questBonusDTOList.add(mock3);
        questBonusDTOList.add(mock4);
        questBonusDTOList.add(mock5);

        List<QuestBonusDTO> questBonusDTOList1 = questBonusDTOList.getNextInclusive(1, 5);

        assertThat(questBonusDTOList1).isNotNull();
        assertThat(questBonusDTOList1.size()).isEqualTo(5);

        boolean found = contains(questBonusDTOList1, 1);

        assertThat(found).isTrue();
    }

    @Test
    public void testShouldReturnGreaterThanCurrentLevel() throws Exception
    {
        QuestBonusDTO mock1 = new QuestBonusDTO();
        mock1.level = 1;
        mock1.id = 1;
        mock1.bonus = 100;

        QuestBonusDTO mock2 = new QuestBonusDTO();
        mock2.level = 2;
        mock2.id = 2;
        mock2.bonus = 200;

        QuestBonusDTO mock3 = new QuestBonusDTO();
        mock3.level = 4;
        mock3.id = 3;
        mock3.bonus = 300;

        QuestBonusDTO mock4 = new QuestBonusDTO();
        mock4.level = 8;
        mock4.id = 4;
        mock4.bonus = 400;

        QuestBonusDTO mock5 = new QuestBonusDTO();
        mock5.level = 10;
        mock5.id = 5;
        mock5.bonus = 500;

        QuestBonusDTO mock6 = new QuestBonusDTO();
        mock6.level = 15;
        mock6.id = 5;
        mock6.bonus = 500;

        questBonusDTOList.add(mock1);
        questBonusDTOList.add(mock2);
        questBonusDTOList.add(mock3);
        questBonusDTOList.add(mock4);
        questBonusDTOList.add(mock5);
        questBonusDTOList.add(mock6);

        int currentLevel = 2;

        List<QuestBonusDTO> questBonusDTOList1 = questBonusDTOList.getNextInclusive(currentLevel, 5);

        assertThat(questBonusDTOList1).isNotEmpty();
        assertThat(questBonusDTOList1.size()).isEqualTo(5);

        for (QuestBonusDTO questBonusDTO : questBonusDTOList1)
        {
            assertThat(questBonusDTO.level).isGreaterThanOrEqualTo(currentLevel);
            currentLevel = questBonusDTO.level;
        }
    }

    @Test
    public void testShouldReturnEmptyIfNegativeArgs() throws Exception
    {
        QuestBonusDTO mock1 = new QuestBonusDTO();
        mock1.level = 1;
        mock1.id = 1;
        mock1.bonus = 100;

        QuestBonusDTO mock2 = new QuestBonusDTO();
        mock2.level = 2;
        mock2.id = 2;
        mock2.bonus = 200;

        QuestBonusDTO mock3 = new QuestBonusDTO();
        mock3.level = 3;
        mock3.id = 3;
        mock3.bonus = 300;

        questBonusDTOList.add(mock1);
        questBonusDTOList.add(mock2);
        questBonusDTOList.add(mock3);

        List<QuestBonusDTO> questBonusDTOList1 = questBonusDTOList.getNextInclusive(-1, 5);

        assertThat(questBonusDTOList1).isEmpty();

        questBonusDTOList1 = questBonusDTOList.getNextInclusive(1, -5);

        assertThat(questBonusDTOList1).isEmpty();
    }

    @Test
    public void testPreviousShouldReturnNull() throws Exception
    {
        QuestBonusDTO questBonusDTO = questBonusDTOList.getPrevious(1);
        assertThat(questBonusDTO).isNull();

        QuestBonusDTO mock1 = new QuestBonusDTO();
        mock1.level = 1;
        mock1.id = 1;
        mock1.bonus = 100;
        questBonusDTOList.add(mock1);

        questBonusDTO = questBonusDTOList.getPrevious(1);
        assertThat(questBonusDTO).isNull();

        questBonusDTO = questBonusDTOList.getPrevious(-1);
        assertThat(questBonusDTO).isNull();
    }

    @Test
    public void testPreviousShouldRetunCorrectDTO() throws Exception
    {
        QuestBonusDTO mock1 = new QuestBonusDTO();
        mock1.level = 1;
        mock1.id = 1;
        mock1.bonus = 100;

        QuestBonusDTO mock2 = new QuestBonusDTO();
        mock2.level = 2;
        mock2.id = 2;
        mock2.bonus = 200;

        QuestBonusDTO mock3 = new QuestBonusDTO();
        mock3.level = 4;
        mock3.id = 3;
        mock3.bonus = 300;

        questBonusDTOList.add(mock1);
        questBonusDTOList.add(mock2);
        questBonusDTOList.add(mock3);

        QuestBonusDTO questBonusDTO = questBonusDTOList.getPrevious(2);
        assertThat(questBonusDTO).isNotNull();
        assertThat(questBonusDTO.level).isEqualTo(1);

        questBonusDTO = questBonusDTOList.getPrevious(4);
        assertThat(questBonusDTO).isNotNull();
        assertThat(questBonusDTO.level).isEqualTo(2);
    }

    @Test
    public void testGetPreviousShouldNotCrash() throws Exception
    {
        questBonusDTOList.getPrevious(0);

        questBonusDTOList.getPrevious(-2);

        questBonusDTOList.getPrevious(0, 2);

        questBonusDTOList.getPrevious(0, -2);

        questBonusDTOList.getPrevious(0, 0);
    }

    @Test
    public void testGetPreviousShouldReturnCorrectNumOfItems() throws Exception
    {
        QuestBonusDTO mock1 = new QuestBonusDTO();
        mock1.level = 1;
        mock1.id = 1;
        mock1.bonus = 100;

        QuestBonusDTO mock2 = new QuestBonusDTO();
        mock2.level = 2;
        mock2.id = 2;
        mock2.bonus = 200;

        QuestBonusDTO mock3 = new QuestBonusDTO();
        mock3.level = 4;
        mock3.id = 3;
        mock3.bonus = 300;

        questBonusDTOList.add(mock1);
        questBonusDTOList.add(mock2);
        questBonusDTOList.add(mock3);

        List<QuestBonusDTO> questBonusDTO = questBonusDTOList.getPrevious(2, 2);
        assertThat(questBonusDTO).isNotEmpty();
        assertThat(questBonusDTO.size()).isEqualTo(1);

        questBonusDTO = questBonusDTOList.getPrevious(2, 1);
        assertThat(questBonusDTO).isNotEmpty();
        assertThat(questBonusDTO.size()).isEqualTo(1);

        questBonusDTO = questBonusDTOList.getPrevious(1, 1);
        assertThat(questBonusDTO).isEmpty();

        questBonusDTO = questBonusDTOList.getPrevious(4, 2);
        assertThat(questBonusDTO).isNotEmpty();
        assertThat(questBonusDTO.size()).isEqualTo(2);

        questBonusDTO = questBonusDTOList.getPrevious(4, 1);
        assertThat(questBonusDTO).isNotEmpty();
        assertThat(questBonusDTO.size()).isEqualTo(1);

        questBonusDTO = questBonusDTOList.getPrevious(4, 4);
        assertThat(questBonusDTO).isNotEmpty();
        assertThat(questBonusDTO.size()).isEqualTo(2);
    }

    @Test
    public void testGetPreviousShouldAlwaysGetSmallerLevel() throws Exception
    {

    }

    @Test
    public void testGetPreviousReturnsAscendingOrder() throws Exception
    {

    }

    @Test
    public void testGetInclusiveShouldReturn5() throws Exception
    {
        QuestBonusDTO mock1 = new QuestBonusDTO();
        mock1.level = 1;
        mock1.id = 1;
        mock1.bonus = 100;

        QuestBonusDTO mock2 = new QuestBonusDTO();
        mock2.level = 2;
        mock2.id = 2;
        mock2.bonus = 200;

        QuestBonusDTO mock3 = new QuestBonusDTO();
        mock3.level = 4;
        mock3.id = 3;
        mock3.bonus = 300;

        QuestBonusDTO mock4 = new QuestBonusDTO();
        mock4.level = 8;
        mock4.id = 4;
        mock4.bonus = 400;

        QuestBonusDTO mock5 = new QuestBonusDTO();
        mock5.level = 10;
        mock5.id = 5;
        mock5.bonus = 500;

        QuestBonusDTO mock6 = new QuestBonusDTO();
        mock6.level = 15;
        mock6.id = 5;
        mock6.bonus = 500;

        questBonusDTOList.add(mock1);
        questBonusDTOList.add(mock2);
        questBonusDTOList.add(mock3);
        questBonusDTOList.add(mock4);
        questBonusDTOList.add(mock5);
        questBonusDTOList.add(mock6);

        List<QuestBonusDTO> questBonusDTOList1 = questBonusDTOList.getInclusive(1, 5);
        assertThat(questBonusDTOList1.size()).isEqualTo(5);

        questBonusDTOList1 = questBonusDTOList.getInclusive(4, 5);
        assertThat(questBonusDTOList1.size()).isEqualTo(5);

        questBonusDTOList1 = questBonusDTOList.getInclusive(10, 5);
        assertThat(questBonusDTOList1.size()).isEqualTo(5);
    }

    @Test
    public void testGetInclusiveShouldReturnSize() throws Exception
    {
        QuestBonusDTO mock1 = new QuestBonusDTO();
        mock1.level = 1;
        mock1.id = 1;
        mock1.bonus = 100;

        QuestBonusDTO mock2 = new QuestBonusDTO();
        mock2.level = 2;
        mock2.id = 2;
        mock2.bonus = 200;

        QuestBonusDTO mock3 = new QuestBonusDTO();
        mock3.level = 4;
        mock3.id = 3;
        mock3.bonus = 300;

        questBonusDTOList.add(mock1);
        questBonusDTOList.add(mock2);
        questBonusDTOList.add(mock3);

        List<QuestBonusDTO> questBonusDTOList1 = questBonusDTOList.getInclusive(1, 5);
        assertThat(questBonusDTOList1.size()).isEqualTo(3);
    }

    @Test
    public void testGetInclusiveShouldGetCurrentLevel() throws Exception
    {
        QuestBonusDTO mock1 = new QuestBonusDTO();
        mock1.level = 1;
        mock1.id = 1;
        mock1.bonus = 100;

        QuestBonusDTO mock2 = new QuestBonusDTO();
        mock2.level = 2;
        mock2.id = 2;
        mock2.bonus = 200;

        QuestBonusDTO mock3 = new QuestBonusDTO();
        mock3.level = 4;
        mock3.id = 3;
        mock3.bonus = 300;

        QuestBonusDTO mock4 = new QuestBonusDTO();
        mock4.level = 8;
        mock4.id = 4;
        mock4.bonus = 400;

        QuestBonusDTO mock5 = new QuestBonusDTO();
        mock5.level = 10;
        mock5.id = 5;
        mock5.bonus = 500;

        QuestBonusDTO mock6 = new QuestBonusDTO();
        mock6.level = 15;
        mock6.id = 5;
        mock6.bonus = 500;

        questBonusDTOList.add(mock1);
        questBonusDTOList.add(mock2);
        questBonusDTOList.add(mock3);
        questBonusDTOList.add(mock4);
        questBonusDTOList.add(mock5);
        questBonusDTOList.add(mock6);

        List<QuestBonusDTO> questBonusDTOList1 = questBonusDTOList.getInclusive(1, 5);
        assertThat(questBonusDTOList1.size()).isEqualTo(5);

        boolean found = contains(questBonusDTOList1, 1);
        assertThat(found).isTrue();

        questBonusDTOList1 = questBonusDTOList.getInclusive(15, 5);
        found = contains(questBonusDTOList1, 15);
        assertThat(found).isTrue();
    }

    @Test
    public void testGetInclusiveReturnCurrentLevelInCorrectIndex() throws Exception
    {
        QuestBonusDTO mock1 = new QuestBonusDTO();
        mock1.level = 1;
        mock1.id = 1;
        mock1.bonus = 100;

        QuestBonusDTO mock2 = new QuestBonusDTO();
        mock2.level = 2;
        mock2.id = 2;
        mock2.bonus = 200;

        QuestBonusDTO mock3 = new QuestBonusDTO();
        mock3.level = 4;
        mock3.id = 3;
        mock3.bonus = 300;

        QuestBonusDTO mock4 = new QuestBonusDTO();
        mock4.level = 8;
        mock4.id = 4;
        mock4.bonus = 400;

        QuestBonusDTO mock5 = new QuestBonusDTO();
        mock5.level = 10;
        mock5.id = 5;
        mock5.bonus = 500;

        QuestBonusDTO mock6 = new QuestBonusDTO();
        mock6.level = 15;
        mock6.id = 5;
        mock6.bonus = 500;

        questBonusDTOList.add(mock1);
        questBonusDTOList.add(mock2);
        questBonusDTOList.add(mock3);
        questBonusDTOList.add(mock4);
        questBonusDTOList.add(mock5);
        questBonusDTOList.add(mock6);

        List<QuestBonusDTO> questBonusDTOList1 = questBonusDTOList.getInclusive(1, 5);
        assertThat(questBonusDTOList1.get(0).level).isEqualTo(1);

        questBonusDTOList1 = questBonusDTOList.getInclusive(2, 5);
        assertThat(questBonusDTOList1.get(0).level).isEqualTo(2);

        questBonusDTOList1 = questBonusDTOList.getInclusive(4, 5);
        assertThat(questBonusDTOList1.get(1).level).isEqualTo(4);

        questBonusDTOList1 = questBonusDTOList.getInclusive(8, 5);
        assertThat(questBonusDTOList1.get(2).level).isEqualTo(8);

        questBonusDTOList1 = questBonusDTOList.getInclusive(10, 5);
        assertThat(questBonusDTOList1.get(3).level).isEqualTo(10);

        questBonusDTOList1 = questBonusDTOList.getInclusive(15, 5);
        assertThat(questBonusDTOList1.get(4).level).isEqualTo(15);
    }

    private boolean contains(List<QuestBonusDTO> questBonusDTOs, int level)
    {
        boolean found = false;
        for (QuestBonusDTO questBonusDTO : questBonusDTOs)
        {
            if (questBonusDTO.level == level)
            {
                found = true;
            }
        }
        return found;
    }
}
