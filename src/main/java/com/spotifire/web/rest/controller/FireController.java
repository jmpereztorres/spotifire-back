package com.spotifire.web.rest.controller;

import java.util.ArrayList;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spotifire.core.service.IReportService;
import com.spotifire.persistence.constants.SpotifireConstants;
import com.spotifire.persistence.pojo.Evidence;
import com.spotifire.persistence.pojo.Location;
import com.spotifire.web.rest.dto.FireDTO;
import com.spotifire.web.rest.dto.NasaFireDTO;
import com.spotifire.web.rest.dto.ReportRequestDTO;

@RestController
@RequestMapping(value = "/api/fires", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@CrossOrigin
public class FireController {

	@Autowired
	private IReportService reportService;

	@GetMapping(value = "")
	public ResponseEntity<FireDTO> getFires() {

		FireDTO fireDTO = new FireDTO();
		fireDTO.setEvidences(new ArrayList<>());
		fireDTO.setNasaFires(new ArrayList<>());

		Evidence evidence = new Evidence();
		evidence.setConfidence(80);
		evidence.setCreationDate(Calendar.getInstance().getTime());
		evidence.setImpact(90);

		NasaFireDTO nasaFireDTO = new NasaFireDTO();
		nasaFireDTO.setCreationDate(Calendar.getInstance().getTime());

		Location location = new Location();
		location.setLatitude(-4.069);
		location.setLatitude(144.342);

		evidence.setLocation(location);
		nasaFireDTO.setLocation(location);

		fireDTO.getEvidences().add(evidence);
		fireDTO.getNasaFires().add(nasaFireDTO);

		return new ResponseEntity<>(fireDTO, HttpStatus.OK);

	}

	@RequestMapping(value = "/", method = RequestMethod.POST, produces = { SpotifireConstants.REST_ACCEPT_APPLICATION_JSON_UTF_8 })
	public ResponseEntity<FireDTO> getFireByLocation(@RequestBody ReportRequestDTO reportRequestDTO) {
		FireDTO fireDTO = this.reportService.findFiresByLocation(reportRequestDTO);
		return new ResponseEntity<>(fireDTO, HttpStatus.OK);
	}

}
