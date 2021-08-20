package com.mobiquity.validator;

import com.mobiquity.exception.APIException;
import com.mobiquity.model.PackageMetaData;
import com.mobiquity.packer.PackerHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PackageValidatorTest {

    private static PackerValidator packerValidator;

    @BeforeAll
    public static void setup() {
        packerValidator = new PackerValidator();
    }

    @Test
    public void hasValidRangeTest() throws APIException {
        PackerValidator packageValidator = new PackerValidator();
        boolean hasValidRange = packageValidator.hasValidRange(constructPackage());
        assertEquals(hasValidRange, true);
    }

    @Test
    public void checkInValidRangeTest() throws APIException {
        APIException exception = assertThrows(APIException.class, () -> {
            PackerValidator packageValidator = new PackerValidator();
            Map<Double, List<PackageMetaData>> pkgList = constructPackage();
            List<PackageMetaData> pkgDataList = pkgList.values().iterator().next();
            pkgDataList.get(0).setCost(101d);
            packageValidator.hasValidRange(pkgList);
        });
        assertEquals("Max Cost that a package can take is 100", exception.getMessage());
    }

    @Test
    public void validInputTest() throws APIException {
        String input = "81 : (1,53.38,€45) (2,88.62,€98) (3,78.48,€3)";
        boolean valid = packerValidator.isValidInput(input);
        assertEquals(valid, true);
    }

    @Test
    public void checkInValidInputTest() throws APIException {
        APIException exception = assertThrows(APIException.class, () -> {
            String input = "81 : (1,53.38,€45) |||(2,88.62,€98) (3,78.48,€3)";
            packerValidator.isValidInput(input);
        });
        assertEquals("Can not read the input, please align with the format", exception.getMessage());
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
