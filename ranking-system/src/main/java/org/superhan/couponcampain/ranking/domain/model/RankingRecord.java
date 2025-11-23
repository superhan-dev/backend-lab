package org.superhan.couponcampain.ranking.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public record RankingRecord(
        String userId,
        long score,
        Instant playAt,
        Optional<String> mode,
        Optional<Long> seasonId,
        ScorePolicy policy
) {
    public RankingRecord {
        Objects.requireNonNull(userId, "userId is required");
        Objects.requireNonNull(playAt, "playAt is required");
        Objects.requireNonNull(policy, "policy is required");
        mode = mode != null ? mode.map(String::trim).filter(s -> !s.isEmpty()) : Optional.empty();
        seasonId = seasonId != null ? seasonId : Optional.empty();
    }
}
