package org.superhan.couponcampain.ranking.application.service;

import java.util.Optional;
import org.superhan.couponcampain.ranking.application.model.SubmitScoreCommand;
import org.superhan.couponcampain.ranking.application.model.SubmitScoreResult;
import org.superhan.couponcampain.ranking.application.usecase.SubmitScoreUseCase;
import org.superhan.couponcampain.ranking.domain.model.RankWithScore;
import org.superhan.couponcampain.ranking.domain.model.RankingRecord;
import org.superhan.couponcampain.ranking.domain.model.ScorePolicy;
import org.superhan.couponcampain.ranking.domain.port.RankingLeaderboardPort;
import org.superhan.couponcampain.ranking.domain.port.RankingRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmitScoreService implements SubmitScoreUseCase {

    private static final Logger log = LoggerFactory.getLogger(SubmitScoreService.class);

    private final RankingRecordRepository recordRepository;
    private final RankingLeaderboardPort leaderboardPort;
    private final RankingKeyFactory rankingKeyFactory;

    public SubmitScoreService(RankingRecordRepository recordRepository,
                              RankingLeaderboardPort leaderboardPort,
                              RankingKeyFactory rankingKeyFactory) {
        this.recordRepository = recordRepository;
        this.leaderboardPort = leaderboardPort;
        this.rankingKeyFactory = rankingKeyFactory;
    }

    @Override
    @Transactional
    public SubmitScoreResult submit(SubmitScoreCommand command) {
        String key = rankingKeyFactory.build(command.scope());
        RankingRecord record = new RankingRecord(
                command.userId(),
                command.score(),
                command.playAt(),
                command.scope().mode(),
                command.scope().seasonId(),
                command.policy()
        );

        recordRepository.save(record);
        try {
            if (command.policy() == ScorePolicy.BEST) {
                leaderboardPort.upsertBestScore(key, command.userId(), command.score());
            } else {
                leaderboardPort.incrementScore(key, command.userId(), command.score());
            }
        } catch (RuntimeException ex) {
            log.warn("Redis leaderboard update failed: key={}, policy={}, user={}",
                    key, command.policy(), command.userId(), ex);
        }

        Optional<RankWithScore> rank = leaderboardPort.findRank(key, command.userId());
        return new SubmitScoreResult(key, command.policy(), rank);
    }
}
