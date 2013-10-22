package com.tradehero.th.persistence.yahoo;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.yahoo.News;
import com.tradehero.th.network.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.YahooNewsService;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Cache for Yahoo News - uses SecurityId as a key and store List<News> as values.
 * This class uses internally the SecurityCompactCache (see the fetch method implementation)
 */
@Singleton public class NewsCache extends StraightDTOCache<SecurityId, List<News>>
{

    public static final String TAG = NewsCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 15;

    @Inject protected Lazy<SecurityCompactCache> securityCache;
    @Inject protected Lazy<YahooNewsService> yahooService;

    @Inject public NewsCache()
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
    @Override protected List<News> fetch(SecurityId key)
    {
        String yahooSymbol = getYahooSymbol(key);
        Response rawResponse = null;
        if (yahooSymbol != null)
        {
            return fetchYahooNews(yahooSymbol, rawResponse);
        }
        return null;
    }

        private String getYahooSymbol(SecurityId key)
        {
            String yahooSymbol = null;
            try
            {
               SecurityCompactDTO security = securityCache.get().get(key);
                if (security != null)
                    yahooSymbol = security.yahooSymbol;
            }
            catch (RetrofitError retrofitError)
            {
                BasicRetrofitErrorHandler.handle(retrofitError);
                THLog.e(TAG, "Error requesting key " + key.toString(), retrofitError);
            }
            return yahooSymbol;
        }

        private List<News> fetchYahooNews(String yahooSymbol, Response rawResponse)
        {
            try
            {
                rawResponse = yahooService.get().getNews(yahooSymbol);
            }
            catch (RetrofitError retrofitError)
            {
                BasicRetrofitErrorHandler.handle(retrofitError);
                THLog.e(TAG, "Error requesting yahoo symbol " + yahooSymbol, retrofitError);
            }

            if (rawResponse == null) return null;

            return tryParseResponse(rawResponse);
        }

            private List<News> tryParseResponse(Response response)
            {
                try
                {
                    List<News> news = parseResponse(response);
                    return news;

                } catch (XPathExpressionException e)
                {
                    THLog.e(TAG, "Failed to compile XPath", e);
                }
                catch (IOException e)
                {
                    THLog.e(TAG, "Failed to get response body", e);
                }
                return null;
            }

                private List<News> parseResponse(Response response) throws XPathExpressionException, IOException
                {
                    XPathExpression xpathItems = getxPathExpression();
                    InputSource input = new InputSource(response.getBody().in());
                    NodeList nodes = (NodeList)xpathItems.evaluate(input, XPathConstants.NODESET);
                    List<News> result = processItems(nodes);
                    return result;
                }

                    private XPathExpression getxPathExpression() throws XPathExpressionException
                    {
                        XPathFactory factory=XPathFactory.newInstance();
                        XPath xPath=factory.newXPath();
                        return xPath.compile("//item");
                    }

                    private List<News> processItems(NodeList nodes)
                    {
                        List<News> result = new ArrayList<>();
                        for (int i = 0; i < nodes.getLength(); i++) {
                            Node node = nodes.item(i);
                            result.add(new News(node));
                        }
                        return result;
                    }
}

