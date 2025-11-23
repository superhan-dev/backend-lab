package org.superhan.couponcampain.ranking.presentation.response;

import java.util.List;
import java.util.stream.Collectors;
import org.superhan.couponcampain.ranking.application.model.AroundRankingResult;

public record AroundRankResponse(
        String key,
        List<RankingEntryResponse> entries
) {
    public static AroundRankResponse from(AroundRankingResult result) {
        List<RankingEntryResponse> responses = result.entries()
                .stream()
                .map(RankingEntryResponse::from)
                .collect(Collectors.toList());
        return new AroundRankResponse(result.key(), responses);
    }
}
