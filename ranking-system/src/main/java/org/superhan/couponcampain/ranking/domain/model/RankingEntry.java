package org.superhan.couponcampain.ranking.domain.model;

public record RankingEntry(
        String userId,
        double score,
        long rank
) {
}
