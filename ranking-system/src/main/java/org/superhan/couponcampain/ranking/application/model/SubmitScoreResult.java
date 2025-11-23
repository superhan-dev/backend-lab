package org.superhan.couponcampain.ranking.application.model;

import java.util.Optional;
import org.superhan.couponcampain.ranking.domain.model.RankWithScore;
import org.superhan.couponcampain.ranking.domain.model.ScorePolicy;

public record SubmitScoreResult(
        String key,
        ScorePolicy policy,
        Optional<RankWithScore> rank
) {
    public SubmitScoreResult {
        rank = rank != null ? rank : Optional.empty();
    }
}
