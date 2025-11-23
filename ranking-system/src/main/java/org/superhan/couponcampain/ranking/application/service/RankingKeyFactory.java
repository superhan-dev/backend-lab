package org.superhan.couponcampain.ranking.application.service;

import java.time.format.DateTimeFormatter;
import org.superhan.couponcampain.ranking.domain.model.RankingScope;
import org.superhan.couponcampain.ranking.domain.model.RankingType;
import org.springframework.stereotype.Component;

@Component
public class RankingKeyFactory {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public String build(RankingScope scope) {
        return switch (scope.type()) {
            case GLOBAL -> "ranking:global";
            case DAILY -> "ranking:daily:" + scope.date().orElseThrow(this::dateRequired).format(DATE_FORMATTER);
            case SEASON ->
                    "ranking:season:" + scope.seasonId().orElseThrow(this::seasonIdRequired);
            case MODE_DAILY ->
                    "ranking:mode:" + scope.mode().orElseThrow(this::modeRequired) + ":" + scope.date()
                            .orElseThrow(this::dateRequired)
                            .format(DATE_FORMATTER);
            case MODE_SEASON ->
                    "ranking:mode:" + scope.mode().orElseThrow(this::modeRequired) + ":season:" + scope.seasonId()
                            .orElseThrow(this::seasonIdRequired);
        };
    }

    private IllegalArgumentException dateRequired() {
        return new IllegalArgumentException("date is required for DAILY or MODE_DAILY rankings");
    }

    private IllegalArgumentException seasonIdRequired() {
        return new IllegalArgumentException("seasonId is required for SEASON or MODE_SEASON rankings");
    }

    private IllegalArgumentException modeRequired() {
        return new IllegalArgumentException("mode is required for MODE_* rankings");
    }
}
