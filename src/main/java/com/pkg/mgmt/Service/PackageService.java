package com.pkg.mgmt.Service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pkg.mgmt.Repo.PackageRepo;
import com.pkg.mgmt.Utils.PackageUtils;
import com.pkg.mgmt.model.BusinessException;
import com.pkg.mgmt.model.PackageBean;
import com.pkg.mgmt.model.ProductBean;

@Component
public class PackageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PackageService.class);

	@Autowired
	private PackageRepo packageRepo;

	@Autowired
	private PackageUtils packageUtils;

	public void createPackage(PackageBean pkg) throws BusinessException {

		for (int i = 0; i < pkg.getProducts().size(); i++) {
			ProductBean product = pkg.getProducts().get(i);
			ProductBean productDetails = packageUtils.getProductDetails(product.getId());

			if (productDetails == null) {

				throw new BusinessException("Product Details Not found for Product Id: " + product.getId());

			}
			product.setName(productDetails.getName());
			product.setUsdPrice(productDetails.getUsdPrice());
		}

		packageRepo.persistPackage(pkg);
	}

	public PackageBean fetchPackage(String packageId) {

		return packageRepo.fetchPackage(packageId);

	}

	public void updatePackage(PackageBean pkg) throws BusinessException {

		for (int i = 0; i < pkg.getProducts().size(); i++) {
			ProductBean product = pkg.getProducts().get(i);
			ProductBean productDetails = packageUtils.getProductDetails(product.getId());

			if (productDetails == null) {

				throw new BusinessException("Product Details Not found for Product Id: " + product.getId());

			}
			product.setName(productDetails.getName());
			product.setUsdPrice(productDetails.getUsdPrice());
		}

		deletePackage(pkg.getId());
		createPackage(pkg);

	}

	public List<PackageBean> getAllPackageDetails(String currency) {

		boolean convertCurrency = false;
		double exchangeRate = 1;

		if (!"USD".equalsIgnoreCase(currency)) {
			convertCurrency = true;
			exchangeRate = packageUtils.getExchangeRates(currency);
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
		packageBean.setPrice(packageUtils.getTotalPackagePrice(products, currency));
		packageBean.getProducts().addAll(products);
		return packageBean;
	}

}
