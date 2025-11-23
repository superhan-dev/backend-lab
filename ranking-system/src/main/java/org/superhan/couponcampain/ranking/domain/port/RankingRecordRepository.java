package org.superhan.couponcampain.ranking.domain.port;

import org.superhan.couponcampain.ranking.domain.model.RankingRecord;

public interface RankingRecordRepository {
    void save(RankingRecord record);
}
