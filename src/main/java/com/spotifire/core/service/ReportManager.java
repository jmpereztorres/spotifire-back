package com.spotifire.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spotifire.persistence.pojo.Report;
import com.spotifire.persistence.repository.ITransactionalRepository;

@Service
public class ReportManager implements IReportService {

	@Autowired
	private ITransactionalRepository transactionalService;

	@Override
	public void report(Report report) {

	}

}
