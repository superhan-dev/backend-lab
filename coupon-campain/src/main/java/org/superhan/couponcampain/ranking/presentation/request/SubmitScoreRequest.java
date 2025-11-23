package org.superhan.couponcampain.ranking.presentation.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import org.superhan.couponcampain.ranking.domain.model.RankingScope;
import org.superhan.couponcampain.ranking.domain.model.RankingType;
import org.superhan.couponcampain.ranking.domain.model.ScorePolicy;
import org.springframework.format.annotation.DateTimeFormat;

public record SubmitScoreRequest(
        @NotBlank String userId,
        @NotNull @Min(0) Long score,
        @JsonFormat(shape = JsonFormat.Shape.STRING) Instant playAt,
        @NotNull RankingType type,
        String mode,
        Long seasonId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @NotNull ScorePolicy policy
) {
    public RankingScope toScope() {
        return RankingScope.of(type, date, mode, seasonId);
    }
}
