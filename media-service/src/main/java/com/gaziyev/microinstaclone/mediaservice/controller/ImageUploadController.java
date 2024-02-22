package com.gaziyev.microinstaclone.mediaservice.controller;

import com.gaziyev.microinstaclone.mediaservice.entity.ImageMetadata;
import com.gaziyev.microinstaclone.mediaservice.dto.UploadFileResponseDTO;
import com.gaziyev.microinstaclone.mediaservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ImageUploadController {

	private final ImageService imageService;

	private static final String POST_IMAGE = "/images/upload";

	@PostMapping(POST_IMAGE)
	@PreAuthorize("hasRole('USER')")
	public UploadFileResponseDTO uploadFile(@RequestParam("image") MultipartFile file,
											Authentication authentication
	) {
		String filename = StringUtils.cleanPath(
				Objects.requireNonNull(file.getOriginalFilename())
		);
		log.info("received a request to upload file {} for user {}", filename, authentication.getPrincipal());

		ImageMetadata metadata = imageService.upload(file,  authentication.getPrincipal().toString());
		return new UploadFileResponseDTO(metadata.getFilename(), metadata.getUri(),
		                              metadata.getFileType());
	}
}
