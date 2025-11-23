package org.superhan.couponcampain.ranking.presentation;

import jakarta.validation.Valid;
import java.time.Instant;
import org.superhan.couponcampain.ranking.application.model.SubmitScoreCommand;
import org.superhan.couponcampain.ranking.application.model.SubmitScoreResult;
import org.superhan.couponcampain.ranking.application.usecase.SubmitScoreUseCase;
import org.superhan.couponcampain.ranking.presentation.request.SubmitScoreRequest;
import org.superhan.couponcampain.ranking.presentation.response.SubmitScoreResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rankings")
@Validated
public class RankingCommandController {

    private final SubmitScoreUseCase submitScoreUseCase;

    public RankingCommandController(SubmitScoreUseCase submitScoreUseCase) {
        this.submitScoreUseCase = submitScoreUseCase;
    }

    @PostMapping
    public ResponseEntity<SubmitScoreResponse> submitScore(@Valid @RequestBody SubmitScoreRequest request) {
        SubmitScoreCommand command = new SubmitScoreCommand(
                request.userId(),
                request.score(),
                request.playAt() != null ? request.playAt() : Instant.now(),
                request.toScope(),
                request.policy()
        );
        SubmitScoreResult result = submitScoreUseCase.submit(command);
        return ResponseEntity.ok(SubmitScoreResponse.from(result));
    }
}
