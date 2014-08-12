package com.tradehero.th.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

public class ObjectMapperWrapper extends ObjectMapper
{
    public ObjectMapperWrapper()
    {
        super();
    }

    public ObjectMapperWrapper(JsonFactory jf)
    {
        super(jf);
    }

    protected ObjectMapperWrapper(ObjectMapper src)
    {
        super(src);
    }

    public ObjectMapperWrapper(JsonFactory jf, DefaultSerializerProvider sp, DefaultDeserializationContext dc)
    {
        super(jf, sp, dc);
    }

    @Override protected Object _readMapAndClose(JsonParser jp, JavaType valueType) throws IOException, JsonParseException, JsonMappingException
    {
        ObjectNode root = readTree(jp);
        Iterator<Map.Entry<String, JsonNode>> elementsIterator =
                root.fields();
        while (elementsIterator.hasNext())
        {
            Map.Entry<String, JsonNode> element = elementsIterator.next();
            String name = element.getKey();
            if (name.equals("achievements"))
            {
                List<UserAchievementDTO> userAchievementDTO = readValue(root.get("achievements").traverse(), List.class);
                Timber.d("Found achievement");
                //Do something with it
                root.remove("achievements");
                break;
            }
        }
        return super._readMapAndClose(root.traverse(jp.getCodec()), valueType);
    }
}
