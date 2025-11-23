package org.superhan.couponcampain.ranking.application.usecase;

import org.superhan.couponcampain.ranking.application.model.AroundRankingResult;
import org.superhan.couponcampain.ranking.application.model.MyRankingResult;
import org.superhan.couponcampain.ranking.application.model.TopRankingsResult;
import org.superhan.couponcampain.ranking.domain.model.RankingScope;

public interface RankingQueryUseCase {
    TopRankingsResult getTop(RankingScope scope, int limit);

    MyRankingResult getMyRank(RankingScope scope, String userId);

    AroundRankingResult getAround(RankingScope scope, String userId, int range);
}
