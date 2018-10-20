package com.spotifire.core.service;

import com.spotifire.persistence.pojo.Report;
import com.spotifire.web.rest.dto.ReportRequestDTO;

public interface IReportService {

	Report processReport(Report report);

	public Report saveReport(Report report);

	public void parseReportAndSave(ReportRequestDTO reportRequest);
}
