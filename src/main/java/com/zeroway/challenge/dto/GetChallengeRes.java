package com.zeroway.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChallengeRes {
    private Long challenge_id;
    private boolean complete;
    private String content;
}
