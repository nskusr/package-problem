package com.mobiquity.validator;

import com.mobiquity.constant.StingDelimitersEnum;
import com.mobiquity.exception.APIException;
import com.mobiquity.model.PackageMetaData;
import com.mobiquity.service.ValidatorService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Process the input lines and validates the format and the range
 * {@link APIException} will be thrown.
 * <br/>
 * The format of the each line as below:
 * <p>
 * <code>{maxWeight}: ({itemIndex},{itemWeight},{itemCost}), ..</code>
 * </p>
 */
public class PackerValidator implements ValidatorService {

	private final Double MAX_WEIGHT = 100d;
	private final int MAX_ITEMS = 15;
	private final Double MAX_COST = 100d;
    private final Pattern strPattern = Pattern.compile("^\\((\\d+),(\\d+\\.?\\d*?),€?(\\d+)\\)$");
    private final Pattern digitPattern = Pattern.compile("^\\d+$");

    /**
     * @param line
     * @return true if the input format is the correct one
     * @throws APIException if there format is incorrect
     *
     * Validates the input format and checks if the format
     * is the desired one
     */
    @Override
    public boolean isValidInput(String line) throws APIException {
        int index = line.indexOf(StingDelimitersEnum.COLON.getDelimeter());

        if (index < 0) {
           throw new APIException("Can not read weight of from the given input");
        }

        String strBeforeColon = line.substring(0, index).trim();
        boolean validDigitPattern = this.digitPattern.matcher(strBeforeColon).find();

        String strAfterColon = line.substring(index + 1);
        List<String> list = Arrays.asList(strAfterColon
                .split(StingDelimitersEnum.SPACE.getDelimeter()));
        boolean validStrPattern = list.stream().allMatch(str -> {
            return str.isBlank() ? true : this.strPattern.matcher(str).find();
        });

        if(!validStrPattern || !validDigitPattern){
            throw new APIException("Can not read the input, please align with the format");
        }

        return validStrPattern && validDigitPattern;
    }

    /**
     * @param allPackage
     * @return true if the package list falls with in the range
     * @throws APIException if it meets the following boundary condition
     *
     * 1. Max weight that a package can take is ≤ 100
     * 2. There might be up to 15 items you need to choose from
     * 3. Max weight and cost of an item is ≤ 100
     *
     */
    @Override
    public boolean hasValidRange(Map<Double, List<PackageMetaData>> allPackage) throws APIException {
        Double maxWeight = allPackage.keySet().stream().iterator().next();
        List<PackageMetaData> pkgList = allPackage.values().iterator().next();

        if(MAX_WEIGHT < maxWeight){
            throw new APIException("Max weight that a package can take is 100");
        } else if(pkgList.size() > 15){
            throw new APIException("Max items that a package can take is 15");
        } else {
            boolean hasMaxWeightExceeded = pkgList.stream().anyMatch(pkg -> {
                return pkg.getCost() > 100;
            });
            if(hasMaxWeightExceeded){
                throw new APIException("Max Cost that a package can take is 100");
            }
        }

        return true;
    }
}
