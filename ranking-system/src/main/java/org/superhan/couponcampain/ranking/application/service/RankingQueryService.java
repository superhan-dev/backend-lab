package org.superhan.couponcampain.ranking.application.service;

import java.util.List;
import java.util.Optional;
import org.superhan.couponcampain.ranking.application.model.AroundRankingResult;
import org.superhan.couponcampain.ranking.application.model.MyRankingResult;
import org.superhan.couponcampain.ranking.application.model.TopRankingsResult;
import org.superhan.couponcampain.ranking.application.usecase.RankingQueryUseCase;
import org.superhan.couponcampain.ranking.domain.model.RankWithScore;
import org.superhan.couponcampain.ranking.domain.model.RankingEntry;
import org.superhan.couponcampain.ranking.domain.model.RankingScope;
import org.superhan.couponcampain.ranking.domain.port.RankingLeaderboardPort;
import org.springframework.stereotype.Service;

@Service
public class RankingQueryService implements RankingQueryUseCase {

    private final RankingLeaderboardPort leaderboardPort;
    private final RankingKeyFactory rankingKeyFactory;

    public RankingQueryService(RankingLeaderboardPort leaderboardPort, RankingKeyFactory rankingKeyFactory) {
        this.leaderboardPort = leaderboardPort;
        this.rankingKeyFactory = rankingKeyFactory;
    }

    @Override
    public TopRankingsResult getTop(RankingScope scope, int limit) {
        String key = rankingKeyFactory.build(scope);
        List<RankingEntry> entries = leaderboardPort.findTop(key, limit);
        return new TopRankingsResult(key, entries);
    }

    @Override
    public MyRankingResult getMyRank(RankingScope scope, String userId) {
        String key = rankingKeyFactory.build(scope);
        Optional<RankWithScore> rank = leaderboardPort.findRank(key, userId);
        return new MyRankingResult(key, rank);
    }

    @Override
    public AroundRankingResult getAround(RankingScope scope, String userId, int range) {
        String key = rankingKeyFactory.build(scope);
        List<RankingEntry> entries = leaderboardPort.findAround(key, userId, range);
        return new AroundRankingResult(key, entries);
    }
}
