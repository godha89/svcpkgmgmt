package com.pkg.mgmt.Controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.pkg.mgmt.Service.PackageService;
import com.pkg.mgmt.model.BusinessException;
import com.pkg.mgmt.model.PackageBean;
import com.pkg.mgmt.model.PackageMgmtErrorResponse;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/package")
@Transactional
public class PackageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PackageController.class);

	@Autowired
	RestTemplate restTemplate;
	@Autowired
	PackageService packageService;

	@ApiOperation(value = "Creates a new package with selected products", response = String.class)
	@PostMapping(path = "/create", consumes = { MediaType.APPLICATION_JSON_VALUE, }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<PackageBean> createPackage(@Valid @RequestBody PackageBean packageBean,
			UriComponentsBuilder ucBuilder) throws BusinessException {

		LOGGER.info("Start Create Package");
		// Generate Random Package Id.
		String packageId = UUID.randomUUID().toString().replace("-", "");
		packageBean.setId(packageId);

		packageService.createPackage(packageBean);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/api/package/fetchPackage/{id}").buildAndExpand(packageId).toUri());
		LOGGER.info("Package Created Successfully");

		return new ResponseEntity<PackageBean>(headers, HttpStatus.CREATED);

	}

	@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8761" })
	@ApiOperation(value = "Fetches details of a package based on packageId", response = PackageBean.class)
	@GetMapping(path = "/fetchPackage/{id}")
	public ResponseEntity<?> getPackage(@PathVariable String id,
			@RequestParam(value = "curr", required = false) String currency) {
		LOGGER.info("Fetch Package Details for id={}", id);
		if (StringUtils.isBlank(currency)) {
			currency = "USD";
		}
		PackageBean packageBean = packageService.fetchPackage(id);

		if (packageBean == null) {
			LOGGER.error("Package with id {} not found.", id);
			return new ResponseEntity(new PackageMgmtErrorResponse("false", "Package with id " + id + " not found"),
					HttpStatus.NOT_FOUND);
		}

		packageBean = packageService.fetchPackageProducts(id, currency.toUpperCase(), packageBean);

		return new ResponseEntity<PackageBean>(packageBean, HttpStatus.OK);

	}

	@ApiOperation(value = "Delete package based on packageId", response = String.class)
	@DeleteMapping(path = "/deletePackage/{id}")
	public ResponseEntity<?> deletePackage(@PathVariable String id) {

		PackageBean packageBean = packageService.fetchPackage(id);

		if (packageBean == null) {
			LOGGER.error("Unable to delete. Package with id {} not found.", id);
			return new ResponseEntity(
					new PackageMgmtErrorResponse("false", "Unable to delete. Package with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		packageService.deletePackage(id);

		return new ResponseEntity<PackageBean>(HttpStatus.NO_CONTENT);
	}

	@ApiOperation(value = "Update a package based on packageId", response = PackageBean.class)
	@PutMapping(path = "/updatePackage/{id}")
	public ResponseEntity<?> updatePackage(@PathVariable String id, @RequestBody PackageBean packageBean)
			throws BusinessException {
		LOGGER.debug("Version 1 input");

		PackageBean existingPackage = packageService.fetchPackage(id);

		if (existingPackage == null) {
			LOGGER.error("Unable to update. Package with id {} not found.", id);
			return new ResponseEntity(
					new PackageMgmtErrorResponse("false", "Unable to upate. Package with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}

		existingPackage.setName(packageBean.getName());
		existingPackage.setDescription(packageBean.getDescription());
		existingPackage.setProducts(packageBean.getProducts());

		packageService.updatePackage(existingPackage);
		return new ResponseEntity<PackageBean>(existingPackage, HttpStatus.OK);
	}

	@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8761" })
	@ApiOperation(value = "Fetches list of packages", response = List.class)
	@GetMapping(path = "/allPackages")
	public ResponseEntity<List<PackageBean>> getAllPackages(
			@RequestParam(value = "curr", required = false) String currency) {

		if (StringUtils.isBlank(currency)) {
			currency = "USD";
		}

		List<PackageBean> packages = packageService.getAllPackageDetails(currency.toUpperCase());
		if (packages.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<PackageBean>>(packages, HttpStatus.OK);
	}

}
