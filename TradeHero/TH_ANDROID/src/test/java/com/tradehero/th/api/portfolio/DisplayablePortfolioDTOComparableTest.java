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
public class DisplayablePortfolioDTOComparableTest
{
    public static final String TAG = DisplayablePortfolioDTOComparableTest.class.getSimpleName();

    @Before public void setUp()
    {
        DisplayablePortfolioDTO.currentUserBase = getCurrentUser();
    }

    @After public void tearDown()
    {
        DisplayablePortfolioDTO.currentUserBase = null;
    }

    /**
     * User ids:
     * user1: 5
     * current: 10
     * user2: 15
     */
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

    /**
     * Portfolio ids:
     * current user default: 5
     * current user null date: 10
     * current user older: 15
     * current user younger: 20
     * user1 null date: 25
     * user1 older: 30
     * user1 younger: 35
     * user2 null date: 40
     * user2 older: 45
     * user2 younger: 50
     * null user: 55
     * @return
     */
    private OwnedPortfolioId getCurrentUserDefaultPortfolioId()
    {
        return new OwnedPortfolioId(10, 5);
    }

    private OwnedPortfolioId getCurrentUserNullDatePortfolioId()
    {
        return new OwnedPortfolioId(10, 10);
    }

    private OwnedPortfolioId getCurrentUserOlderPortfolioId()
    {
        return new OwnedPortfolioId(10, 15);
    }

    private OwnedPortfolioId getCurrentUserYoungerPortfolioId()
    {
        return new OwnedPortfolioId(10, 20);
    }

    private OwnedPortfolioId getUser1NullDatePortfolioId()
    {
        return new OwnedPortfolioId(5, 25);
    }

    private OwnedPortfolioId getUser1OlderPortfolioId()
    {
        return new OwnedPortfolioId(5, 30);
    }

    private OwnedPortfolioId getUser1YoungerPortfolioId()
    {
        return new OwnedPortfolioId(5, 35);
    }

    private OwnedPortfolioId getUser2NullDatePortfolioId()
    {
        return new OwnedPortfolioId(15, 40);
    }

    private OwnedPortfolioId getUser2OlderPortfolioId()
    {
        return new OwnedPortfolioId(15, 45);
    }

    private OwnedPortfolioId getUser2YoungerPortfolioId()
    {
        return new OwnedPortfolioId(15, 50);
    }

    private PortfolioDTO getNullUserPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 55;
        portfolioDTO.title = "userNull";
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserDefaultPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 5;
        portfolioDTO.title = PortfolioCompactDTO.DEFAULT_TITLE;
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserNullCreationPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 10;
        portfolioDTO.title = "currentUser NullCreation";
        portfolioDTO.creationDate = null;
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserOlderPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 15;
        portfolioDTO.title = "CurrentUser Older";
        portfolioDTO.creationDate = new Date(2013, 1, 1);
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserYoungerPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 20;
        portfolioDTO.title = "CurrentUser Younger";
        portfolioDTO.creationDate = new Date(2013, 1, 2);
        return portfolioDTO;
    }

    private PortfolioDTO getUser1NullDatePortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 25;
        portfolioDTO.title = "User1 NullCreation";
        return portfolioDTO;
    }

    private PortfolioDTO getUser1OlderPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 30;
        portfolioDTO.title = "User1 Older";
        portfolioDTO.creationDate = new Date(2013, 1, 1);
        return portfolioDTO;
    }

    private PortfolioDTO getUser1YoungerPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 35;
        portfolioDTO.title = "User1 Younger";
        portfolioDTO.creationDate = new Date(2013, 1, 5);
        return portfolioDTO;
    }

    private PortfolioDTO getUser2NullDatePortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 40;
        portfolioDTO.title = "User2 NullCreation";
        return portfolioDTO;
    }

    private PortfolioDTO getUser2OlderPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 45;
        portfolioDTO.title = "User2 Older";
        portfolioDTO.creationDate = new Date(2013, 1, 1);
        return portfolioDTO;
    }

    private PortfolioDTO getUser2YoungerPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 50;
        portfolioDTO.title = "User2 Younger";
        portfolioDTO.creationDate = new Date(2013, 1, 5);
        return portfolioDTO;
    }

    private DisplayablePortfolioDTO getNullUserDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(null, null, getNullUserPortfolio());
    }

    private DisplayablePortfolioDTO getCurrentUserDefaultDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getCurrentUserDefaultPortfolioId(), getCurrentUser(), getCurrentUserDefaultPortfolio());
    }

    private DisplayablePortfolioDTO getCurrentUserNullCreationDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getCurrentUserNullDatePortfolioId(), getCurrentUser(), getCurrentUserNullCreationPortfolio());
    }

    private DisplayablePortfolioDTO getCurrentUserOlderDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getCurrentUserOlderPortfolioId(), getCurrentUser(), getCurrentUserOlderPortfolio());
    }

    private DisplayablePortfolioDTO getCurrentUserYoungerDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getCurrentUserYoungerPortfolioId(), getCurrentUser(), getCurrentUserYoungerPortfolio());
    }

    private DisplayablePortfolioDTO getUser1NullDateDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getUser1NullDatePortfolioId(), getOtherUser1(), getUser1NullDatePortfolio());
    }

    private DisplayablePortfolioDTO getUser1OlderDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getUser1OlderPortfolioId(), getOtherUser1(), getUser1OlderPortfolio());
    }

    private DisplayablePortfolioDTO getUser1YoungerDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getUser1YoungerPortfolioId(), getOtherUser1(), getUser1YoungerPortfolio());
    }

    private DisplayablePortfolioDTO getUser2NullDateDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getUser2NullDatePortfolioId(), getOtherUser2(), getUser2NullDatePortfolio());
    }

    private DisplayablePortfolioDTO getUser2OlderDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getUser2OlderPortfolioId(), getOtherUser2(), getUser2OlderPortfolio());
    }

    private DisplayablePortfolioDTO getUser2YoungerDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getUser2YoungerPortfolioId(), getOtherUser2(), getUser2YoungerPortfolio());
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
        assertThat(getNullUserDisplayablePortfolio().compareTo(getNullUserDisplayablePortfolio()), equalTo(0));
        assertThat(getCurrentUserDefaultDisplayablePortfolio().compareTo(getCurrentUserDefaultDisplayablePortfolio()), equalTo(0));
        assertThat(getCurrentUserNullCreationDisplayablePortfolio().compareTo(getCurrentUserNullCreationDisplayablePortfolio()), equalTo(0));
        assertThat(getCurrentUserOlderDisplayablePortfolio().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(0));
        assertThat(getCurrentUserYoungerDisplayablePortfolio().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(0));
        assertThat(getUser1NullDateDisplayablePortfolio().compareTo(getUser1NullDateDisplayablePortfolio()), equalTo(0));
        assertThat(getUser1OlderDisplayablePortfolio().compareTo(getUser1OlderDisplayablePortfolio()), equalTo(0));
        assertThat(getUser1YoungerDisplayablePortfolio().compareTo(getUser1YoungerDisplayablePortfolio()), equalTo(0));
        assertThat(getUser2NullDateDisplayablePortfolio().compareTo(getUser2NullDateDisplayablePortfolio()), equalTo(0));
        assertThat(getUser2OlderDisplayablePortfolio().compareTo(getUser2OlderDisplayablePortfolio()), equalTo(0));
        assertThat(getUser2YoungerDisplayablePortfolio().compareTo(getUser2YoungerDisplayablePortfolio()), equalTo(0));
    }

    @Test public void compareNullUserToOthers()
    {
        assertThat(getNullUserDisplayablePortfolio().compareTo(getNullUserDisplayablePortfolio()), equalTo(0));
        assertThat(getNullUserDisplayablePortfolio().compareTo(getCurrentUserDefaultDisplayablePortfolio()), equalTo(-1));
        assertThat(getNullUserDisplayablePortfolio().compareTo(getCurrentUserNullCreationDisplayablePortfolio()), equalTo(-1));
        assertThat(getNullUserDisplayablePortfolio().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getNullUserDisplayablePortfolio().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(-1));
        assertThat(getNullUserDisplayablePortfolio().compareTo(getUser1NullDateDisplayablePortfolio()), equalTo(-1));
        assertThat(getNullUserDisplayablePortfolio().compareTo(getUser1OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getNullUserDisplayablePortfolio().compareTo(getUser1YoungerDisplayablePortfolio()), equalTo(-1));
        assertThat(getNullUserDisplayablePortfolio().compareTo(getUser2NullDateDisplayablePortfolio()), equalTo(-1));
        assertThat(getNullUserDisplayablePortfolio().compareTo(getUser2OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getNullUserDisplayablePortfolio().compareTo(getUser2YoungerDisplayablePortfolio()), equalTo(-1));
    }

    @Test public void compareCurrentUserDefaultToOthers()
    {
        assertThat(getCurrentUserDefaultDisplayablePortfolio().compareTo(getNullUserDisplayablePortfolio()), equalTo(1));
        assertThat(getCurrentUserDefaultDisplayablePortfolio().compareTo(getCurrentUserDefaultDisplayablePortfolio()), equalTo(0));
        assertThat(getCurrentUserDefaultDisplayablePortfolio().compareTo(getCurrentUserNullCreationDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultDisplayablePortfolio().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultDisplayablePortfolio().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultDisplayablePortfolio().compareTo(getUser1NullDateDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultDisplayablePortfolio().compareTo(getUser1OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultDisplayablePortfolio().compareTo(getUser1YoungerDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultDisplayablePortfolio().compareTo(getUser2NullDateDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultDisplayablePortfolio().compareTo(getUser2OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserDefaultDisplayablePortfolio().compareTo(getUser2YoungerDisplayablePortfolio()), equalTo(-1));
    }

    @Test public void compareCurrentUserNullCreationToOthers()
    {
        assertThat(getCurrentUserNullCreationDisplayablePortfolio().compareTo(getNullUserDisplayablePortfolio()), equalTo(1));
        assertThat(getCurrentUserNullCreationDisplayablePortfolio().compareTo(getCurrentUserDefaultDisplayablePortfolio()), equalTo(1));
        assertThat(getCurrentUserNullCreationDisplayablePortfolio().compareTo(getCurrentUserNullCreationDisplayablePortfolio()), equalTo(0));
        assertThat(getCurrentUserNullCreationDisplayablePortfolio().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationDisplayablePortfolio().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationDisplayablePortfolio().compareTo(getUser1NullDateDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationDisplayablePortfolio().compareTo(getUser1OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationDisplayablePortfolio().compareTo(getUser1YoungerDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationDisplayablePortfolio().compareTo(getUser2NullDateDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationDisplayablePortfolio().compareTo(getUser2OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserNullCreationDisplayablePortfolio().compareTo(getUser2YoungerDisplayablePortfolio()), equalTo(-1));
    }

    @Test public void compareCurrentUserOlderToOthers()
    {
        assertThat(getCurrentUserOlderDisplayablePortfolio().compareTo(getNullUserDisplayablePortfolio()), equalTo(1));
        assertThat(getCurrentUserOlderDisplayablePortfolio().compareTo(getCurrentUserDefaultDisplayablePortfolio()), equalTo(1));
        assertThat(getCurrentUserOlderDisplayablePortfolio().compareTo(getCurrentUserNullCreationDisplayablePortfolio()), equalTo(1));
        assertThat(getCurrentUserOlderDisplayablePortfolio().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(0));
        assertThat(getCurrentUserOlderDisplayablePortfolio().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserOlderDisplayablePortfolio().compareTo(getUser1NullDateDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserOlderDisplayablePortfolio().compareTo(getUser1OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserOlderDisplayablePortfolio().compareTo(getUser1YoungerDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserOlderDisplayablePortfolio().compareTo(getUser2NullDateDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserOlderDisplayablePortfolio().compareTo(getUser2OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserOlderDisplayablePortfolio().compareTo(getUser2YoungerDisplayablePortfolio()), equalTo(-1));
    }

    @Test public void compareCurrentUserYoungerToOthers()
    {
        assertThat(getCurrentUserYoungerDisplayablePortfolio().compareTo(getNullUserDisplayablePortfolio()), equalTo(1));
        assertThat(getCurrentUserYoungerDisplayablePortfolio().compareTo(getCurrentUserDefaultDisplayablePortfolio()), equalTo(1));
        assertThat(getCurrentUserYoungerDisplayablePortfolio().compareTo(getCurrentUserNullCreationDisplayablePortfolio()), equalTo(1));
        assertThat(getCurrentUserYoungerDisplayablePortfolio().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(1));
        assertThat(getCurrentUserYoungerDisplayablePortfolio().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(0));
        assertThat(getCurrentUserYoungerDisplayablePortfolio().compareTo(getUser1NullDateDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserYoungerDisplayablePortfolio().compareTo(getUser1OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserYoungerDisplayablePortfolio().compareTo(getUser1YoungerDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserYoungerDisplayablePortfolio().compareTo(getUser2NullDateDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserYoungerDisplayablePortfolio().compareTo(getUser2OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getCurrentUserYoungerDisplayablePortfolio().compareTo(getUser2YoungerDisplayablePortfolio()), equalTo(-1));
    }

    @Test public void compareUser1NullDateToOthers()
    {
        assertThat(getUser1NullDateDisplayablePortfolio().compareTo(getNullUserDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1NullDateDisplayablePortfolio().compareTo(getCurrentUserDefaultDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1NullDateDisplayablePortfolio().compareTo(getCurrentUserNullCreationDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1NullDateDisplayablePortfolio().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1NullDateDisplayablePortfolio().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1NullDateDisplayablePortfolio().compareTo(getUser1NullDateDisplayablePortfolio()), equalTo(0));
        assertThat(getUser1NullDateDisplayablePortfolio().compareTo(getUser1OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getUser1NullDateDisplayablePortfolio().compareTo(getUser1YoungerDisplayablePortfolio()), equalTo(-1));
        assertThat(getUser1NullDateDisplayablePortfolio().compareTo(getUser2NullDateDisplayablePortfolio()), equalTo(-1));
        assertThat(getUser1NullDateDisplayablePortfolio().compareTo(getUser2OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getUser1NullDateDisplayablePortfolio().compareTo(getUser2YoungerDisplayablePortfolio()), equalTo(-1));
    }

    @Test public void compareUser1OlderToOthers()
    {
        assertThat(getUser1OlderDisplayablePortfolio().compareTo(getNullUserDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1OlderDisplayablePortfolio().compareTo(getCurrentUserDefaultDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1OlderDisplayablePortfolio().compareTo(getCurrentUserNullCreationDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1OlderDisplayablePortfolio().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1OlderDisplayablePortfolio().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1OlderDisplayablePortfolio().compareTo(getUser1NullDateDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1OlderDisplayablePortfolio().compareTo(getUser1OlderDisplayablePortfolio()), equalTo(0));
        assertThat(getUser1OlderDisplayablePortfolio().compareTo(getUser1YoungerDisplayablePortfolio()), equalTo(-1));
        assertThat(getUser1OlderDisplayablePortfolio().compareTo(getUser2NullDateDisplayablePortfolio()), equalTo(-1));
        assertThat(getUser1OlderDisplayablePortfolio().compareTo(getUser2OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getUser1OlderDisplayablePortfolio().compareTo(getUser2YoungerDisplayablePortfolio()), equalTo(-1));
    }

    @Test public void compareUser1YoungerToOthers()
    {
        assertThat(getUser1YoungerDisplayablePortfolio().compareTo(getNullUserDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1YoungerDisplayablePortfolio().compareTo(getCurrentUserDefaultDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1YoungerDisplayablePortfolio().compareTo(getCurrentUserNullCreationDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1YoungerDisplayablePortfolio().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1YoungerDisplayablePortfolio().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1YoungerDisplayablePortfolio().compareTo(getUser1NullDateDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1YoungerDisplayablePortfolio().compareTo(getUser1OlderDisplayablePortfolio()), equalTo(1));
        assertThat(getUser1YoungerDisplayablePortfolio().compareTo(getUser1YoungerDisplayablePortfolio()), equalTo(0));
        assertThat(getUser1YoungerDisplayablePortfolio().compareTo(getUser2NullDateDisplayablePortfolio()), equalTo(-1));
        assertThat(getUser1YoungerDisplayablePortfolio().compareTo(getUser2OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getUser1YoungerDisplayablePortfolio().compareTo(getUser2YoungerDisplayablePortfolio()), equalTo(-1));
    }

    @Test public void compareUser2NullDateToOthers()
    {
        assertThat(getUser2NullDateDisplayablePortfolio().compareTo(getNullUserDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2NullDateDisplayablePortfolio().compareTo(getCurrentUserDefaultDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2NullDateDisplayablePortfolio().compareTo(getCurrentUserNullCreationDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2NullDateDisplayablePortfolio().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2NullDateDisplayablePortfolio().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2NullDateDisplayablePortfolio().compareTo(getUser1NullDateDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2NullDateDisplayablePortfolio().compareTo(getUser1OlderDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2NullDateDisplayablePortfolio().compareTo(getUser1YoungerDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2NullDateDisplayablePortfolio().compareTo(getUser2NullDateDisplayablePortfolio()), equalTo(0));
        assertThat(getUser2NullDateDisplayablePortfolio().compareTo(getUser2OlderDisplayablePortfolio()), equalTo(-1));
        assertThat(getUser2NullDateDisplayablePortfolio().compareTo(getUser2YoungerDisplayablePortfolio()), equalTo(-1));
    }

    @Test public void compareUser2OlderToOthers()
    {
        assertThat(getUser2OlderDisplayablePortfolio().compareTo(getNullUserDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2OlderDisplayablePortfolio().compareTo(getCurrentUserDefaultDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2OlderDisplayablePortfolio().compareTo(getCurrentUserNullCreationDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2OlderDisplayablePortfolio().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2OlderDisplayablePortfolio().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2OlderDisplayablePortfolio().compareTo(getUser1NullDateDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2OlderDisplayablePortfolio().compareTo(getUser1OlderDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2OlderDisplayablePortfolio().compareTo(getUser1YoungerDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2OlderDisplayablePortfolio().compareTo(getUser2NullDateDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2OlderDisplayablePortfolio().compareTo(getUser2OlderDisplayablePortfolio()), equalTo(0));
        assertThat(getUser2OlderDisplayablePortfolio().compareTo(getUser2YoungerDisplayablePortfolio()), equalTo(-1));
    }

    @Test public void compareUser2YoungerToOthers()
    {
        assertThat(getUser2YoungerDisplayablePortfolio().compareTo(getNullUserDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2YoungerDisplayablePortfolio().compareTo(getCurrentUserDefaultDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2YoungerDisplayablePortfolio().compareTo(getCurrentUserNullCreationDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2YoungerDisplayablePortfolio().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2YoungerDisplayablePortfolio().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2YoungerDisplayablePortfolio().compareTo(getUser1NullDateDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2YoungerDisplayablePortfolio().compareTo(getUser1OlderDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2YoungerDisplayablePortfolio().compareTo(getUser1YoungerDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2YoungerDisplayablePortfolio().compareTo(getUser2NullDateDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2YoungerDisplayablePortfolio().compareTo(getUser2OlderDisplayablePortfolio()), equalTo(1));
        assertThat(getUser2YoungerDisplayablePortfolio().compareTo(getUser2YoungerDisplayablePortfolio()), equalTo(0));
    }

    @Test public void shouldOrderOldYoung()
    {
        Set<DisplayablePortfolioDTO> treeSet = new TreeSet<>();
        treeSet.add(getCurrentUserOlderDisplayablePortfolio());
        treeSet.add(getCurrentUserYoungerDisplayablePortfolio());

        assertThat(treeSet.size(), equalTo(2));

        Iterator<DisplayablePortfolioDTO> iterator = treeSet.iterator();
        assertThat(iterator.next().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(0));
    }

    @Test public void shouldOrderAsExpected()
    {
        Set<DisplayablePortfolioDTO> treeSet = new TreeSet<>();

        treeSet.add(getUser1YoungerDisplayablePortfolio());
        treeSet.add(getUser2YoungerDisplayablePortfolio());
        treeSet.add(getUser1NullDateDisplayablePortfolio());
        treeSet.add(getNullUserDisplayablePortfolio());
        treeSet.add(getCurrentUserNullCreationDisplayablePortfolio());
        treeSet.add(getUser2NullDateDisplayablePortfolio());
        treeSet.add(getCurrentUserYoungerDisplayablePortfolio());
        treeSet.add(getCurrentUserDefaultDisplayablePortfolio());
        treeSet.add(getUser1OlderDisplayablePortfolio());
        treeSet.add(getUser2OlderDisplayablePortfolio());
        treeSet.add(getCurrentUserOlderDisplayablePortfolio());

        assertThat(treeSet.size(), equalTo(11));

        Iterator<DisplayablePortfolioDTO> iterator = treeSet.iterator();
        assertThat(iterator.next().compareTo(getNullUserDisplayablePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getCurrentUserDefaultDisplayablePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getCurrentUserNullCreationDisplayablePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser1NullDateDisplayablePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser1OlderDisplayablePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser1YoungerDisplayablePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser2NullDateDisplayablePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser2OlderDisplayablePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser2YoungerDisplayablePortfolio()), equalTo(0));
    }
}
