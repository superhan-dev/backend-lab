package org.superhan.couponcampain.ranking.domain.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public record RankingScope(
        RankingType type,
        Optional<LocalDate> date,
        Optional<String> mode,
        Optional<Long> seasonId
) {

    public RankingScope {
        Objects.requireNonNull(type, "type is required");
        date = date != null ? date : Optional.empty();
        mode = normalizeMode(mode);
        seasonId = seasonId != null ? seasonId : Optional.empty();
    }

    public static RankingScope of(RankingType type, LocalDate date, String mode, Long seasonId) {
        return new RankingScope(
                type,
                Optional.ofNullable(date),
                Optional.ofNullable(mode),
                Optional.ofNullable(seasonId)
        );
    }

    private static Optional<String> normalizeMode(Optional<String> value) {
        if (value == null) {
            return Optional.empty();
        }
        return value.map(String::trim).filter(s -> !s.isEmpty());
    }
}
