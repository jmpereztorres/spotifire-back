package com.spotifire.web.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotifire.core.service.IReportService;
import com.spotifire.web.rest.dto.NotificationDTO;
import com.spotifire.web.rest.dto.ReportRequestDTO;

@RestController
@RequestMapping(value = "/api/notifications", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@CrossOrigin
public class NotificationController {

	@Autowired
	private IReportService reportService;

	@PostMapping(value = "")
	public ResponseEntity<NotificationDTO> getNotification(@RequestBody ReportRequestDTO reportRequestDTO) {

		NotificationDTO notification = this.reportService.createPushNotification(reportRequestDTO);

		return new ResponseEntity<>(notification, HttpStatus.OK);

	}

}
