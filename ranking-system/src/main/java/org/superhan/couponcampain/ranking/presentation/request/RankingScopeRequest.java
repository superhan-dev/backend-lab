package org.superhan.couponcampain.ranking.presentation.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.superhan.couponcampain.ranking.domain.model.RankingScope;
import org.superhan.couponcampain.ranking.domain.model.RankingType;
import org.springframework.format.annotation.DateTimeFormat;

public record RankingScopeRequest(
        @NotNull RankingType type,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        String mode,
        Long seasonId
) {
    public RankingScope toScope() {
        return RankingScope.of(type, date, mode, seasonId);
    }
}
