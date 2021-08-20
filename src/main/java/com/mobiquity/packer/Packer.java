package com.mobiquity.packer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.mobiquity.exception.APIException;
import com.mobiquity.validator.PackerValidator;


/**
 * With a given input of package which has such parameters
 * as index number, weight and cost. The package has a weight limit.
 * The goal is to determine which things to put into the package so
 * that the total weight is less than or equal to the package limit
 * and the total cost is as large as possible. You would prefer to send
 * a package which weighs less in case there is more than one package with the
 * same price.
 *
 * Input:
 * 75 : (1,85.31,€29) (2,14.55,€74) (3,3.98,€16) (4,26.24,€55) (5,63.69,€52)
 * (6,76.25,€75) (7,60.02,€74) (8,93.18,€35) (9,89.95,€78)
 * 56 : (1,90.72,€13) (2,33.80,€40) (3,43.15,€10) (4,37.97,€16) (5,46.81,€36)
 * (6,48.77,€79) (7,81.80,€45) (8,19.36,€79) (9,6.76,€64)
 *
 * Output:
 * 2,7
 * 6,9
 *
 * @see #pack(String) details of the implemntation
 * 
 *
 */
public class Packer {
	
    private static Logger logger = Logger.getLogger(Packer.class);

	/**
	 * @param filePath
	 * @return The output
	 * @throws APIException if there is any exception raised
	 *
	 * Reads the input string form the file
	 * validates the input content
	 * Gets the result from the algorithm implemented
	 * Outputs the result
	 */
	public static String pack(String filePath) throws APIException {
		logger.info("filling package initiated");
		PackerHandler packerHandler = new PackerHandler();
		PackerValidator packerValidator = new PackerValidator();
		StringBuilder result = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
		   stream.forEach(line -> {
			   try {
				   if(packerValidator.isValidInput(line)) {
				   	   String indices = packerHandler.processPack(line);
					   result.append(indices).append("\n");
				   }
			   } catch (APIException e) {
				   logger.error("Invalid Data : " + e.getMessage());
			   }
		   });
		} catch(IOException io){
			logger.error("Unable to locate the input file!!", io);
		} catch(Exception e){
			logger.error("Some error occurred while processing the input file: {}", e);
		}
		logger.info("filling package accomplished");
		return result.toString();
	}
}
