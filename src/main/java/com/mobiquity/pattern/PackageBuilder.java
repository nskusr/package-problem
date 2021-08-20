package com.mobiquity.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mobiquity.constant.StingDelimitersEnum;
import com.mobiquity.model.PackageMetaData;

/**
 * Builds the Map with key as maxWeight of an item list
 * and values as the list of packages with the following
 * information.
 *
 * <br/>
 * The format of the package :
 * <p>
 * <code>{key -> maxWeight}: (values -> {itemIndex},{itemWeight},{itemCost}), ..</code>
 * </p>
 */

public class PackageBuilder {
	
	private String content;
	private String[] splitStrByColon;
	private String[] splitStrBySpace;
	private List<String> bagList;
	private List<PackageMetaData> packageList;
	private Map<Double, List<PackageMetaData>> allPackages;

	public PackageBuilder(){
		reset();
	}

	/**
	 * @return PackageBuilder
	 *
	 * split the content by colon as delimiter
	 *
	 * For example :
	 * content - 81 : (1,53.38,€45) (2,88.62,€98)
	 * splitStrByColon - [81, {1,53.38,€45} {2,88.62,€9}]
	 */
	public PackageBuilder splitByColon(){
		if(null != content  || !content.isBlank()) {
			this.splitStrByColon =  content.split(StingDelimitersEnum.COLON.getDelimeter());
		}
        return this;
    }

	/**
	 * @return PackageBuilder
	 *
	 * split the content by space as delimiter
	 *
	 * @see #splitByColon() to derive splitStrBySpace
	 *
	 * For example :
	 * splitStrByColon[1] - (1,53.38,€45) (2,88.62,€98)
	 * splitStrBySpace - [{1,53.38,€45},{2,88.62,€9}]
	 */
	public PackageBuilder splitBySpace(){
		if(null != this.splitStrByColon  || this.splitStrByColon.length != 0) {
			this.splitStrBySpace =  this.splitStrByColon[1].split(StingDelimitersEnum.SPACE.getDelimeter());
		} else {
			this.splitStrBySpace =  content.split(StingDelimitersEnum.SPACE.getDelimeter());
		}
        return this;
    }

	/**
	 * @return PackageBuilder
	 *
	 * Formats the input package list by removing
	 * the brackets and euro symbol
	 *
	 * @see #splitBySpace() to derive splitStrBySpace
	 *
	 * splitStrBySpace - [{1,53.38,€45},{2,88.62,€9}]
	 * packageList - [{1, 53.38,45},{2, 88.62, 9}]
	 *
	 */
	public PackageBuilder formatPackageData() {
		bagList = new ArrayList<>();
		if(null != this.splitStrBySpace  || this.splitStrBySpace.length != 0) {
			for(String str : this.splitStrBySpace) {
				if(!str.isBlank()) {
					str = str.replace(StingDelimitersEnum.BRACKETOPEN.getDelimeter(), StingDelimitersEnum.EMPTY.getDelimeter())
							.replace(StingDelimitersEnum.BRACKETCLOSE.getDelimeter(), StingDelimitersEnum.EMPTY.getDelimeter())
							.replace(StingDelimitersEnum.EURO.getDelimeter(), StingDelimitersEnum.EMPTY.getDelimeter());
					bagList.add(str);
				}
			}
		}
		return this;
	}

	/**
	 * @return PackageBuilder
	 *
	 * split the content by comma as delimiter
	 * and then convert it to a list of packages
	 *
	 * @see #formatPackageData() to derive bagList
	 *
	 * For example :
	 * bagList - [{1,53.38,€45},{2,88.62,€9}]
	 * packageList - [{index:1, weight: 53.38, cost: 45}
	 *                {index:2, weight: 88.62, cost: 9}]
	 */
	public PackageBuilder splitByCommaAndConvertToList(){
		if(null != bagList && !this.bagList.isEmpty()) {
			packageList = new ArrayList<>();
			this.bagList.forEach(bag -> {
				String[] bagItems = bag.split(StingDelimitersEnum.COMMA.getDelimeter());
				PackageMetaData pkg = new PackageMetaData();
				pkg.setIndex(Integer.parseInt(bagItems[0]));
				pkg.setWeight(Double.parseDouble(bagItems[1]));
				pkg.setCost(Double.parseDouble(bagItems[2]));
				packageList.add(pkg);
			});
		}
        return this;
    }


	/**
	 * @return A Map
	 * Builds the Map with key as maxWeight of an item list
	 * and values as the list of packages with the following
	 * information.
	 * {key -> maxWeight}: (values -> {itemIndex},{itemWeight},{itemCost}), ..</code>
	 *
	 * @see #splitByCommaAndConvertToList() to derive bagList
	 *
	 */
	public Map<Double, List<PackageMetaData>> buildMap(){
		allPackages = new HashMap<>();
		allPackages.put(Double.parseDouble(this.splitStrByColon[0]), packageList);
		reset();
		return allPackages;
	}

	/**
	 * Reset all the fields
	 */
	public void reset() {
		this.content = null;
		this.splitStrByColon = null;
		this.splitStrBySpace = null;
		this.bagList = new ArrayList<>();
		this.packageList = new ArrayList<>();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
