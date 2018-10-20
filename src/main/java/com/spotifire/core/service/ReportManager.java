package com.spotifire.core.service;

import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.spotifire.core.utils.ImageUtils;
import com.spotifire.persistence.constants.SourceType;
import com.spotifire.persistence.pojo.Author;
import com.spotifire.persistence.pojo.Location;
import com.spotifire.persistence.pojo.Report;
import com.spotifire.persistence.repository.ITransactionalRepository;
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

		return this.saveReport(report);
	}

	@Override
	public Report saveReport(Report report) {
		return this.transactionalRepository.save(report);
	}

	@Override
	public void parseReportAndSave(ReportRequestDTO reportRequest, MultipartFile file) throws IOException {

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

		byte[] bytes;

		try {
			bytes = IOUtils.toByteArray(file.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

		report.setImage(bytes);

		this.processReport(report);

	}

}
