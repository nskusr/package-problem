package com.mobiquity.packer;

import com.mobiquity.exception.APIException;
import com.mobiquity.model.PackageMetaData;
import com.mobiquity.validator.PackerValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PackerHandlerTest {

    private static PackerHandler packageHandler;

    @BeforeAll
    public static void setup() {
        packageHandler = new PackerHandler();
    }

    @Test
    public void buildPackageTest() throws APIException {
        String content = "81 : (1,53.38,€45)";
        PackerHandler packageHandler = new PackerHandler();
        Map<Double, List<PackageMetaData>> actual = packageHandler.buildPackage(content);

        Map<Double, List<PackageMetaData>> expected = constructPackage();

        Double expectedMaxWeight = expected.keySet().iterator().next();
        Double actualMaxWeight = actual.keySet().iterator().next();

        List<PackageMetaData> expectedPkgList = expected.values().iterator().next();
        List<PackageMetaData> actualPkgList = actual.values().iterator().next();

        assertEquals(expectedMaxWeight, actualMaxWeight);
        assertEquals(expectedPkgList.get(0).getIndex(), actualPkgList.get(0).getIndex());
        assertEquals(expectedPkgList.get(0).getCost(), actualPkgList.get(0).getCost());
        assertEquals(expectedPkgList.get(0).getWeight(), actualPkgList.get(0).getWeight());
    }

    @Test
    public void evaluateMaxCostPackageTest() throws APIException {
        PackerValidator packageValidator = new PackerValidator();
        Map<Double, List<PackageMetaData>> pkgList = constructPackage();
        Double maxWeight = pkgList.keySet().iterator().next();
        List<PackageMetaData> pkgDataList = pkgList.values().iterator().next();

        String actual = packageHandler.evaluateMaxCostPackage(maxWeight, pkgDataList);
        assertEquals("1", actual);
    }

    private Map<Double, List<PackageMetaData>> constructPackage() {
        Map<Double, List<PackageMetaData>> expected = new HashMap<>();
        List<PackageMetaData> packageList = new ArrayList<>();
        PackageMetaData pkg1 = new PackageMetaData();
        pkg1.setIndex(1);
        pkg1.setWeight(53.38d);
        pkg1.setCost(45d);
        packageList.add(pkg1);
        expected.put(81d, packageList);
        return expected;
    }
}
