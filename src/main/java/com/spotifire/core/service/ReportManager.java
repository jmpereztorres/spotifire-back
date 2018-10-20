package com.spotifire.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spotifire.core.utils.ImageUtils;
import com.spotifire.core.utils.SpotifireUtils;
import com.spotifire.persistence.constants.SourceType;
import com.spotifire.persistence.pojo.Author;
import com.spotifire.persistence.pojo.Evidence;
import com.spotifire.persistence.pojo.Location;
import com.spotifire.persistence.pojo.Report;
import com.spotifire.persistence.repository.ITransactionalRepository;
import com.spotifire.web.rest.dto.FireDTO;
import com.spotifire.web.rest.dto.ReportRequestDTO;

@Service
public class ReportManager implements IReportService {

	@Autowired
	private ITransactionalRepository transactionalRepository;

	@Override
	public Report processReport(Report report) {

		if (report.getImage() != null) {
			report.setImageScore(ImageUtils.scoringImage(report.getImage()));
		}

		List<Evidence> evidences = this.transactionalRepository.findByExample(new Evidence());

		Evidence persisted = null;
		if (evidences != null) {
			persisted = evidences.stream().filter(evidence -> SpotifireUtils.distance(evidence.getLocation(), report.getLocation()) < 15000)
					.sorted((evidence1, evidence2) ->

					Double.compare(SpotifireUtils.distance(evidence1.getLocation(), report.getLocation()),
							SpotifireUtils.distance(evidence2.getLocation(), report.getLocation())))
					.findFirst().orElse(null);

		}

		report.setEvidence(persisted != null ? persisted : this.createEvidence(report));

		return this.saveReport(report);
	}

	private Evidence createEvidence(Report report) {
		Evidence evidence = new Evidence();

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
						report.getLocation().getLatitude(), reportRequestDTO.getLongitude(), 0d, 0d) < 50000);

		fireDTO.setEvidences(supplier.get().filter(report -> report.getConfidence() >= 70).collect(Collectors.toList()));
		fireDTO.setAlerts(supplier.get().filter(report -> report.getConfidence() < 70).collect(Collectors.toList()));

		return fireDTO;
	}

}
