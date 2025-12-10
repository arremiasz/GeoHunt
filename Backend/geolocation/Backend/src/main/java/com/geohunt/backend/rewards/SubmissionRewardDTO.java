package com.geohunt.backend.rewards;

import lombok.Data;

@Data
public class SubmissionRewardDTO {
    private Reward reward;
    private int submissionValue;
}
