package org.superhan.couponcampain.ranking.presentation.response;

import org.superhan.couponcampain.ranking.domain.model.RankingEntry;

public record RankingEntryResponse(
        String userId,
        double score,
        long rank
) {
    public static RankingEntryResponse from(RankingEntry entry) {
        return new RankingEntryResponse(entry.userId(), entry.score(), entry.rank());
    }
}
