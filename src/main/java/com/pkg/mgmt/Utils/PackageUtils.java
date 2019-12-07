package com.pkg.mgmt.Utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.pkg.mgmt.model.PackageBean;

@Component
public class PackageUtils {

	public void validatePackage(PackageBean packageBean) {

		if (StringUtils.isBlank(packageBean.getName())) {

		}

		if (StringUtils.isBlank(packageBean.getDescription())) {

		}
		if (StringUtils.isBlank(packageBean.getDescription())) {

		}
	}

}
