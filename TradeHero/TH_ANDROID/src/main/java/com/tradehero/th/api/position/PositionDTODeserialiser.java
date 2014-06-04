package com.tradehero.th.api.position;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PositionDTODeserialiser extends StdDeserializer<PositionDTO>
{
    private Map<String, Class<? extends PositionDTO>> uniqueAttributes;

    //<editor-fold desc="Constructors">
    PositionDTODeserialiser()
    {
        super(PositionDTO.class);
        uniqueAttributes = new HashMap<>();
    }
    //</editor-fold>

    void registerPositionDTO(
            String uniqueAttribute,
            Class<? extends PositionDTO> positionDTOClass)
    {
        uniqueAttributes.put(uniqueAttribute, positionDTOClass);
    }

    @Override
    public PositionDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException
    {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = mapper.readTree(jp);
        Class<? extends PositionDTO> positionDTOClass = PositionDTO.class;
        Iterator<Map.Entry<String, JsonNode>> elementsIterator =
                root.fields();
        while (elementsIterator.hasNext())
        {
            Map.Entry<String, JsonNode> element = elementsIterator.next();
            String name = element.getKey();
            if (uniqueAttributes.containsKey(name))
            {
                positionDTOClass = uniqueAttributes.get(name);
                break;
            }
        }
        return mapper.readValue(jp, positionDTOClass);
    }
}
