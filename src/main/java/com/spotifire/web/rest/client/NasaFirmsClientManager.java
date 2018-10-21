package com.spotifire.web.rest.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.spotifire.core.service.IReportService;
import com.spotifire.core.utils.SpotifireUtils;
import com.spotifire.persistence.constants.ReportType;
import com.spotifire.persistence.constants.SourceType;
import com.spotifire.persistence.constants.SpotifireConstants;
import com.spotifire.persistence.pojo.Location;
import com.spotifire.persistence.pojo.Report;
import com.spotifire.persistence.repository.ITransactionalRepository;

@Service
public class NasaFirmsClientManager implements INasaFirmsClientService {

	private static final Logger LOGGER = LogManager.getLogger(NasaFirmsClientManager.class);

	@Autowired
	private ITransactionalRepository transactionalRepository;

	@Autowired
	private IReportService reportService;

	@Autowired
	@Qualifier("nasaRestTemplate")
	private RestTemplate trustedRestTemplate;

	@Override
	public void fetchData() throws URISyntaxException {
		URI uriPetition = new URI(SpotifireConstants.NASA_FIRMS_URL);
		ResponseEntity<String> response = trustedRestTemplate.exchange(uriPetition, HttpMethod.GET, null, String.class);

		List<Report> reportList = new ArrayList<>();
		String[] lines = response.getBody().split("\n");

		if (lines != null) {
			Arrays.asList(lines).forEach(line -> {
				String[] columns = line.split(SpotifireConstants.OPERATOR_COMMA);
				Report report = new Report();
				if (columns != null) {
					report.setCreationDate(
							SpotifireUtils.parseDateFromCsvFile(columns[5] + columns[6], SpotifireConstants.NASA_FIRMS_DATE_PATTERN));
					report.setType(ReportType.FIRE);
					report.setScore(
							scoreSatelliteDataRow(Double.valueOf(columns[2]), Double.valueOf(columns[10]), Double.valueOf(columns[11])));
					report.setLocation(new Location(Double.valueOf(columns[0]), Double.valueOf(columns[1])));
					report.setSource(SourceType.NASA);
				}
				reportList.add(report);
			});
		}

		reportList.stream().forEach(this.reportService::processReport);

	}

	private static int scoreSatelliteDataRow(double brightness1, double brightness2, double power) {

		double maxBrightness1 = 503.2;
		double minBrightness1 = 300.0;
		double maxBrightness2 = 400.1;
		double minBrightness2 = 264.9;
		double maxPower = 400.1;
		double minPower = 2;
		double coeficienBrightness1 = 0.3045;
		double coeficienBrightness2 = 0.1297;
		double coeficientPower = 0.1747;

		double calculationBrightness1 = calculateCuadraticMinimum(brightness1, maxBrightness1, minBrightness1) * coeficienBrightness1;
		double calculationBrightness2 = calculateCuadraticMinimum(brightness2, maxBrightness2, minBrightness2) * coeficienBrightness2;
		double calculationPower = calculateCuadraticMinimum(power, maxPower, minPower) * coeficientPower;

		return (int) (calculationBrightness1 + calculationBrightness2 + calculationPower) * 100;
	}

	private static double calculateCuadraticMinimum(double input, double maximum, double minimum) {
		return (input - minimum) / (maximum - minimum);
	}

}
