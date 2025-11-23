package org.superhan.couponcampain.ranking.presentation.response;

import java.util.Optional;
import org.superhan.couponcampain.ranking.application.model.SubmitScoreResult;
import org.superhan.couponcampain.ranking.domain.model.RankWithScore;
import org.superhan.couponcampain.ranking.domain.model.ScorePolicy;

public record SubmitScoreResponse(
        String key,
        ScorePolicy policy,
        Long rank,
        Double score
) {
    public static SubmitScoreResponse from(SubmitScoreResult result) {
        Optional<RankWithScore> rank = result.rank();
        return new SubmitScoreResponse(
                result.key(),
                result.policy(),
                rank.map(RankWithScore::rank).orElse(null),
                rank.map(RankWithScore::score).orElse(null)
        );
    }
}
