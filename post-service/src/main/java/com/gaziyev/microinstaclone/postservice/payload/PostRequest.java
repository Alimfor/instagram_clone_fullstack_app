package com.gaziyev.microinstaclone.postservice.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {

	private String imageUrl;
	private String caption;
}
