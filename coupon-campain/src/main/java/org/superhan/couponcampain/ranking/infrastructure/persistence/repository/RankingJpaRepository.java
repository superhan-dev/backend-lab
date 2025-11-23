package org.superhan.couponcampain.ranking.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.superhan.couponcampain.ranking.infrastructure.persistence.entity.RankingEntity;

public interface RankingJpaRepository extends JpaRepository<RankingEntity, Long> {
}
