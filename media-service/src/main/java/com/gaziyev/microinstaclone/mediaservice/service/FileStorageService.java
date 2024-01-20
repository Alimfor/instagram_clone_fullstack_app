package com.gaziyev.microinstaclone.mediaservice.service;

import com.gaziyev.microinstaclone.mediaservice.entity.ImageMetadata;
import com.gaziyev.microinstaclone.mediaservice.exception.InvalidFileException;
import com.gaziyev.microinstaclone.mediaservice.exception.InvalidFileNameException;
import com.gaziyev.microinstaclone.mediaservice.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

	@Value("${file.upload-dir}")
	private String uploadDirectory;

	@Value("${file.path.prefix}")
	private String filePathPrefix;

	private final Environment environment;

	public ImageMetadata storeFile(MultipartFile file, String username) {
		String filename = StringUtils.cleanPath(
				Objects.requireNonNull(
						file.getOriginalFilename()
				)
		);
		log.info("storing file {}", filename);

		try {

			if (file.isEmpty()) {
				log.warn("filed to store empty file {}", filename);
				throw new InvalidFileException("Filed to store empty file");
			}

			if (filename.contains("..")) {
				log.warn("file {} contains invalid path sequence", filename);
				throw new InvalidFileNameException(
						"Cannot store file with relative path outside current directory " + filename
				);
			}

			String extension = FilenameUtils.getExtension(filename);
			String newFilename = UUID.randomUUID() + "." + extension;

			try (InputStream inputStream = file.getInputStream()) {
				Path userDir = Paths.get(uploadDirectory + username);

				if (Files.notExists(userDir)) {
					Files.createDirectory(userDir);
				}

				Files.copy(inputStream, userDir.resolve(newFilename),
				           StandardCopyOption.REPLACE_EXISTING
				);
			}

			String port = environment.getProperty("local.server.port");
			String hostName = InetAddress.getLocalHost().getHostName();

			String fileUrl = String.format("http://%s:%s%s/%s/%s",
			                               hostName,port, filePathPrefix, username, newFilename);

			log.info("successfully stored file {} location {}", filename, fileUrl);

			return new ImageMetadata(
					newFilename, fileUrl,
					Objects.requireNonNull(
							file.getContentType()
					)
			);
		} catch (IOException ex) {
			log.error("failed to store file {} error: {}", filename, ex.getMessage());
			throw new StorageException("Filed to store file " + filename, ex);
		}
	}
}
