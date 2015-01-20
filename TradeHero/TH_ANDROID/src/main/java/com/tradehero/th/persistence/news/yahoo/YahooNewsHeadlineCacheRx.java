package com.tradehero.th.persistence.news.yahoo;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.news.NewsHeadlineList;
import com.tradehero.th.api.news.yahoo.YahooNewsHeadline;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.YahooNewsServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
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
import rx.Observable;
import timber.log.Timber;

/**
 * Cache for Yahoo News - uses SecurityId as a key and store List<News> as values.
 * This class uses internally the SecurityCompactCache (see the fetch method implementation)
 */
@Singleton @UserCache
public class YahooNewsHeadlineCacheRx extends BaseFetchDTOCacheRx<SecurityId, NewsHeadlineList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 15;

    @NonNull private final Lazy<SecurityCompactCacheRx> securityCache;
    @NonNull private final YahooNewsServiceWrapper yahooServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public YahooNewsHeadlineCacheRx(
            @NonNull Lazy<SecurityCompactCacheRx> securityCache,
            @NonNull YahooNewsServiceWrapper yahooNewsServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
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
    @Override @NonNull protected Observable<NewsHeadlineList> fetch(@NonNull SecurityId key)
    {
        return getYahooSymbol(key)
                .flatMap(this::fetchYahooNews);
    }

    @NonNull private Observable<String> getYahooSymbol(@NonNull SecurityId key)
    {
        return securityCache.get().get(key)
                .flatMap(pair -> {
                    if (pair.second.yahooSymbol != null)
                    {
                        return Observable.just(pair.second.yahooSymbol);
                    }
                    return Observable.empty();
                });
    }

    @NonNull private Observable<NewsHeadlineList> fetchYahooNews(@NonNull String yahooSymbol)
    {
        return yahooServiceWrapper.getNewsRx(yahooSymbol)
            .map(rawResponse -> {
                if (rawResponse == null)
                {
                    throw new NullPointerException("Response was null");
                }

                return new NewsHeadlineList(tryParseResponse(rawResponse));
            });
    }

    @NonNull private List<YahooNewsHeadline> tryParseResponse(@NonNull Response response)
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

    @NonNull private List<YahooNewsHeadline> parseResponse(@NonNull Response response) throws XPathExpressionException, IOException
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

    @NonNull private List<YahooNewsHeadline> processItems(@NonNull NodeList nodes)
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

