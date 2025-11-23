package org.superhan.couponcampain.ranking.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.superhan.couponcampain.ranking.application.model.AroundRankingResult;
import org.superhan.couponcampain.ranking.application.model.MyRankingResult;
import org.superhan.couponcampain.ranking.application.model.TopRankingsResult;
import org.superhan.couponcampain.ranking.application.usecase.RankingQueryUseCase;
import org.superhan.couponcampain.ranking.presentation.request.RankingScopeRequest;
import org.superhan.couponcampain.ranking.presentation.response.AroundRankResponse;
import org.superhan.couponcampain.ranking.presentation.response.MyRankResponse;
import org.superhan.couponcampain.ranking.presentation.response.TopRankingsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rankings")
@Validated
public class RankingQueryController {

    private final RankingQueryUseCase rankingQueryUseCase;

    public RankingQueryController(RankingQueryUseCase rankingQueryUseCase) {
        this.rankingQueryUseCase = rankingQueryUseCase;
    }

    @GetMapping("/top")
    public ResponseEntity<TopRankingsResponse> top(
            @Valid @ModelAttribute RankingScopeRequest scopeRequest,
            @RequestParam(defaultValue = "10") @Min(1) @Max(500) int limit
    ) {
        TopRankingsResult result = rankingQueryUseCase.getTop(scopeRequest.toScope(), limit);
        return ResponseEntity.ok(TopRankingsResponse.from(result));
    }

    @GetMapping("/me")
    public ResponseEntity<MyRankResponse> myRank(
            @Valid @ModelAttribute RankingScopeRequest scopeRequest,
            @RequestParam @NotBlank String userId
    ) {
        MyRankingResult result = rankingQueryUseCase.getMyRank(scopeRequest.toScope(), userId);
        return ResponseEntity.ok(MyRankResponse.from(result));
    }

    @GetMapping("/around-me")
    public ResponseEntity<AroundRankResponse> aroundMe(
            @Valid @ModelAttribute RankingScopeRequest scopeRequest,
            @RequestParam @NotBlank String userId,
            @RequestParam(defaultValue = "2") @Min(0) @Max(50) int range
    ) {
        AroundRankingResult result = rankingQueryUseCase.getAround(scopeRequest.toScope(), userId, range);
        return ResponseEntity.ok(AroundRankResponse.from(result));
    }
}
