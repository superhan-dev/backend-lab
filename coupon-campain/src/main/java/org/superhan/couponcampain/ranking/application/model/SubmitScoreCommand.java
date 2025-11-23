package org.superhan.couponcampain.ranking.application.model;

import java.time.Instant;
import java.util.Objects;
import org.superhan.couponcampain.ranking.domain.model.RankingScope;
import org.superhan.couponcampain.ranking.domain.model.ScorePolicy;

public record SubmitScoreCommand(
        String userId,
        long score,
        Instant playAt,
        RankingScope scope,
        ScorePolicy policy
) {
    public SubmitScoreCommand {
        Objects.requireNonNull(userId, "userId is required");
        Objects.requireNonNull(playAt, "playAt is required");
        Objects.requireNonNull(scope, "scope is required");
        Objects.requireNonNull(policy, "policy is required");
    }
}
