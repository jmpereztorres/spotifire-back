package com.spotifire.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spotifire.core.utils.ImageUtils;
import com.spotifire.persistence.pojo.Report;
import com.spotifire.persistence.repository.ITransactionalRepository;

@Service
public class ReportManager implements IReportService {

	@Autowired
	private ITransactionalRepository transactionalRepository;

	@Override
	public Report processReport(Report report) {
		if (report.getImage() != null) {
			report.setImageScore(ImageUtils.scoringImage(report.getImage()));
		}
		return this.transactionalRepository.save(report);
	}

	@Override
	public List<Report> listReports(Report report) {
		return this.transactionalRepository.findByExample(report);
	}

}
