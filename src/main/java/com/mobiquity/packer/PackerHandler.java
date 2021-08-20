package com.mobiquity.packer;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mobiquity.constant.StingDelimitersEnum;
import com.mobiquity.exception.APIException;
import com.mobiquity.validator.PackerValidator;
import org.apache.log4j.Logger;

import com.mobiquity.model.PackageMetaData;
import com.mobiquity.pattern.PackageBuilder;
import com.mobiquity.service.PackerService;

/**
 * Process the package list, builds a Map which stores the key as the maximum weight
 * a bag can hold and the list of package with itemIndex, itemWeight and itemCost
 * {@link APIException} will be thrown.
 * <br/>
 * The format of each line should be:
 * <p>
 * <code>{maxPackageWeight}: ({itemIndex},{itemWeight},{itemCost}), ...</code>
 * </p>
 */
public class PackerHandler implements PackerService {
	
    private static Logger logger = Logger.getLogger(PackerHandler.class);

	/**
	 * @param content represents the input line
	 * @return
	 * @throws APIException if te input does not fall in the range as below
	 *
	 * Validates the input and processes the package
	 *
	 * For input validation
	 * @see PackerValidator#hasValidRange
	 *
	 * For processing of the input
	 * @see #processPack(Map<Double, List<PackageMetaData>>)
	 *
	 */
	@Override
	public String processPack(String content) throws APIException {
		PackerValidator packageValidator = new PackerValidator();
		Map<Double, List<PackageMetaData>> allPackages = buildPackage(content);
		if(packageValidator.hasValidRange(allPackages)){
			return processPack(allPackages);
		}
		return StingDelimitersEnum.EMPTY.getDelimeter();
	}


	/**
	 * @param content
	 * @return
	 *
	 * Builds a Map which stores the key as the maximum weight
	 * a bag can hold and the list of package with itemIndex, itemWeight and itemCost
	 *
	 * Refer the builder pattern to construct an Map with maxWeight as key
	 * and values as ({itemIndex},{itemWeight},{itemCost})
	 *
	 * @see PackageBuilder#splitByColon()
	 * @see PackageBuilder#splitBySpace()
	 * @see PackageBuilder#formatPackageData()
	 * @see PackageBuilder#splitByCommaAndConvertToList()
	 * @see PackageBuilder#buildMap()
	 *
	 */
	public Map<Double, List<PackageMetaData>> buildPackage(String content) {
		PackageBuilder packageBuilder = new PackageBuilder();
		packageBuilder.reset();
		packageBuilder.setContent(content);
		Map<Double, List<PackageMetaData>> allPackages = packageBuilder
			.splitByColon()
			.splitBySpace()
			.formatPackageData()
			.splitByCommaAndConvertToList()
			.buildMap();
		return allPackages;
	}

	/**
	 * @param packages The bag with max weight along with itemIndex, itemWeight and itemCost
	 * @return the indices of the list items
	 *
	 * Applies the descending order sort on itemCost
	 * and processes the list of items
	 *
	 * Look for the greedy algorithm
	 * @see #evaluateMaxCostPackage(Double, List<PackageMetaData>)
	 */
	public String processPack(Map<Double, List<PackageMetaData>> packages){
		Comparator<PackageMetaData> compareByCost =
				Comparator.comparing(PackageMetaData::getCost).reversed();
		var ref = new Object(){
			String indices = null;
		};
		packages.forEach((maxWeight, packageList) -> {
			List<PackageMetaData> packList = packageList.stream()
					.sorted(compareByCost)
					.collect(Collectors.toList());
			ref.indices = evaluateMaxCostPackage(maxWeight, packList);
		});
		return ref.indices;
	}

	/**
	 * @param maxWeight
	 * @param packages
	 * @return the indices of the list items
	 *
	 * Keep on filling the bag with the weight
	 * until the bag's maximum weight is exhausted
	 * or iterating through all the packages are
	 * finished
	 */
	public String evaluateMaxCostPackage(Double maxWeight, List<PackageMetaData> packages){
		var ref = new Object(){
			Double remaining = maxWeight;
			Double result = 0d;
			StringBuilder finalResult = new StringBuilder();
		};

		packages.stream().takeWhile( t -> ref.remaining >= 0).forEach( pkg -> {
			if(pkg.getWeight() <= ref.remaining){
				ref.remaining -= pkg.getWeight();
				ref.result += pkg.getCost();
				ref.finalResult.append(pkg.getIndex()).append(StingDelimitersEnum.COMMA.getDelimeter());
				logger.info("Pack " + pkg.getIndex() + " - Weight " + pkg.getWeight() + " - Cost " + pkg.getCost());
			}
		});

		return ref.finalResult.toString().isEmpty() ? StingDelimitersEnum.DASH.getDelimeter() :
				ref.finalResult.toString().substring(0, ref.finalResult.toString().length() - 1);
	}
}