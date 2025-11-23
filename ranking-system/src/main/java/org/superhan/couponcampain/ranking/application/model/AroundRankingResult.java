package org.superhan.couponcampain.ranking.application.model;

import java.util.List;
import org.superhan.couponcampain.ranking.domain.model.RankingEntry;

public record AroundRankingResult(
        String key,
        List<RankingEntry> entries
) {
}
