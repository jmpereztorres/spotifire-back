package com.spotifire.web.rest.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spotifire.core.service.IReportService;
import com.spotifire.web.rest.dto.FireDTO;
import com.spotifire.web.rest.dto.ReportRequestDTO;

@RestController
@RequestMapping(value = "/api/reports", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@CrossOrigin
public class ReportController {

	private static final Logger LOGGER = LogManager.getLogger(ReportController.class);

	@Autowired
	private IReportService reportService;

	@PostMapping(value = "")
	public ResponseEntity<FireDTO> createReport(@RequestBody ReportRequestDTO reportRequest) throws IOException {

		System.out.println(reportRequest);
		LOGGER.debug(reportRequest);

		Path dir = Paths.get("pictures");
		File file = Paths.get(dir + File.separator + reportRequest.getImageId()).toFile();

		if (file.exists()) {
			InputStream is = new FileInputStream(file);
			this.reportService.parseReportAndSave(reportRequest, IOUtils.toByteArray(is));
		}

		return new ResponseEntity<>(HttpStatus.OK);

	}

	@PostMapping(value = "/upload")
	public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {

		String imageId = UUID.randomUUID().toString() + "_" + Calendar.getInstance().getTimeInMillis() + "."
				+ FilenameUtils.getExtension(file.getOriginalFilename());

		Path dir = Paths.get("pictures");
		if (!dir.toFile().exists()) {
			Files.createDirectory(dir);
		}

		Path fileSaved = Paths.get(dir + File.separator + imageId);

		System.out.println("Saving file " + imageId);

		Files.copy(file.getInputStream(), fileSaved);

		return new ResponseEntity<>(imageId, HttpStatus.OK);

	}

}
