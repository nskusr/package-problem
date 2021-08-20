package com.mobiquity.packer;

import com.mobiquity.exception.APIException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class PackerTest {

    @Test
    public void processPackTest() throws APIException {
        String actual = Packer.pack("./src/test/resources/input.txt");
        String expected =  "4\n" + "-\n" + "2,7\n" +  "6,9\n";
        assertEquals(expected, actual);
    }
}
