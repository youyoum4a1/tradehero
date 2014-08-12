package com.tradehero.th.models.level;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.persistence.level.LevelDefListCache;
import java.util.Random;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class LevelDefUtilTest
{
    @Inject LevelDefUtil levelDefUtil;
    @Inject LevelDefListCache levelDefListCache;

    @Test
    public void testShouldReturnNull()
    {
        int xp = new Random().nextInt(Integer.MAX_VALUE);
        LevelDefDTO levelDefDTO = levelDefUtil.getCurrentLevel(xp);
        assertThat(levelDefDTO).isNull();
        LevelDefDTO nextLevelDefDTO = levelDefUtil.getNextLevelDTO(1);
        assertThat(nextLevelDefDTO).isNull();
    }

    @Test
    public void testShouldReturnCorrectLevel()
    {
        int xp = 2000;

        LevelDefDTOList levelDefDTOList = createMockList();

        levelDefUtil.setAndSortLevelDefDTOList(levelDefDTOList);

        LevelDefDTO current = levelDefUtil.getCurrentLevel(xp);
        assertThat(current).isNotNull();
        assertThat(current.level).isEqualTo(2);
    }

    @Test
    public void testShouldReturnNextLevel()
    {
        int level = 2;

        LevelDefDTOList levelDefDTOList = createMockList();

        levelDefUtil.setAndSortLevelDefDTOList(levelDefDTOList);

        LevelDefDTO next = levelDefUtil.getNextLevelDTO(level);
        assertThat(next).isNotNull();
        assertThat(next.level).isEqualTo(3);
    }

    @Test
    public void testShouldReturnMaxLevel()
    {
        LevelDefDTOList levelDefDTOList = createMockList();

        int level = levelDefDTOList.size();

        levelDefUtil.setAndSortLevelDefDTOList(levelDefDTOList);

        LevelDefDTO max = levelDefUtil.getNextLevelDTO(level);
        assertThat(max).isNotNull();
        assertThat(max.level).isEqualTo(level);
        assertThat(max).isEqualTo(levelDefUtil.getMaxLevelDTO());
    }

    private LevelDefDTOList createMockList()
    {
        LevelDefDTO mockLevel1 = new LevelDefDTO();
        mockLevel1.id = 1;
        mockLevel1.level = 1;
        mockLevel1.xpFrom = 0;
        mockLevel1.xpTo = 1000;

        LevelDefDTO mockLevel2 = new LevelDefDTO();
        mockLevel2.id = 2;
        mockLevel2.level = 2;
        mockLevel2.xpFrom = 1001;
        mockLevel2.xpTo = 2000;

        LevelDefDTO mockLevel3 = new LevelDefDTO();
        mockLevel3.id = 3;
        mockLevel3.level = 3;
        mockLevel3.xpFrom = 2001;
        mockLevel3.xpTo = 3000;

        LevelDefDTOList levelDefDTOList = new LevelDefDTOList();
        levelDefDTOList.add(mockLevel1);
        levelDefDTOList.add(mockLevel3);
        levelDefDTOList.add(mockLevel2);

        return levelDefDTOList;
    }
}
