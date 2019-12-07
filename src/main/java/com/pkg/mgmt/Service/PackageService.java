package com.pkg.mgmt.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.pkg.mgmt.Repo.PackageRepo;
import com.pkg.mgmt.model.ExchangeApiModel;
import com.pkg.mgmt.model.PackageBean;
import com.pkg.mgmt.model.ProductBean;

@Component
public class PackageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PackageService.class);

	@Autowired
	private PackageRepo packageRepo;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${CURRENCY_EXCHANGE_API_URL}")
	private String currencyExchangeUrl;

	public void createPackage(PackageBean pkg) {
		packageRepo.persistPackage(pkg);
	}

	public PackageBean fetchPackage(String packageId) {

		return packageRepo.fetchPackage(packageId);

	}

	public void updatePackage(PackageBean pkg) {

		deletePackage(pkg.getId());
		createPackage(pkg);

	}

	public List<PackageBean> getAllPackageDetails(String currency) {

		boolean convertCurrency = false;
		double exchangeRate = 1;

		if (!"USD".equalsIgnoreCase(currency)) {
			convertCurrency = true;
			exchangeRate = getExchangeRates(currency);
		}

		List<PackageBean> packages = packageRepo.fetchAllPackage();

		if (convertCurrency) {
			for (PackageBean packageBean : packages) {
				BigDecimal convertedCurrency = new BigDecimal(exchangeRate * packageBean.getPrice().doubleValue());
				packageBean.setPrice(convertedCurrency);
			}
		}

		return packages;
	}

	public void deletePackage(String packageId) {

		packageRepo.deletePackage(packageId);

	}

	public PackageBean fetchPackageProducts(String packageId, String currency, PackageBean packageBean) {

		List<ProductBean> products = packageRepo.fetchProducts(packageId);
		packageBean.setPrice(getTotalPackagePrice(products, currency));
		packageBean.getProducts().addAll(products);
		return packageBean;
	}

	private BigDecimal getTotalPackagePrice(List<ProductBean> products, String currency) {

		boolean convertCurrency = false;
		double exchangeRate = 1;

		if (!"USD".equalsIgnoreCase(currency)) {
			convertCurrency = true;
			exchangeRate = getExchangeRates(currency);
		}

		double packagePrice = 0;
		for (ProductBean product : products) {

			if (convertCurrency) {
				BigDecimal convertedCurrency = new BigDecimal(exchangeRate * product.getUsdPrice().doubleValue());
				product.setUsdPrice(convertedCurrency.setScale(2, RoundingMode.HALF_UP));
			}

			packagePrice += product.getUsdPrice().doubleValue();
		}
		BigDecimal packageCost = new BigDecimal(packagePrice);
		return packageCost.setScale(2, RoundingMode.HALF_UP);
	}

	private double getExchangeRates(String currency) {
		String url = String.format(currencyExchangeUrl, currency);

		LOGGER.info("Exchange Rate API Url = {}", url);
		try {
			ExchangeApiModel exchangeRate = restTemplate.getForObject(url, ExchangeApiModel.class);

			if (exchangeRate == null || StringUtils.isNotBlank(exchangeRate.getError())) {
				return 0;
			} else {
				LOGGER.info("Exhcnge Rate = {} for Currency = {}", exchangeRate.getRates().get(currency), currency);
				return Double.parseDouble(exchangeRate.getRates().get(currency));
			}
		} catch (Exception e) {
			LOGGER.error("Error occured while fetching exchange rates. Returning Price in USD.");
			return 1;
		}
	}

}
