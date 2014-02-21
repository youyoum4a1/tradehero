package com.tradehero.th.persistence.news.yahoo;

import com.tradehero.th.api.news.NewsHeadlineList;
import com.tradehero.th.api.news.yahoo.YahooNewsHeadline;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.YahooNewsService;
import com.tradehero.th.persistence.news.NewsHeadlineCache;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Cache for Yahoo News - uses SecurityId as a key and store List<News> as values.
 * This class uses internally the SecurityCompactCache (see the fetch method implementation)
 */
@Singleton public class YahooNewsHeadlineCache extends NewsHeadlineCache
{
    public static final int DEFAULT_MAX_SIZE = 15;

    @Inject protected Lazy<SecurityCompactCache> securityCache;
    @Inject protected YahooNewsService yahooService;

    @Inject public YahooNewsHeadlineCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    /**
     *  the fetch operation works as follow:
     *  - use the SecurityCompactCache to get a SecurityCompactDTO for the given SecurityId
     *  - get the yahooSymbol for the DTO
     *  - use YahooNewsService to fetch the news for the given yahooSymbol
     *  - parse the xml feed
     */
    @Override protected NewsHeadlineList fetch(SecurityId key) throws Throwable
    {
        String yahooSymbol = getYahooSymbol(key);
        Response rawResponse = null;
        if (yahooSymbol != null)
        {
            return fetchYahooNews(yahooSymbol, rawResponse);
        }
        return null;
    }

    private String getYahooSymbol(SecurityId key) throws Throwable
    {
        String yahooSymbol = null;
        SecurityCompactDTO security = securityCache.get().get(key);
        if (security != null)
        {
            yahooSymbol = security.yahooSymbol;
        }
        return yahooSymbol;
    }

    private NewsHeadlineList fetchYahooNews(String yahooSymbol, Response rawResponse) throws Throwable
    {
        rawResponse = yahooService.getNews(yahooSymbol);

        if (rawResponse == null)
        {
            return null;
        }

        return new NewsHeadlineList(tryParseResponse(rawResponse));
    }

    private List<YahooNewsHeadline> tryParseResponse(Response response)
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

    private List<YahooNewsHeadline> parseResponse(Response response) throws XPathExpressionException, IOException
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

    private List<YahooNewsHeadline> processItems(NodeList nodes)
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

