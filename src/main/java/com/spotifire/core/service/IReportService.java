package com.spotifire.core.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.spotifire.persistence.pojo.Report;
import com.spotifire.web.rest.dto.ReportRequestDTO;

public interface IReportService {

	Report processReport(Report report);

	Report saveReport(Report report);

	void parseReportAndSave(ReportRequestDTO reportRequest, MultipartFile file) throws IOException;

}
