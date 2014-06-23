package com.tradehero.th.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

abstract public class UniqueFieldDTODeserialiser<DTOType> extends StdDeserializer<DTOType>
{
    @NotNull private final Map<String, Class<? extends DTOType>> uniqueAttributes;
    // We need an inner mapper to avoid infinite looping
    @NotNull private final ObjectMapper innerMapper;

    //<editor-fold desc="Constructors">
    protected UniqueFieldDTODeserialiser(
            @NotNull ObjectMapper innerMapper,
            @NotNull Map<String, Class<? extends DTOType>> uniqueAttributes,
            @NotNull Class<? extends DTOType> baseClass)
    {
        super(baseClass);
        this.uniqueAttributes = uniqueAttributes;
        this.innerMapper = innerMapper;
        innerMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    //</editor-fold>

    @Override
    public DTOType deserialize(@NotNull JsonParser jp, DeserializationContext ctxt)
            throws IOException
    {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = mapper.readTree(jp);
        Class<? extends DTOType> dtoClass = getDefaultClass();
        Iterator<Map.Entry<String, JsonNode>> elementsIterator =
                root.fields();
        while (elementsIterator.hasNext())
        {
            Map.Entry<String, JsonNode> element = elementsIterator.next();
            String name = element.getKey();
            if (uniqueAttributes.containsKey(name))
            {
                dtoClass = uniqueAttributes.get(name);
                break;
            }
        }
        return innerMapper.readValue(root.traverse(), dtoClass);
    }

    /**
     * Children classes that wish to have a default class type to deserialise to should override this method.
     * @return
     */
    abstract protected Class<? extends DTOType> getDefaultClass();
}
