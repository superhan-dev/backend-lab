CREATE TABLE rankings (
                          id         BIGSERIAL PRIMARY KEY,
                          user_id    BIGINT       NOT NULL,
                          score      INTEGER      NOT NULL,
                          play_at    TIMESTAMPTZ  NOT NULL,
                          mode       VARCHAR(50),
                           season_id  BIGINT,
                          policy     VARCHAR(20)  NOT NULL,
                          created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_rankings_user_id
    ON rankings(user_id);

CREATE INDEX idx_rankings_play_at
    ON rankings(play_at);

CREATE INDEX idx_rankings_mode_season
    ON rankings(mode, season_id, play_at DESC);

CREATE TABLE ranking_snapshot (
                                  id          BIGSERIAL PRIMARY KEY,
                                  season_id   BIGINT,
                                  mode        VARCHAR(50),
                                  snapshot_at TIMESTAMP NOT NULL,
                                  rank        INT NOT NULL,
                                  user_id     BIGINT NOT NULL,
                                  score       INT NOT NULL
);

CREATE INDEX idx_ranking_snapshot_season_mode ON ranking_snapshot(season_id, mode);
