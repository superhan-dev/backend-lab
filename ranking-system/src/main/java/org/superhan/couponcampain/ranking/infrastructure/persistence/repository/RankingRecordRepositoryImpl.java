package org.superhan.couponcampain.ranking.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import org.superhan.couponcampain.ranking.domain.model.RankingRecord;
import org.superhan.couponcampain.ranking.domain.port.RankingRecordRepository;
import org.superhan.couponcampain.ranking.infrastructure.persistence.entity.RankingEntity;

@Repository
public class RankingRecordRepositoryImpl implements RankingRecordRepository {

    private final RankingJpaRepository rankingJpaRepository;

    public RankingRecordRepositoryImpl(RankingJpaRepository rankingJpaRepository) {
        this.rankingJpaRepository = rankingJpaRepository;
    }

    @Override
    public void save(RankingRecord record) {
        RankingEntity entity = new RankingEntity(
                record.userId(),
                record.score(),
                record.playAt(),
                record.mode().orElse(null),
                record.seasonId().orElse(null),
                record.policy().name()
        );
        rankingJpaRepository.save(entity);
    }
}
