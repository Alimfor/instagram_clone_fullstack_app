package com.gaziyev.microinstaclone.mediaservice.controller;

import com.gaziyev.microinstaclone.mediaservice.entity.ImageMetadata;
import com.gaziyev.microinstaclone.mediaservice.payload.UploadFileResponse;
import com.gaziyev.microinstaclone.mediaservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ImageUploadController {

	private final ImageService imageService;

	@PostMapping("/images")
	@PreAuthorize("hasRole('USER')")
	public UploadFileResponse uploadFile(@RequestParam("image") MultipartFile file,
	                                     Authentication authentication
	) {
		String filename = StringUtils.cleanPath(
				Objects.requireNonNull(file.getOriginalFilename())
		);
		log.info("received a request to upload file {} for user {}", filename, authentication.getPrincipal());

		ImageMetadata metadata = imageService.upload(file,  authentication.getPrincipal().toString());
		return new UploadFileResponse(metadata.getFilename(), metadata.getUri(),
		                              metadata.getFileType());
	}
}
