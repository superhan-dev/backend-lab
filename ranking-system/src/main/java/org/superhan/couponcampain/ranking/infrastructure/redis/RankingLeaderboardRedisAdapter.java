package org.superhan.couponcampain.ranking.infrastructure.redis;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.superhan.couponcampain.ranking.domain.model.RankWithScore;
import org.superhan.couponcampain.ranking.domain.model.RankingEntry;
import org.superhan.couponcampain.ranking.domain.port.RankingLeaderboardPort;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@Component
public class RankingLeaderboardRedisAdapter implements RankingLeaderboardPort {

    private final StringRedisTemplate redisTemplate;

    public RankingLeaderboardRedisAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void upsertBestScore(String key, String userId, double score) {
        redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            byte[] rawKey = raw(key);
            byte[] rawMember = raw(userId);
            if (rawKey == null || rawMember == null) {
                return Boolean.FALSE;
            }
            return connection.zAdd(rawKey, score, rawMember, RedisZSetCommands.ZAddArgs.ifExists().gt());
        });
    }

    @Override
    public double incrementScore(String key, String userId, double delta) {
        Double updated = redisTemplate.opsForZSet().incrementScore(key, userId, delta);
        return updated != null ? updated : 0.0d;
    }

    @Override
    public List<RankingEntry> findTop(String key, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, limit - 1);
        if (tuples == null || tuples.isEmpty()) {
            return Collections.emptyList();
        }
        List<RankingEntry> entries = new ArrayList<>(tuples.size());
        long rank = 1;
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            entries.add(new RankingEntry(
                    tuple.getValue(),
                    tuple.getScore() != null ? tuple.getScore() : 0.0d,
                    rank++
            ));
        }
        return entries;
    }

    @Override
    public Optional<RankWithScore> findRank(String key, String userId) {
        Long rank = redisTemplate.opsForZSet().reverseRank(key, userId);
        Double score = redisTemplate.opsForZSet().score(key, userId);
        if (rank == null || score == null) {
            return Optional.empty();
        }
        return Optional.of(new RankWithScore(rank + 1, score));
    }

    @Override
    public List<RankingEntry> findAround(String key, String userId, int range) {
        Long myRank = redisTemplate.opsForZSet().reverseRank(key, userId);
        if (myRank == null) {
            return Collections.emptyList();
        }
        long start = Math.max(myRank - range, 0);
        long end = myRank + range;
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, start, end);
        if (tuples == null || tuples.isEmpty()) {
            return Collections.emptyList();
        }
        List<RankingEntry> entries = new ArrayList<>(tuples.size());
        long rankCursor = start + 1;
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            entries.add(new RankingEntry(
                    tuple.getValue(),
                    tuple.getScore() != null ? tuple.getScore() : 0.0d,
                    rankCursor++
            ));
        }
        return entries;
    }

    private byte[] raw(String value) {
        if (Objects.isNull(value)) {
            return null;
        }
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
