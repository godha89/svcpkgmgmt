package com.pkg.mgmt.Repo;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pkg.mgmt.model.PackageBean;
import com.pkg.mgmt.model.ProductBean;

@Repository
public class PackageRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(PackageRepo.class);

	private final String INSERT_PACKAGE_SQL = "INSERT INTO PACKAGE(PACKAGE_ID, NAME, DESCRIPTION)  values(:packageId, :name, :description)";
	private final String INSERT_PRODUCT_SQL = "INSERT INTO PRODUCT(PRODUCT_ID, PACKAGE_ID, NAME, PRICE) values (:productId,:packageId,:name,:price)";
	private final String FETCH_PACKAGE_SQL = "SELECT * FROM PACKAGE WHERE PACKAGE_ID=:packageId";
	private final String FETCH_PRODUCT_SQL = "SELECT * FROM PRODUCT WHERE PACKAGE_ID=:packageId";
	private final String ALL_PACKAGE_SQL = "SELECT PKG.PACKAGE_ID, PKG.NAME, DESCRIPTION, SUM(PRICE) AS USD_PRICE FROM PACKAGE PKG LEFT JOIN PRODUCT PRO ON PKG.PACKAGE_ID=PRO.PACKAGE_ID GROUP BY PRO.PACKAGE_ID";

	private final String DELETE_PACKAGE_SQL = "DELETE FROM PACKAGE WHERE PACKAGE_ID=:packageId";
	private final String DELETE_PRODUCT_SQL = "DELETE FROM PRODUCT WHERE PACKAGE_ID=:packageId";

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public void persistPackage(PackageBean pkg) {

		MapSqlParameterSource paramSource = new MapSqlParameterSource();

		paramSource.addValue("packageId", pkg.getId());
		paramSource.addValue("name", pkg.getName());
		paramSource.addValue("description", pkg.getDescription());

		namedParameterJdbcTemplate.update(INSERT_PACKAGE_SQL, paramSource);

		LOGGER.info("Package Created with ID={}", pkg.getId());

		persistProducts(pkg.getId(), pkg.getProducts());

		// jdbcTemplate.update(sql, pss)

	}

	private void persistProducts(String packageId, List<ProductBean> products) {

		List<Map<String, Object>> batchValues = new ArrayList<>(products.size());

		for (ProductBean product : products) {
			batchValues.add(new MapSqlParameterSource("productId", product.getId()).addValue("packageId", packageId)
					.addValue("name", product.getName()).addValue("price", product.getUsdPrice()).getValues());
		}

		namedParameterJdbcTemplate.batchUpdate(INSERT_PRODUCT_SQL, batchValues.toArray(new Map[products.size()]));

	}

	public PackageBean fetchPackage(String packageId) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("packageId", packageId);

		LOGGER.info("Fetch Package with ID={}", packageId);

		List<PackageBean> packageList = namedParameterJdbcTemplate.query(FETCH_PACKAGE_SQL, paramSource,
				new RowMapper<PackageBean>() {

					public PackageBean mapRow(ResultSet rs, int row) throws SQLException {
						PackageBean packageBean = new PackageBean();
						packageBean.setId(rs.getString("PACKAGE_ID"));
						packageBean.setName(rs.getString("NAME"));
						packageBean.setDescription(rs.getString("DESCRIPTION"));

						return packageBean;
					}

				});

		if (packageList == null || packageList.isEmpty()) {
			return null;
		}

		return packageList.get(0);

	}

	public List<ProductBean> fetchProducts(String packageId) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("packageId", packageId);

		return namedParameterJdbcTemplate.query(FETCH_PRODUCT_SQL, paramSource, new RowMapper<ProductBean>() {

			public ProductBean mapRow(ResultSet rs, int row) throws SQLException {
				ProductBean productBean = new ProductBean();
				productBean.setId(rs.getString("PRODUCT_ID"));
				productBean.setName(rs.getString("NAME"));
				productBean.setUsdPrice(new BigDecimal(rs.getString("PRICE")));

				return productBean;
			}

		});
	}

	public void deletePackage(String packageId) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("packageId", packageId);

		namedParameterJdbcTemplate.update(DELETE_PRODUCT_SQL, paramSource);
		namedParameterJdbcTemplate.update(DELETE_PACKAGE_SQL, paramSource);
		LOGGER.info("Package Deleted with ID={}", packageId);

	}

	public List<PackageBean> fetchAllPackage() {

		List<PackageBean> packageList = namedParameterJdbcTemplate.query(ALL_PACKAGE_SQL, new RowMapper<PackageBean>() {

			public PackageBean mapRow(ResultSet rs, int row) throws SQLException {
				PackageBean packageBean = new PackageBean();
				packageBean.setId(rs.getString("PACKAGE_ID"));
				packageBean.setName(rs.getString("NAME"));
				packageBean.setDescription(rs.getString("DESCRIPTION"));
				packageBean.setPrice(new BigDecimal(rs.getDouble("USD_PRICE")));
				return packageBean;
			}

		});

		return packageList;

	}

}
