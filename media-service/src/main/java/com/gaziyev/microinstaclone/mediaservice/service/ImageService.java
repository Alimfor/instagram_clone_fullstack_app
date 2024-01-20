package com.gaziyev.microinstaclone.mediaservice.service;

import com.gaziyev.microinstaclone.mediaservice.entity.ImageMetadata;
import com.gaziyev.microinstaclone.mediaservice.repository.ImageMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

	private final FileStorageService fileStorageService;
	private final ImageMetadataRepository imageMetadataRepository;

	public ImageMetadata upload(MultipartFile file, String username) {
		String filename = StringUtils.cleanPath(
				Objects.requireNonNull(file.getOriginalFilename())
		);

		log.info("storing file {}", filename);

		ImageMetadata metadata = fileStorageService.storeFile(file, username);
		return imageMetadataRepository.save(metadata);
	}
}
