package com.tradehero.th.persistence.news.yahoo;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.api.news.NewsHeadlineList;
import com.tradehero.th.api.news.yahoo.YahooNewsHeadline;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.YahooNewsServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Cache for Yahoo News - uses SecurityId as a key and store List<News> as values.
 * This class uses internally the SecurityCompactCache (see the fetch method implementation)
 */
@Singleton public class YahooNewsHeadlineCache extends StraightDTOCache<SecurityId, NewsHeadlineList>
{
    public static final int DEFAULT_MAX_SIZE = 15;

    @NotNull private final Lazy<SecurityCompactCache> securityCache;
    @NotNull private final YahooNewsServiceWrapper yahooServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public YahooNewsHeadlineCache(
            @NotNull Lazy<SecurityCompactCache> securityCache,
            @NotNull YahooNewsServiceWrapper yahooNewsServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.securityCache = securityCache;
        this.yahooServiceWrapper = yahooNewsServiceWrapper;
    }
    //</editor-fold>

    /**
     *  the fetch operation works as follow:
     *  - use the SecurityCompactCache to get a SecurityCompactDTO for the given SecurityId
     *  - get the yahooSymbol for the DTO
     *  - use YahooNewsService to fetch the news for the given yahooSymbol
     *  - parse the xml feed
     */
    @Override @Nullable protected NewsHeadlineList fetch(@NotNull SecurityId key) throws Throwable
    {
        String yahooSymbol = getYahooSymbol(key);
        if (yahooSymbol != null)
        {
            return fetchYahooNews(yahooSymbol);
        }
        return null;
    }

    private String getYahooSymbol(@NotNull SecurityId key) throws Throwable
    {
        String yahooSymbol = null;
        SecurityCompactDTO security = securityCache.get().get(key);
        if (security != null)
        {
            yahooSymbol = security.yahooSymbol;
        }
        return yahooSymbol;
    }

    @Nullable private NewsHeadlineList fetchYahooNews(@NotNull String yahooSymbol) throws Throwable
    {
        Response rawResponse = yahooServiceWrapper.getNews(yahooSymbol);

        if (rawResponse == null)
        {
            return null;
        }

        return new NewsHeadlineList(tryParseResponse(rawResponse));
    }

    @NotNull private List<YahooNewsHeadline> tryParseResponse(@NotNull Response response)
    {
        try
        {
            return parseResponse(response);
        }
        catch (XPathExpressionException e)
        {
            Timber.e("Failed to compile XPath", e);
        }
        catch (IOException e)
        {
            Timber.e("Failed to get response body", e);
        }
        return null;
    }

    @NotNull private List<YahooNewsHeadline> parseResponse(@NotNull Response response) throws XPathExpressionException, IOException
    {
        XPathExpression xpathItems = getxPathExpression();
        InputSource input = new InputSource(response.getBody().in());
        NodeList nodes = (NodeList) xpathItems.evaluate(input, XPathConstants.NODESET);
        return processItems(nodes);
    }

    private XPathExpression getxPathExpression() throws XPathExpressionException
    {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        return xPath.compile("//item");
    }

    @NotNull private List<YahooNewsHeadline> processItems(@NotNull NodeList nodes)
    {
        List<YahooNewsHeadline> result = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++)
        {
            Node node = nodes.item(i);
            result.add(new YahooNewsHeadline(node));
        }
        return result;
    }
}

