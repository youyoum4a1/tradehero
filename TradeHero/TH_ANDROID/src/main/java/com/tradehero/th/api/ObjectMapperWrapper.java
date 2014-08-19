package com.tradehero.th.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.achievement.UserAchievementDTOUtil;
import dagger.Lazy;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class ObjectMapperWrapper extends ObjectMapper
{
    @Inject Lazy<UserAchievementDTOUtil> userAchievementDTOUtil;

    public ObjectMapperWrapper()
    {
        super();
        init();
    }

    public ObjectMapperWrapper(JsonFactory jf)
    {
        super(jf);
        init();
    }

    protected ObjectMapperWrapper(ObjectMapper src)
    {
        super(src);
        init();
    }

    public ObjectMapperWrapper(JsonFactory jf, DefaultSerializerProvider sp, DefaultDeserializationContext dc)
    {
        super(jf, sp, dc);
        init();
    }

    private void init()
    {
        DaggerUtils.inject(this);
    }

    @Override protected Object _readMapAndClose(JsonParser jp, JavaType valueType) throws IOException, JsonParseException, JsonMappingException
    {
        TreeNode root = readTree(jp);
        if(root instanceof ObjectNode)
        {
            ObjectNode objectNode = (ObjectNode) root;
            Iterator<Map.Entry<String, JsonNode>> elementsIterator =
                    objectNode.fields();
            while (elementsIterator.hasNext())
            {
                Map.Entry<String, JsonNode> element = elementsIterator.next();
                String name = element.getKey();
                if (name.equals(UserAchievementDTOUtil.KEY_ACHIEVEMENT_NODE))
                {
                    List<UserAchievementDTO> userAchievementDTOs =
                            readValue(objectNode.get(UserAchievementDTOUtil.KEY_ACHIEVEMENT_NODE).traverse(), new TypeReference<List<UserAchievementDTO>>()
                            {
                            });

                    for (UserAchievementDTO userAchievementDTO : userAchievementDTOs)
                    {
                        userAchievementDTOUtil.get().put(userAchievementDTO);
                    }

                    objectNode.remove(UserAchievementDTOUtil.KEY_ACHIEVEMENT_NODE);
                    break;
                }
            }
        }
        return super._readMapAndClose(root.traverse(jp.getCodec()), valueType);
    }
}
