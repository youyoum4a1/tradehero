package com.tradehero;

import com.android.internal.util.Predicate;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.api.competition.key.ProviderDisplayCellListKey;
import com.tradehero.th.api.competition.key.ProviderSecurityListType;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.key.CommentKey;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.discussion.key.MessageDiscussionListKey;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.key.RecipientTypedMessageListKey;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.leaderboard.position.PerPagedLeaderboardMarkUserId;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListRegionalKey;
import com.tradehero.th.api.news.key.NewsItemListSecurityKey;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionCompactId;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.social.key.FriendsListKey;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.jetbrains.annotations.NotNull;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

abstract public class AbstractTestBase
{
    protected Random random = new Random();

    // HACK Let's see if we still have random results on AsyncTask-based tests.
    protected void runBgUiTasks(int count) throws InterruptedException
    {
        assertThat(count).isGreaterThan(0);
        for (int i = 0; i < count; i++)
        {
            Robolectric.runBackgroundTasks();
            Thread.sleep(50);
            Robolectric.runUiThreadTasks();
            Robolectric.runUiThreadTasksIncludingDelayedTasks();
        }
    }

    public ArrayList<Class<?>> getClassesForPackage(@NotNull Package pkg, @NotNull Predicate<Class<?>> thatMatch)
    {
        ArrayList<Class<?>> classes = getClassesForPackage(pkg);
        ArrayList<Class<?>> matched = new ArrayList<>();
        for (Class<?> potential :classes)
        {
            if (thatMatch.apply(potential))
            {
                matched.add(potential);
            }
        }
        return matched;
    }

    public ArrayList<Class<?>> getClassesForPackage( @NotNull Package pkg)
    {
        // From http://stackoverflow.com/questions/176527/how-can-i-enumerate-all-classes-in-a-package-and-add-them-to-a-list
        String pkgname = pkg.getName();
        ArrayList<Class<?>> classes = new ArrayList<>();
        // Get a File object for the package
        File directory = null;
        String fullPath;
        String relPath = pkgname.replace('.', '/');
        System.out.println("ClassDiscovery: Package: " + pkgname + " becomes Path:" + relPath);
        URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
        System.out.println("ClassDiscovery: Resource = " + resource);
        if (resource == null)
        {
            throw new RuntimeException("No resource for " + relPath);
        }
        fullPath = resource.getFile();
        System.out.println("ClassDiscovery: FullPath = " + resource);

        try
        {
            directory = new File(resource.toURI());
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(
                    pkgname + " (" + resource + ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...", e);
        }
        catch (IllegalArgumentException e)
        {
            directory = null;
        }
        System.out.println("ClassDiscovery: Directory = " + directory);

        String nonTestFullPath = fullPath.replace("test-", "");
        String nonTestRelPath = "../" + relPath.replace("test-", "");
        File nonTestDirectory = new File(nonTestFullPath);

        ArrayList<Class<?>> collated = getClassesForDirectory(directory, pkgname, fullPath, relPath);
        collated.addAll(getClassesForDirectory(nonTestDirectory, pkgname, nonTestFullPath, nonTestRelPath));
        return collated;
    }

    public ArrayList<Class<?>> getClassesForDirectory(
            File directory,
            String pkgname,
            String fullPath,
            String relPath)
    {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        if (directory != null && directory.exists())
        {
            // Get the list of the files contained in the package
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++)
            {
                // we are only interested in .class files
                if (files[i].endsWith(".class"))
                {
                    // removes the .class extension
                    String className = pkgname + '.' + files[i].substring(0, files[i].length() - 6);
                    System.out.println("ClassDiscovery: className = " + className);
                    try
                    {
                        classes.add(Class.forName(className));
                    }
                    catch (ClassNotFoundException e)
                    {
                        throw new RuntimeException("ClassNotFoundException loading " + className);
                    }
                }
            }
        }
        else
        {
            try
            {
                String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
                JarFile jarFile = new JarFile(jarPath);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements())
                {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length()))
                    {
                        System.out.println("ClassDiscovery: JarEntry: " + entryName);
                        String className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
                        System.out.println("ClassDiscovery: className = " + className);
                        try
                        {
                            classes.add(Class.forName(className));
                        }
                        catch (ClassNotFoundException e)
                        {
                            throw new RuntimeException("ClassNotFoundException loading " + className);
                        }
                    }
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(pkgname + " (" + directory + ") does not appear to be a valid package", e);
            }
        }
        return classes;
    }

    //<editor-fold desc="Create valid parameters">
    public Object mockValidParameter(@NotNull Class<?> type)
    {
        if (type.equals(UserBaseKey.class))
        {
            return new UserBaseKey(1);
        }
        if (type.equals(UserListType.class))
        {
            Integer perPage = random.nextBoolean() ? 2 : null;
            return new SearchUserListType(
                    "a",
                    perPage != null || random.nextBoolean() ? 1 : null,
                    perPage);
        }
        if (type.equals(FriendsListKey.class))
        {
            return new FriendsListKey(
                    (UserBaseKey) mockValidParameter(UserBaseKey.class),
                    random.nextBoolean() ? SocialNetworkEnum.FB : null,
                    random.nextBoolean() ? "a" : null);
        }
        if (type.equals(SocialNetworkEnum.class))
        {
            return SocialNetworkEnum.FB;
        }

        if (type.equals(AlertId.class))
        {
            return new AlertId(1, 2);
        }
        if (type.equals(ProviderId.class))
        {
            return new ProviderId(1);
        }
        if (type.equals(CompetitionId.class))
        {
            return new CompetitionId(1);
        }
        if (type.equals(CompetitionLeaderboardId.class))
        {
            Integer perPage = random.nextBoolean() ? 4 : null;
           return new CompetitionLeaderboardId(
                   1,
                   2,
                   perPage != null || random.nextBoolean() ? 3 : null,
                   perPage);
        }
        if (type.equals(SecurityIntegerId.class))
        {
            return new SecurityIntegerId(1);
        }

        if (type.equals(FollowerHeroRelationId.class))
        {
            return new FollowerHeroRelationId(1, 2);
        }
        if (type.equals(LeaderboardKey.class))
        {
            return new LeaderboardKey(1);
        }
        if (type.equals(LeaderboardMarkUserId.class))
        {
            Integer perPage = random.nextBoolean() ? 3 : null;
            return new PerPagedLeaderboardMarkUserId(
                    1,
                    perPage != null || random.nextBoolean() ? 2 : null,
                    perPage);
        }

        if (type.equals(ExchangeIntegerId.class))
        {
            return new ExchangeIntegerId(1);
        }

        if (type.equals(TimelineItemDTOKey.class))
        {
            return new TimelineItemDTOKey(1);
        }
        if (type.equals(DiscussionKey.class))
        {
            return new CommentKey(1);
        }
        if (type.equals(DiscussionFormDTO.class))
        {
            return new DiscussionFormDTO()
            {
                @Override public DiscussionType getInReplyToType()
                {
                    return DiscussionType.BROADCAST_MESSAGE;
                }

                @Override public DiscussionKey getInitiatingDiscussionKey()
                {
                    return (DiscussionKey) mockValidParameter(DiscussionKey.class);
                }
            };
        }
        if (type.equals(MessageDiscussionListKey.class))
        {
            return new MessageDiscussionListKey(
                    DiscussionType.COMMENT,
                    1,
                    (UserBaseKey) mockValidParameter(UserBaseKey.class),
                    (UserBaseKey) mockValidParameter(UserBaseKey.class),
                    random.nextBoolean() ? 2 : null,
                    random.nextBoolean() ? 3 : null,
                    random.nextBoolean() ? 4 : null);
        }
        if (DiscussionListKey.class.isAssignableFrom(type))
        {
            return new DiscussionVoteKey(DiscussionType.COMMENT, 1, VoteDirection.DownVote);
        }
        if (MessageListKey.class.isAssignableFrom(type))
        {
            Integer perPage = random.nextBoolean() ? 2 : null;
            return new RecipientTypedMessageListKey(
                    perPage != null || random.nextBoolean() ? (Integer) 1 : null,
                    perPage,
                    DiscussionType.COMMENT,
                    (UserBaseKey) mockValidParameter(UserBaseKey.class));
        }
        if (type.equals(MessageHeaderId.class))
        {
            return new MessageHeaderId(1);
        }
        if (type.equals(NewsItemListRegionalKey.class))
        {
            Integer perPage = random.nextBoolean() ? 2 : null;
            return new NewsItemListRegionalKey(
                    "a",
                    "b",
                    perPage != null || random.nextBoolean() ? 1 : null,
                    perPage);
        }
        if (type.equals(NewsItemListSecurityKey.class))
        {
            Integer perPage = random.nextBoolean() ? 2 : null;
            return new NewsItemListSecurityKey(
                    (SecurityIntegerId) mockValidParameter(SecurityIntegerId.class),
                    perPage != null || random.nextBoolean() ? 1 : null,
                    perPage);
        }
        if (type.equals(NewsItemListKey.class))
        {
            return mockValidParameter(NewsItemListRegionalKey.class);
        }
        if (type.equals(NotificationKey.class))
        {
            return new NotificationKey(1);
        }

        if (type.equals(OwnedTradeId.class))
        {
            return new OwnedTradeId(1, 2, 3, 4);
        }
        if (type.equals(OwnedPositionId.class))
        {
            return new OwnedPositionId(1, 2, 3);
        }
        if (type.equals(OwnedPortfolioId.class))
        {
            return new OwnedPortfolioId(1, 2);
        }
        if (type.equals(WatchlistPositionDTO.class))
        {
            WatchlistPositionDTO value = new WatchlistPositionDTO();
            value.id = 1;
            return value;
        }
        if (type.equals(PositionCompactId.class))
        {
            return new PositionCompactId(1);
        }

        if (type.equals(ProviderSecurityListType.class))
        {
            Integer perPage = random.nextBoolean() ? 2 : null;
            return new BasicProviderSecurityListType(
                    (ProviderId) mockValidParameter(ProviderId.class),
                    perPage != null || random.nextBoolean() ? 1 : null,
                    perPage);
        }
        if (type.equals(HelpVideoListKey.class))
        {
            return new HelpVideoListKey((ProviderId) mockValidParameter(ProviderId.class));
        }
        if (type.equals(ProviderDisplayCellListKey.class))
        {
            return new ProviderDisplayCellListKey((ProviderId) mockValidParameter(ProviderId.class));
        }

        if (type.equals(SecurityId.class))
        {
            return new SecurityId("a", "b");
        }
        if (type.equals(SecurityListType.class))
        {
            Integer perPage = random.nextBoolean() ? 2 : null;
            // TODO randomly pick another one?
            return new TrendingBasicSecurityListType(
                    random.nextBoolean() ? "a" : null,
                    perPage != null || random.nextBoolean() ? 1 : null,
                    perPage
            );
        }

        if (type.equals(Boolean.class) || type.equals(boolean.class))
        {
            return random.nextBoolean();
        }
        if (type.equals(Integer.class))
        {
            return 1;
        }
        if (type.equals(String.class))
        {
            return "a";
        }
        return mock(type);
    }
    //</editor-fold>
}
