package com.spotifire.web.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spotifire.core.service.IReportService;
import com.spotifire.persistence.constants.SpotifireConstants;
import com.spotifire.web.rest.dto.FireDTO;
import com.spotifire.web.rest.dto.ReportRequestDTO;

@RestController
@RequestMapping(value = "/api/fires", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@CrossOrigin
public class FireController {

	@Autowired
	private IReportService reportService;

	@RequestMapping(value = "", method = RequestMethod.POST, produces = { SpotifireConstants.REST_ACCEPT_APPLICATION_JSON_UTF_8 })
	public ResponseEntity<FireDTO> getFireByLocation(@RequestBody ReportRequestDTO reportRequestDTO) {
		FireDTO fireDTO = this.reportService.findFiresByLocation(reportRequestDTO);
		return new ResponseEntity<>(fireDTO, HttpStatus.OK);
	}

	@RequestMapping(value = "/typed", method = RequestMethod.POST, produces = { SpotifireConstants.REST_ACCEPT_APPLICATION_JSON_UTF_8 })
	public ResponseEntity<FireDTO> getFireByLocationTyped(@RequestBody ReportRequestDTO reportRequestDTO) {
		FireDTO fireDTO = this.reportService.findTypedFiresByLocation(reportRequestDTO);
		return new ResponseEntity<>(fireDTO, HttpStatus.OK);
	}

}
