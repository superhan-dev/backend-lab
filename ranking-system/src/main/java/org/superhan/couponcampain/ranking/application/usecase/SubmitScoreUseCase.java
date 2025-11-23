package org.superhan.couponcampain.ranking.application.usecase;

import org.superhan.couponcampain.ranking.application.model.SubmitScoreCommand;
import org.superhan.couponcampain.ranking.application.model.SubmitScoreResult;

public interface SubmitScoreUseCase {
    SubmitScoreResult submit(SubmitScoreCommand command);
}
