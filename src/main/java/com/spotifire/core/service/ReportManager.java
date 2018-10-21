package com.spotifire.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spotifire.core.utils.ImageUtils;
import com.spotifire.core.utils.SpotifireUtils;
import com.spotifire.persistence.constants.AlertLevel;
import com.spotifire.persistence.constants.ReportType;
import com.spotifire.persistence.constants.SourceType;
import com.spotifire.persistence.pojo.Author;
import com.spotifire.persistence.pojo.Evidence;
import com.spotifire.persistence.pojo.Location;
import com.spotifire.persistence.pojo.Report;
import com.spotifire.persistence.repository.ITransactionalRepository;
import com.spotifire.web.rest.client.IGeoLocationClientService;
import com.spotifire.web.rest.dto.FireDTO;
import com.spotifire.web.rest.dto.GeolocationDTO;
import com.spotifire.web.rest.dto.ReportRequestDTO;

@Service
public class ReportManager implements IReportService {

	private static final Logger LOGGER = LogManager.getLogger(ReportManager.class);

	@Autowired
	private ITransactionalRepository transactionalRepository;

	@Autowired
	private IGeoLocationClientService geolocationClientService;

	@Override
	public Report processReport(Report report) {

		Report res = null;
		if (report.getLocation() == null) {
			LOGGER.error("ERROR: Report without location");
		} else {

			if (report.getImage() != null) {
				report.setImageScore(ImageUtils.scoringImage(report.getImage()));
			}

			List<Evidence> evidences = this.transactionalRepository.findByExample(new Evidence());

			Evidence persisted = null;
			if (evidences != null) {
				persisted = evidences.stream()
						.filter(evidence -> SpotifireUtils.distance(evidence.getLocation(), report.getLocation()) < 15000)
						.sorted((evidence1, evidence2) ->

						Double.compare(SpotifireUtils.distance(evidence1.getLocation(), report.getLocation()),
								SpotifireUtils.distance(evidence2.getLocation(), report.getLocation())))
						.findFirst().orElse(null);

			}

			report.setEvidence(persisted != null ? persisted : this.createEvidence(report));

			res = this.saveReport(report);

			if (persisted != null) {
				this.updateEvidence(persisted.getId());
			}

		}
		return res;
	}

	private void updateEvidence(Long evidenceId) {
		Evidence evidence = this.transactionalRepository.getObjectById(evidenceId, Evidence.class);
	}

	private Evidence createEvidence(Report report) {
		Evidence evidence = new Evidence();
		evidence.setLocation(report.getLocation());
		evidence.setCreationDate(new Date());
		evidence.setType(ReportType.FIRE);

		GeolocationDTO geolocation = geolocationClientService.locateCoordenates(report.getLocation().getLatitude(),
				report.getLocation().getLongitude());

		if (geolocation != null) {
			evidence.getLocation().setProvince(geolocation.getAddress().getCounty());
			evidence.getLocation().setCountry(geolocation.getAddress().getCountry());
		}

		return evidence;
	}

	@Override
	public Report saveReport(Report report) {
		return this.transactionalRepository.save(report);
	}

	@Override
	public void parseReportAndSave(ReportRequestDTO reportRequest) throws IOException {

		Location location = new Location();
		location.setLatitude(reportRequest.getLatitude());
		location.setLongitude(reportRequest.getLongitude());

		Author author = new Author();
		author.setAlias(reportRequest.getDescription());
		author.setSource(SourceType.SPOTIFIRE);
		author.setCreationDate(Calendar.getInstance().getTime());

		Report report = new Report();
		report.setLocation(location);
		report.setAuthor(author);
		report.setCreationDate(Calendar.getInstance().getTime());
		report.setDescription(reportRequest.getDescription());
		report.setSource(SourceType.SPOTIFIRE);

		if (reportRequest.getImageId() != null || reportRequest.getImageId() != "") {
			Path dir = Paths.get("pictures");
			File file = Paths.get(dir + File.separator + reportRequest.getImageId()).toFile();

			if (file.exists()) {
				InputStream is = new FileInputStream(file);
				report.setImage(IOUtils.toByteArray(is));
				report.setHasImage(true);
			}
		} else {
			report.setHasImage(false);
		}

		this.processReport(report);

	}

	@Override
	public FireDTO findFiresByLocation(ReportRequestDTO reportRequestDTO) {
		FireDTO fireDTO = new FireDTO();
		List<Evidence> evidenceList = this.transactionalRepository.findByExample(new Evidence());

		Supplier<Stream<Evidence>> supplier = () -> evidenceList.stream()
				.filter(report -> SpotifireUtils.distance(report.getLocation().getLatitude(), reportRequestDTO.getLatitude(),
						report.getLocation().getLatitude(), reportRequestDTO.getLongitude(), 0d, 0d) < 2000000000)

				.map(evidence -> fillDistance(reportRequestDTO, evidence));

		fireDTO.setEvidences(supplier.get().filter(report -> report.getConfidence() >= 70).collect(Collectors.toList()));
		fireDTO.setAlerts(supplier.get().filter(report -> report.getConfidence() < 70).collect(Collectors.toList()));

		return fireDTO;
	}

	@Override
	public FireDTO findTypedFiresByLocation(ReportRequestDTO reportRequestDTO) {
		FireDTO fireDTO = new FireDTO();
		List<Evidence> evidenceList = this.transactionalRepository.findByExample(new Evidence());

		fireDTO.setEvidences(evidenceList.stream()
				.filter(evidence -> SpotifireUtils.distance(evidence.getLocation().getLatitude(), reportRequestDTO.getLatitude(),
						evidence.getLocation().getLatitude(), reportRequestDTO.getLongitude(), 0d, 0d) < 50000000)

				.map(evidence -> fillDistance(reportRequestDTO, evidence)).map(ReportManager::fillAlertType)
				.sorted((evidence1, evidence2) -> Double.compare(evidence1.getDistance(), evidence2.getDistance()))
				.collect(Collectors.toList()));

		return fireDTO;
	}

	private static Evidence fillDistance(ReportRequestDTO reportRequestDTO, Evidence evidence) {
		evidence.setDistance(SpotifireUtils.distance(reportRequestDTO.getLatitude(), evidence.getLocation().getLatitude(),
				reportRequestDTO.getLongitude(), evidence.getLocation().getLongitude(), 0, 0));

		return evidence;
	}

	private static Evidence fillAlertType(Evidence evidence) {
		evidence.setLevel(evidence.getConfidence() >= 70 ? AlertLevel.FIRE : AlertLevel.ALERT);

		return evidence;
	}
}
