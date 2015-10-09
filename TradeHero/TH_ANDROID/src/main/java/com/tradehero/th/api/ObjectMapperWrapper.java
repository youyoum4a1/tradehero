package com.tradehero.th.api;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tradehero.th.api.achievement.UserAchievementDTOList;
import com.tradehero.th.api.level.UserXPAchievementDTO;
import com.tradehero.th.api.level.UserXPAchievementDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.achievement.AchievementCategoryCacheRx;
import com.tradehero.th.persistence.achievement.AchievementCategoryListCacheRx;
import com.tradehero.th.persistence.achievement.UserAchievementCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import dagger.Lazy;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

import static com.tradehero.th.utils.broadcast.BroadcastConstants.KEY_ACHIEVEMENT_NODE;
import static com.tradehero.th.utils.broadcast.BroadcastConstants.KEY_XP_NODE;

public class ObjectMapperWrapper extends ObjectMapper
{
    @NonNull protected final Lazy<UserAchievementCacheRx> userAchievementCacheLazy;
    @NonNull protected final Lazy<AchievementCategoryListCacheRx> achievementCategoryListCacheLazy;
    @NonNull protected final Lazy<AchievementCategoryCacheRx> achievementCategoryCacheLazy;
    @NonNull private final Lazy<BroadcastUtils> broadcastUtilsLazy;
    @NonNull private final Lazy<UserProfileCacheRx> userProfileCacheLazy;
    @NonNull private final Lazy<CurrentUserId> currentUserIdLazy;

    //<editor-fold desc="Constructors">
    @Inject public ObjectMapperWrapper(
            @NonNull Lazy<UserAchievementCacheRx> userAchievementCacheLazy,
            @NonNull Lazy<AchievementCategoryListCacheRx> achievementCategoryListCacheLazy,
            @NonNull Lazy<AchievementCategoryCacheRx> achievementCategoryCacheLazy,
            @NonNull Lazy<UserProfileCacheRx> userProfileCacheLazy,
            @NonNull Lazy<CurrentUserId> currentUserIdLazy,
            @NonNull Lazy<BroadcastUtils> broadcastUtilsLazy)
    {
        super();
        this.userAchievementCacheLazy = userAchievementCacheLazy;
        this.achievementCategoryListCacheLazy = achievementCategoryListCacheLazy;
        this.achievementCategoryCacheLazy = achievementCategoryCacheLazy;
        this.broadcastUtilsLazy = broadcastUtilsLazy;
        this.userProfileCacheLazy = userProfileCacheLazy;
        this.currentUserIdLazy = currentUserIdLazy;
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    //</editor-fold>

    @Override protected Object _readMapAndClose(
            @NonNull JsonParser jp,
            @NonNull JavaType valueType)
            throws IOException, JsonMappingException
    {
        TreeNode root = readTree(jp);
        if (root instanceof ObjectNode)
        {
            extractExtras((ObjectNode) root);
        }
        return super._readMapAndClose(root.traverse(jp.getCodec()), valueType);
    }

    protected void extractExtras(@NonNull ObjectNode objectNode)
            throws IOException
    {
        Map.Entry<String, JsonNode> element;
        Iterator<Map.Entry<String, JsonNode>> elementsIterator = objectNode.fields();
        while (elementsIterator.hasNext())
        {
            element = elementsIterator.next();
            if (isAchievementNode(element))
            {
                handleAchievement(objectNode.get(KEY_ACHIEVEMENT_NODE));
            }
            else
            {
                if (isXPNode(element))
                {
                    handleXP(objectNode.get(KEY_XP_NODE));
                }
            }
            //else if (isOther(element)) {}
        }
        objectNode.remove(KEY_ACHIEVEMENT_NODE);
        objectNode.remove(KEY_XP_NODE);
    }

    protected boolean isAchievementNode(@NonNull Map.Entry<String, JsonNode> element)
    {
        return element.getKey().equals(KEY_ACHIEVEMENT_NODE);
    }

    protected boolean isXPNode(@NonNull Map.Entry<String, JsonNode> element)
    {
        return element.getKey().equals(KEY_XP_NODE);
    }

    protected void handleAchievement(
            @NonNull JsonNode jsonNode)
            throws IOException
    {
        UserAchievementDTOList userAchievementDTOs = readValue(
                jsonNode.traverse(),
                new TypeReference<UserAchievementDTOList>()
                {
                });
        if (userAchievementDTOs != null)
        {
            userAchievementCacheLazy.get().onNextNonDefDuplicates(userAchievementDTOs);
            UserBaseKey userBaseKey = currentUserIdLazy.get().toUserBaseKey();
            achievementCategoryListCacheLazy.get().invalidate(userBaseKey);
            userProfileCacheLazy.get().updateXPIfNecessary(userBaseKey, userAchievementDTOs.findBiggestXPTotal());
            userProfileCacheLazy.get().addAchievements(userBaseKey, userAchievementDTOs.size());
        }
    }

    protected void handleXP(@NonNull JsonNode jsonNode)
            throws IOException
    {
        JsonNode copy = jsonNode.deepCopy();
        UserXPAchievementDTOList userXPAchievementDTOs = null;
        JsonParser parser = jsonNode.traverse();
        try
        {
            userXPAchievementDTOs = readValue(
                    parser,
                    new TypeReference<UserXPAchievementDTOList>()
                    {
                    });
        } catch (JsonMappingException e)
        {
            Timber.e(e, "Failed to read UserXPAchievementDTOList. %s", copy.asText());
            throw e;
        }
        if(userXPAchievementDTOs != null)
        {
            UserBaseKey userBaseKey = currentUserIdLazy.get().toUserBaseKey();
            for (UserXPAchievementDTO userXPAchievementDTO : userXPAchievementDTOs)
            {
                broadcastUtilsLazy.get().enqueue(userXPAchievementDTO);
            }
            userProfileCacheLazy.get().updateXPIfNecessary(userBaseKey, userXPAchievementDTOs.findBiggestXPTotal());
        }
    }
}
