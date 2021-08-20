package com.mobiquity.service;

import com.mobiquity.exception.APIException;

public interface PackerService {
	String processPack(String content) throws APIException;
}
