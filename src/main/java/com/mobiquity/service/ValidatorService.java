package com.mobiquity.service;

import com.mobiquity.exception.APIException;
import com.mobiquity.model.PackageMetaData;

import java.util.List;
import java.util.Map;

public interface ValidatorService {
    boolean isValidInput(String content) throws APIException;
    boolean hasValidRange(Map<Double, List<PackageMetaData>> allPackage) throws APIException;
}
