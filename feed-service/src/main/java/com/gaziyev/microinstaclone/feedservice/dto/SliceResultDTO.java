package com.gaziyev.microinstaclone.feedservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SliceResultDTO<T> {

    private String pagingState;
    private boolean isLastPage;
    private List<T> content;
}
