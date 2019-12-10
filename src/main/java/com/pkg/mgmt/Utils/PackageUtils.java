package com.pkg.mgmt.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.pkg.mgmt.model.ExchangeApiModel;
import com.pkg.mgmt.model.ProductBean;

@Component
public class PackageUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(PackageUtils.class);

	@Value("${CURRENCY_EXCHANGE_API_URL}")
	private String currencyExchangeUrl;

	@Value("${PRODUCTS_REST_API}")
	private String productsRestApi;

	@Value("${USER_NAME}")
	private String userName;

	@Value("${SECURE_KEY}")
	private String secureKey;

	@Autowired
	private RestTemplate restTemplate;

	public BigDecimal getTotalPackagePrice(List<ProductBean> products, String currency) {

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

	public double getExchangeRates(String currency) {
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

	public ProductBean[] getAllProducts() {

		String authStr = userName + ":" + secureKey;
		String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);

		HttpEntity request = new HttpEntity(headers);

		ResponseEntity<ProductBean[]> response = restTemplate.exchange(productsRestApi, HttpMethod.GET, request,
				ProductBean[].class);

		return response.getBody();

	}

	@Cacheable("productBean")
	public ProductBean getProductDetails(String productId) {

		String url = productsRestApi + "/" + productId;
		String authStr = userName + ":" + secureKey;
		String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

		// create headers
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);

		HttpEntity request = new HttpEntity(headers);

		try {
			ResponseEntity<ProductBean> response = restTemplate.exchange(url, HttpMethod.GET, request,
					ProductBean.class);

			return response.getBody();
		} catch (RestClientException e) {
			LOGGER.error("Exception occured while fetching productDetails {}", e);
			return null;
		}
	}

}
