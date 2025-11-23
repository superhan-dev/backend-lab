package org.superhan.couponcampain.ranking.application.model;

import java.util.Optional;
import org.superhan.couponcampain.ranking.domain.model.RankWithScore;

public record MyRankingResult(
        String key,
        Optional<RankWithScore> rank
) {
    public MyRankingResult {
        rank = rank != null ? rank : Optional.empty();
    }
}
