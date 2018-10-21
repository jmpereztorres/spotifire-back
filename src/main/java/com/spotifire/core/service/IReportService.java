package com.spotifire.core.service;

import java.io.IOException;

import com.spotifire.persistence.pojo.Report;
import com.spotifire.web.rest.dto.FireDTO;
import com.spotifire.web.rest.dto.NotificationDTO;
import com.spotifire.web.rest.dto.ReportRequestDTO;

public interface IReportService {

	Report processReport(Report report);

	Report saveReport(Report report);

	void parseReportAndSave(ReportRequestDTO reportRequest) throws IOException;

	FireDTO findFiresByLocation(ReportRequestDTO reportRequestDTO);

	NotificationDTO createPushNotification(ReportRequestDTO reportRequestDTO);
}
