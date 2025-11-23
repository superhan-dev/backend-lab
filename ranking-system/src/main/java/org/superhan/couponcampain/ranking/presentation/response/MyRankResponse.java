package org.superhan.couponcampain.ranking.presentation.response;

import java.util.Optional;
import org.superhan.couponcampain.ranking.application.model.MyRankingResult;
import org.superhan.couponcampain.ranking.domain.model.RankWithScore;

public record MyRankResponse(
        String key,
        Long rank,
        Double score
) {
    public static MyRankResponse from(MyRankingResult result) {
        Optional<RankWithScore> rank = result.rank();
        return new MyRankResponse(
                result.key(),
                rank.map(RankWithScore::rank).orElse(null),
                rank.map(RankWithScore::score).orElse(null)
        );
    }
}
