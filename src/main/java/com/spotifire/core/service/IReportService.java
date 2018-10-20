package com.spotifire.core.service;

import java.util.List;

import com.spotifire.persistence.pojo.Report;

public interface IReportService {

	Report processReport(Report report);

	List<Report> listReports(Report report);

}
