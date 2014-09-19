package com.tradehero.th.api;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.level.UserXPAchievementDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.achievement.AchievementCategoryCache;
import com.tradehero.th.persistence.achievement.AchievementCategoryListCache;
import com.tradehero.th.persistence.achievement.UserAchievementCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class ObjectMapperWrapper extends ObjectMapper
{
    @NotNull protected final Lazy<UserAchievementCache> userAchievementCacheLazy;
    @NotNull protected final Lazy<AchievementCategoryListCache> achievementCategoryListCacheLazy;
    @NotNull protected final Lazy<AchievementCategoryCache> achievementCategoryCacheLazy;

    //<editor-fold desc="Constructors">
    @Inject public ObjectMapperWrapper(
            @NotNull Lazy<UserAchievementCache> userAchievementCacheLazy,
            @NotNull Lazy<AchievementCategoryListCache> achievementCategoryListCacheLazy,
            @NotNull Lazy<AchievementCategoryCache> achievementCategoryCacheLazy)
    {
        super();
        this.userAchievementCacheLazy = userAchievementCacheLazy;
        this.achievementCategoryListCacheLazy = achievementCategoryListCacheLazy;
        this.achievementCategoryCacheLazy = achievementCategoryCacheLazy;
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    //</editor-fold>

    @Override protected Object _readMapAndClose(
            @NotNull JsonParser jp,
            @NotNull JavaType valueType)
            throws IOException, JsonParseException, JsonMappingException
    {
        TreeNode root = readTree(jp);
        if (root instanceof ObjectNode)
        {
            extractExtras((ObjectNode) root);
        }
        return super._readMapAndClose(root.traverse(jp.getCodec()), valueType);
    }

    protected void extractExtras(@NotNull ObjectNode objectNode)
            throws IOException
    {
        Map.Entry<String, JsonNode> element;
        Iterator<Map.Entry<String, JsonNode>> elementsIterator = objectNode.fields();
        while (elementsIterator.hasNext())
        {
            element = elementsIterator.next();
            if (isAchievementNode(element))
            {
                handleAchievement(objectNode.get(UserAchievementCache.KEY_ACHIEVEMENT_NODE));
                objectNode.remove(UserAchievementCache.KEY_ACHIEVEMENT_NODE);
            }
            else if(isXPNode(element))
            {
                handleXP(objectNode.get(UserProfileCache.KEY_XP_NODE));
                objectNode.remove(UserProfileCache.KEY_XP_NODE);
            }
            //else if (isOther(element)) {}
        }
    }

    protected boolean isAchievementNode(@NotNull Map.Entry<String, JsonNode> element)
    {
        return element.getKey().equals(UserAchievementCache.KEY_ACHIEVEMENT_NODE);
    }

    protected boolean isXPNode(@NotNull Map.Entry<String, JsonNode> element)
    {
        return element.getKey().equals(UserProfileCache.KEY_XP_NODE);
    }

    protected void handleAchievement(
            @NotNull JsonNode jsonNode)
            throws IOException
    {
        List<UserAchievementDTO> userAchievementDTOs = readValue(
                jsonNode.traverse(),
                new TypeReference<List<UserAchievementDTO>>()
                {
                });
        userAchievementCacheLazy.get().put(userAchievementDTOs);
        achievementCategoryListCacheLazy.get().invalidateAll();
        achievementCategoryCacheLazy.get().invalidateAll();
    }

    protected void handleXP(@NotNull JsonNode jsonNode)
            throws IOException
    {

    }
}
