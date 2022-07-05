package org.example.moviesapi.application;

import an.awesome.pipelinr.Command;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
public class LikeCommand implements Command<Boolean> {

    @NotEmpty
    @Getter
    private final String id;
}
