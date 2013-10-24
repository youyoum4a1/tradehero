package com.tradehero.th.api.portfolio;

import com.tradehero.th.api.users.UserBaseDTO;
import java.sql.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/** Created with IntelliJ IDEA. User: xavier Date: 10/24/13 Time: 7:53 PM To change this template use File | Settings | File Templates. */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class PortfolioDTOComparableTest
{
    public static final String TAG = PortfolioDTOComparableTest.class.getSimpleName();

    @Before public void setUp()
    {
        PortfolioDTO.currentUserBase = getCurrentUser();
    }

    @After public void tearDown()
    {
        PortfolioDTO.currentUserBase = null;
    }

    private UserBaseDTO getCurrentUser()
    {
        UserBaseDTO currentUser = new UserBaseDTO();
        currentUser.firstName = "iamcurrent";
        currentUser.id = 10;
        return currentUser;
    }

    private UserBaseDTO getOtherUser1()
    {
        UserBaseDTO otherUser = new UserBaseDTO();
        otherUser.firstName = "user1";
        otherUser.id = 5;
        return otherUser;
    }

    private UserBaseDTO getOtherUser2()
    {
        UserBaseDTO otherUser = new UserBaseDTO();
        otherUser.firstName = "user2";
        otherUser.id = 15;
        return otherUser;
    }

    private PortfolioDTO getNullUserPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(null);
        portfolioDTO.title = "userNull";
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserDefaultPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getCurrentUser());
        portfolioDTO.title = PortfolioCompactDTO.DEFAULT_TITLE;
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserNullCreationPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getCurrentUser());
        portfolioDTO.title = "currentUser NullCreation";
        portfolioDTO.creationDate = null;
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserOlderPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getCurrentUser());
        portfolioDTO.title = "CurrentUser Older";
        portfolioDTO.creationDate = new Date(2013, 1, 1);
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserYoungerPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getCurrentUser());
        portfolioDTO.title = "CurrentUser Younger";
        portfolioDTO.creationDate = new Date(2013, 1, 2);
        return portfolioDTO;
    }

    private PortfolioDTO getUser1NullDatePortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getOtherUser1());
        portfolioDTO.title = "User1 NullCreation";
        return portfolioDTO;
    }

    private PortfolioDTO getUser1OlderPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getOtherUser1());
        portfolioDTO.title = "User1 Older";
        portfolioDTO.creationDate = new Date(2013, 1, 1);
        return portfolioDTO;
    }

    private PortfolioDTO getUser1YoungerPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getOtherUser1());
        portfolioDTO.title = "User1 Younger";
        portfolioDTO.creationDate = new Date(2013, 1, 5);
        return portfolioDTO;
    }

    private PortfolioDTO getUser2NullDatePortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getOtherUser2());
        portfolioDTO.title = "User2 NullCreation";
        return portfolioDTO;
    }

    private PortfolioDTO getUser2OlderPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getOtherUser2());
        portfolioDTO.title = "User2 Older";
        portfolioDTO.creationDate = new Date(2013, 1, 1);
        return portfolioDTO;
    }

    private PortfolioDTO getUser2YoungerPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getOtherUser2());
        portfolioDTO.title = "User2 Younger";
        portfolioDTO.creationDate = new Date(2013, 1, 5);
        return portfolioDTO;
    }

    @Test public void currentUserShouldBeEqualToItself()
    {
        assertTrue(getCurrentUser().equals(getCurrentUser()));
    }

    @Test public void currentUserShouldBeDifferentFromOthers()
    {
        assertFalse(getCurrentUser().equals(getOtherUser1()));
        assertFalse(getCurrentUser().equals(getOtherUser2()));
    }

    @Test public void compareShouldReturnZeroOnSame()
    {
        assertThat(getNullUserPortfolio().compareTo(getNullUserPortfolio()), equalTo(0));
        assertThat(getCurrentUserDefaultPortfolio().compareTo(getCurrentUserDefaultPortfolio()), equalTo(0));
        assertThat(getCurrentUserNullCreationPortfolio().compareTo(getCurrentUserNullCreationPortfolio()), equalTo(0));
        assertThat(getCurrentUserOlderPortfolio().compareTo(getCurrentUserOlderPortfolio()), equalTo(0));
        assertThat(getCurrentUserYoungerPortfolio().compareTo(getCurrentUserYoungerPortfolio()), equalTo(0));
        assertThat(getUser1NullDatePortfolio().compareTo(getUser1NullDatePortfolio()), equalTo(0));
        assertThat(getUser1OlderPortfolio().compareTo(getUser1OlderPortfolio()), equalTo(0));
        assertThat(getUser1YoungerPortfolio().compareTo(getUser1YoungerPortfolio()), equalTo(0));
        assertThat(getUser2NullDatePortfolio().compareTo(getUser2NullDatePortfolio()), equalTo(0));
        assertThat(getUser2OlderPortfolio().compareTo(getUser2OlderPortfolio()), equalTo(0));
        assertThat(getUser2YoungerPortfolio().compareTo(getUser2YoungerPortfolio()), equalTo(0));
    }

    @Test public void compareNullUserToOthers()
    {
        assertThat(getNullUserPortfolio().compareTo(getNullUserPortfolio()), equalTo(0));
        assertThat(getNullUserPortfolio().compareTo(getCurrentUserDefaultPortfolio()), equalTo(-1));
        assertThat(getNullUserPortfolio().compareTo(getCurrentUserNullCreationPortfolio()), equalTo(-1));
        assertThat(getNullUserPortfolio().compareTo(getCurrentUserOlderPortfolio()), equalTo(-1));
        assertThat(getNullUserPortfolio().compareTo(getCurrentUserYoungerPortfolio()), equalTo(-1));
        assertThat(getNullUserPortfolio().compareTo(getUser1NullDatePortfolio()), equalTo(-1));
        assertThat(getNullUserPortfolio().compareTo(getUser1OlderPortfolio()), equalTo(-1));
        assertThat(getNullUserPortfolio().compareTo(getUser1YoungerPortfolio()), equalTo(-1));
        assertThat(getNullUserPortfolio().compareTo(getUser2NullDatePortfolio()), equalTo(-1));
        assertThat(getNullUserPortfolio().compareTo(getUser2OlderPortfolio()), equalTo(-1));
        assertThat(getNullUserPortfolio().compareTo(getUser2YoungerPortfolio()), equalTo(-1));
    }

    @Test public void compareCurrentUserDefaultToOthers()
    {
        assertThat(getCurrentUserDefaultPortfolio().compareTo(getNullUserPortfolio()), equalTo(1));
        assertThat(getCurrentUserDefaultPortfolio().compareTo(getCurrentUserDefaultPortfolio()), equalTo(0));
        assertThat(getCurrentUserDefaultPortfolio().compareTo(getCurrentUserNullCreationPortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultPortfolio().compareTo(getCurrentUserOlderPortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultPortfolio().compareTo(getCurrentUserYoungerPortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultPortfolio().compareTo(getUser1NullDatePortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultPortfolio().compareTo(getUser1OlderPortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultPortfolio().compareTo(getUser1YoungerPortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultPortfolio().compareTo(getUser2NullDatePortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultPortfolio().compareTo(getUser2OlderPortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultPortfolio().compareTo(getUser2YoungerPortfolio()), equalTo(-1));
    }

    @Test public void compareCurrentUserNullCreationToOthers()
    {
        assertThat(getCurrentUserNullCreationPortfolio().compareTo(getNullUserPortfolio()), equalTo(1));
        assertThat(getCurrentUserNullCreationPortfolio().compareTo(getCurrentUserDefaultPortfolio()), equalTo(1));
        assertThat(getCurrentUserNullCreationPortfolio().compareTo(getCurrentUserNullCreationPortfolio()), equalTo(0));
        assertThat(getCurrentUserNullCreationPortfolio().compareTo(getCurrentUserOlderPortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationPortfolio().compareTo(getCurrentUserYoungerPortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationPortfolio().compareTo(getUser1NullDatePortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationPortfolio().compareTo(getUser1OlderPortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationPortfolio().compareTo(getUser1YoungerPortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationPortfolio().compareTo(getUser2NullDatePortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationPortfolio().compareTo(getUser2OlderPortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationPortfolio().compareTo(getUser2YoungerPortfolio()), equalTo(-1));
    }

    @Test public void compareCurrentUserOlderToOthers()
    {
        assertThat(getCurrentUserOlderPortfolio().compareTo(getNullUserPortfolio()), equalTo(1));
        assertThat(getCurrentUserOlderPortfolio().compareTo(getCurrentUserDefaultPortfolio()), equalTo(1));
        assertThat(getCurrentUserOlderPortfolio().compareTo(getCurrentUserNullCreationPortfolio()), equalTo(1));
        assertThat(getCurrentUserOlderPortfolio().compareTo(getCurrentUserOlderPortfolio()), equalTo(0));
        assertThat(getCurrentUserOlderPortfolio().compareTo(getCurrentUserYoungerPortfolio()), equalTo(-1));
        assertThat(getCurrentUserOlderPortfolio().compareTo(getUser1NullDatePortfolio()), equalTo(-1));
        assertThat(getCurrentUserOlderPortfolio().compareTo(getUser1OlderPortfolio()), equalTo(-1));
        assertThat(getCurrentUserOlderPortfolio().compareTo(getUser1YoungerPortfolio()), equalTo(-1));
        assertThat(getCurrentUserOlderPortfolio().compareTo(getUser2NullDatePortfolio()), equalTo(-1));
        assertThat(getCurrentUserOlderPortfolio().compareTo(getUser2OlderPortfolio()), equalTo(-1));
        assertThat(getCurrentUserOlderPortfolio().compareTo(getUser2YoungerPortfolio()), equalTo(-1));
    }

    @Test public void compareCurrentUserYoungerToOthers()
    {
        assertThat(getCurrentUserYoungerPortfolio().compareTo(getNullUserPortfolio()), equalTo(1));
        assertThat(getCurrentUserYoungerPortfolio().compareTo(getCurrentUserDefaultPortfolio()), equalTo(1));
        assertThat(getCurrentUserYoungerPortfolio().compareTo(getCurrentUserNullCreationPortfolio()), equalTo(1));
        assertThat(getCurrentUserYoungerPortfolio().compareTo(getCurrentUserOlderPortfolio()), equalTo(1));
        assertThat(getCurrentUserYoungerPortfolio().compareTo(getCurrentUserYoungerPortfolio()), equalTo(0));
        assertThat(getCurrentUserYoungerPortfolio().compareTo(getUser1NullDatePortfolio()), equalTo(-1));
        assertThat(getCurrentUserYoungerPortfolio().compareTo(getUser1OlderPortfolio()), equalTo(-1));
        assertThat(getCurrentUserYoungerPortfolio().compareTo(getUser1YoungerPortfolio()), equalTo(-1));
        assertThat(getCurrentUserYoungerPortfolio().compareTo(getUser2NullDatePortfolio()), equalTo(-1));
        assertThat(getCurrentUserYoungerPortfolio().compareTo(getUser2OlderPortfolio()), equalTo(-1));
        assertThat(getCurrentUserYoungerPortfolio().compareTo(getUser2YoungerPortfolio()), equalTo(-1));
    }

    @Test public void compareUser1NullDateToOthers()
    {
        assertThat(getUser1NullDatePortfolio().compareTo(getNullUserPortfolio()), equalTo(1));
        assertThat(getUser1NullDatePortfolio().compareTo(getCurrentUserDefaultPortfolio()), equalTo(1));
        assertThat(getUser1NullDatePortfolio().compareTo(getCurrentUserNullCreationPortfolio()), equalTo(1));
        assertThat(getUser1NullDatePortfolio().compareTo(getCurrentUserOlderPortfolio()), equalTo(1));
        assertThat(getUser1NullDatePortfolio().compareTo(getCurrentUserYoungerPortfolio()), equalTo(1));
        assertThat(getUser1NullDatePortfolio().compareTo(getUser1NullDatePortfolio()), equalTo(0));
        assertThat(getUser1NullDatePortfolio().compareTo(getUser1OlderPortfolio()), equalTo(-1));
        assertThat(getUser1NullDatePortfolio().compareTo(getUser1YoungerPortfolio()), equalTo(-1));
        assertThat(getUser1NullDatePortfolio().compareTo(getUser2NullDatePortfolio()), equalTo(-1));
        assertThat(getUser1NullDatePortfolio().compareTo(getUser2OlderPortfolio()), equalTo(-1));
        assertThat(getUser1NullDatePortfolio().compareTo(getUser2YoungerPortfolio()), equalTo(-1));
    }

    @Test public void compareUser1OlderToOthers()
    {
        assertThat(getUser1OlderPortfolio().compareTo(getNullUserPortfolio()), equalTo(1));
        assertThat(getUser1OlderPortfolio().compareTo(getCurrentUserDefaultPortfolio()), equalTo(1));
        assertThat(getUser1OlderPortfolio().compareTo(getCurrentUserNullCreationPortfolio()), equalTo(1));
        assertThat(getUser1OlderPortfolio().compareTo(getCurrentUserOlderPortfolio()), equalTo(1));
        assertThat(getUser1OlderPortfolio().compareTo(getCurrentUserYoungerPortfolio()), equalTo(1));
        assertThat(getUser1OlderPortfolio().compareTo(getUser1NullDatePortfolio()), equalTo(1));
        assertThat(getUser1OlderPortfolio().compareTo(getUser1OlderPortfolio()), equalTo(0));
        assertThat(getUser1OlderPortfolio().compareTo(getUser1YoungerPortfolio()), equalTo(-1));
        assertThat(getUser1OlderPortfolio().compareTo(getUser2NullDatePortfolio()), equalTo(-1));
        assertThat(getUser1OlderPortfolio().compareTo(getUser2OlderPortfolio()), equalTo(-1));
        assertThat(getUser1OlderPortfolio().compareTo(getUser2YoungerPortfolio()), equalTo(-1));
    }

    @Test public void compareUser1YoungerToOthers()
    {
        assertThat(getUser1YoungerPortfolio().compareTo(getNullUserPortfolio()), equalTo(1));
        assertThat(getUser1YoungerPortfolio().compareTo(getCurrentUserDefaultPortfolio()), equalTo(1));
        assertThat(getUser1YoungerPortfolio().compareTo(getCurrentUserNullCreationPortfolio()), equalTo(1));
        assertThat(getUser1YoungerPortfolio().compareTo(getCurrentUserOlderPortfolio()), equalTo(1));
        assertThat(getUser1YoungerPortfolio().compareTo(getCurrentUserYoungerPortfolio()), equalTo(1));
        assertThat(getUser1YoungerPortfolio().compareTo(getUser1NullDatePortfolio()), equalTo(1));
        assertThat(getUser1YoungerPortfolio().compareTo(getUser1OlderPortfolio()), equalTo(1));
        assertThat(getUser1YoungerPortfolio().compareTo(getUser1YoungerPortfolio()), equalTo(0));
        assertThat(getUser1YoungerPortfolio().compareTo(getUser2NullDatePortfolio()), equalTo(-1));
        assertThat(getUser1YoungerPortfolio().compareTo(getUser2OlderPortfolio()), equalTo(-1));
        assertThat(getUser1YoungerPortfolio().compareTo(getUser2YoungerPortfolio()), equalTo(-1));
    }

    @Test public void compareUser2NullDateToOthers()
    {
        assertThat(getUser2NullDatePortfolio().compareTo(getNullUserPortfolio()), equalTo(1));
        assertThat(getUser2NullDatePortfolio().compareTo(getCurrentUserDefaultPortfolio()), equalTo(1));
        assertThat(getUser2NullDatePortfolio().compareTo(getCurrentUserNullCreationPortfolio()), equalTo(1));
        assertThat(getUser2NullDatePortfolio().compareTo(getCurrentUserOlderPortfolio()), equalTo(1));
        assertThat(getUser2NullDatePortfolio().compareTo(getCurrentUserYoungerPortfolio()), equalTo(1));
        assertThat(getUser2NullDatePortfolio().compareTo(getUser1NullDatePortfolio()), equalTo(1));
        assertThat(getUser2NullDatePortfolio().compareTo(getUser1OlderPortfolio()), equalTo(1));
        assertThat(getUser2NullDatePortfolio().compareTo(getUser1YoungerPortfolio()), equalTo(1));
        assertThat(getUser2NullDatePortfolio().compareTo(getUser2NullDatePortfolio()), equalTo(0));
        assertThat(getUser2NullDatePortfolio().compareTo(getUser2OlderPortfolio()), equalTo(-1));
        assertThat(getUser2NullDatePortfolio().compareTo(getUser2YoungerPortfolio()), equalTo(-1));
    }

    @Test public void compareUser2OlderToOthers()
    {
        assertThat(getUser2OlderPortfolio().compareTo(getNullUserPortfolio()), equalTo(1));
        assertThat(getUser2OlderPortfolio().compareTo(getCurrentUserDefaultPortfolio()), equalTo(1));
        assertThat(getUser2OlderPortfolio().compareTo(getCurrentUserNullCreationPortfolio()), equalTo(1));
        assertThat(getUser2OlderPortfolio().compareTo(getCurrentUserOlderPortfolio()), equalTo(1));
        assertThat(getUser2OlderPortfolio().compareTo(getCurrentUserYoungerPortfolio()), equalTo(1));
        assertThat(getUser2OlderPortfolio().compareTo(getUser1NullDatePortfolio()), equalTo(1));
        assertThat(getUser2OlderPortfolio().compareTo(getUser1OlderPortfolio()), equalTo(1));
        assertThat(getUser2OlderPortfolio().compareTo(getUser1YoungerPortfolio()), equalTo(1));
        assertThat(getUser2OlderPortfolio().compareTo(getUser2NullDatePortfolio()), equalTo(1));
        assertThat(getUser2OlderPortfolio().compareTo(getUser2OlderPortfolio()), equalTo(0));
        assertThat(getUser2OlderPortfolio().compareTo(getUser2YoungerPortfolio()), equalTo(-1));
    }

    @Test public void compareUser2YoungerToOthers()
    {
        assertThat(getUser2YoungerPortfolio().compareTo(getNullUserPortfolio()), equalTo(1));
        assertThat(getUser2YoungerPortfolio().compareTo(getCurrentUserDefaultPortfolio()), equalTo(1));
        assertThat(getUser2YoungerPortfolio().compareTo(getCurrentUserNullCreationPortfolio()), equalTo(1));
        assertThat(getUser2YoungerPortfolio().compareTo(getCurrentUserOlderPortfolio()), equalTo(1));
        assertThat(getUser2YoungerPortfolio().compareTo(getCurrentUserYoungerPortfolio()), equalTo(1));
        assertThat(getUser2YoungerPortfolio().compareTo(getUser1NullDatePortfolio()), equalTo(1));
        assertThat(getUser2YoungerPortfolio().compareTo(getUser1OlderPortfolio()), equalTo(1));
        assertThat(getUser2YoungerPortfolio().compareTo(getUser1YoungerPortfolio()), equalTo(1));
        assertThat(getUser2YoungerPortfolio().compareTo(getUser2NullDatePortfolio()), equalTo(1));
        assertThat(getUser2YoungerPortfolio().compareTo(getUser2OlderPortfolio()), equalTo(1));
        assertThat(getUser2YoungerPortfolio().compareTo(getUser2YoungerPortfolio()), equalTo(0));
    }

    @Test public void shouldOrderOldYoung()
    {
        Set<PortfolioDTO> treeSet = new TreeSet<>();
        treeSet.add(getCurrentUserOlderPortfolio());
        treeSet.add(getCurrentUserYoungerPortfolio());

        assertThat(treeSet.size(), equalTo(2));

        Iterator<PortfolioDTO> iterator = treeSet.iterator();
        assertThat(iterator.next().compareTo(getCurrentUserOlderPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getCurrentUserYoungerPortfolio()), equalTo(0));
    }

    @Test public void shouldOrderAsExpected()
    {
        Set<PortfolioDTO> treeSet = new TreeSet<>();

        treeSet.add(getUser1YoungerPortfolio());
        treeSet.add(getUser2YoungerPortfolio());
        treeSet.add(getUser1NullDatePortfolio());
        treeSet.add(getNullUserPortfolio());
        treeSet.add(getCurrentUserNullCreationPortfolio());
        treeSet.add(getUser2NullDatePortfolio());
        treeSet.add(getCurrentUserYoungerPortfolio());
        treeSet.add(getCurrentUserDefaultPortfolio());
        treeSet.add(getUser1OlderPortfolio());
        treeSet.add(getUser2OlderPortfolio());
        treeSet.add(getCurrentUserOlderPortfolio());

        assertThat(treeSet.size(), equalTo(11));

        Iterator<PortfolioDTO> iterator = treeSet.iterator();
        assertThat(iterator.next().compareTo(getNullUserPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getCurrentUserDefaultPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getCurrentUserNullCreationPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getCurrentUserOlderPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getCurrentUserYoungerPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser1NullDatePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser1OlderPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser1YoungerPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser2NullDatePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser2OlderPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser2YoungerPortfolio()), equalTo(0));
    }
}
