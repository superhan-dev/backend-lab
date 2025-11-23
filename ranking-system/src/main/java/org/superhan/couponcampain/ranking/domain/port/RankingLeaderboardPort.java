package org.superhan.couponcampain.ranking.domain.port;

import java.util.List;
import java.util.Optional;
import org.superhan.couponcampain.ranking.domain.model.RankWithScore;
import org.superhan.couponcampain.ranking.domain.model.RankingEntry;

public interface RankingLeaderboardPort {

    void upsertBestScore(String key, String userId, double score);

    double incrementScore(String key, String userId, double delta);

    List<RankingEntry> findTop(String key, int limit);

    Optional<RankWithScore> findRank(String key, String userId);

    List<RankingEntry> findAround(String key, String userId, int range);
}
