package com.gaziyev.microinstaclone.graphservice.payload;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResult<T> {

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private List<T> contents;
}
