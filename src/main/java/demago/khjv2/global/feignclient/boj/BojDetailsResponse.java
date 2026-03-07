package demago.khjv2.global.feignclient.boj;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDateTime;

public record BojDetailsResponse(
        String handle,
        String bio,
        boolean verified,
        String badgeId,
        String backgroundId,
        String profileImageUrl,
        int solvedCount,
        int voteCount,

        @JsonProperty("class")
        int clazz, // JSON key가 "class"라서 Java 예약어 충돌

        String classDecoration,
        int rivalCount,
        int reverseRivalCount,
        int tier,
        int rating,
        int ratingByProblemsSum,
        int ratingByClass,
        int ratingBySolvedCount,
        int ratingByVoteCount,
        int overRating,
        int overRatingCutoff,
        int arenaTier,
        int arenaRating,
        int arenaMaxTier,
        int arenaMaxRating,
        int arenaCompetedRoundCount,
        int maxStreak,
        int coins,
        int stardusts,
        Instant joinedAt,
        Instant bannedUntil,
        Instant proUntil,
        int rank,
        boolean isRival,
        boolean isReverseRival,
        boolean blocked,
        boolean reverseBlocked
) {}