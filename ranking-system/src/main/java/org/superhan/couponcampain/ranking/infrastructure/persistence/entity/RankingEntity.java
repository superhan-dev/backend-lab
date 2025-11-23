package org.superhan.couponcampain.ranking.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "rankings")
public class RankingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "score", nullable = false)
    private Long score;

    @Column(name = "play_at", nullable = false)
    private Instant playAt;

    @Column(name = "mode")
    private String mode;

    @Column(name = "season_id")
    private Long seasonId;

    @Column(name = "policy", nullable = false)
    private String policy;

    protected RankingEntity() {
        // for JPA
    }

    public RankingEntity(String userId, Long score, Instant playAt, String mode, Long seasonId, String policy) {
        this.userId = userId;
        this.score = score;
        this.playAt = playAt;
        this.mode = mode;
        this.seasonId = seasonId;
        this.policy = policy;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Long getScore() {
        return score;
    }

    public Instant getPlayAt() {
        return playAt;
    }

    public String getMode() {
        return mode;
    }

    public Long getSeasonId() {
        return seasonId;
    }

    public String getPolicy() {
        return policy;
    }
}
