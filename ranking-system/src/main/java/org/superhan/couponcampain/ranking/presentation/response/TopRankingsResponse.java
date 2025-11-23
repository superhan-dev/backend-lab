package org.superhan.couponcampain.ranking.presentation.response;

import java.util.List;
import java.util.stream.Collectors;
import org.superhan.couponcampain.ranking.application.model.TopRankingsResult;

public record TopRankingsResponse(
        String key,
        List<RankingEntryResponse> entries
) {
    public static TopRankingsResponse from(TopRankingsResult result) {
        List<RankingEntryResponse> responses = result.entries()
                .stream()
                .map(RankingEntryResponse::from)
                .collect(Collectors.toList());
        return new TopRankingsResponse(result.key(), responses);
    }
}
