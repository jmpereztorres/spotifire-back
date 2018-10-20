package com.spotifire.web.rest.controller;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class ReportController {

	private static final Logger LOGGER = LogManager.getLogger(ReportController.class);

	@Autowired
	private IReportService reportService;

	@PostMapping(value = "")
	public ResponseEntity<FireDTO> createReport(@RequestBody ReportRequestDTO reportRequest) {

		System.out.println(reportRequest);
		LOGGER.debug(reportRequest);

		return new ResponseEntity<>(HttpStatus.OK);

	}

	@PostMapping(value = "/upload")
	public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {

		LOGGER.debug(file);

		return new ResponseEntity<>(UUID.randomUUID().toString(), HttpStatus.OK);

	}

}
