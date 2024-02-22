package com.gaziyev.microinstaclone.feedservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedResultDTO<T> {

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private List<T> contents;
}
