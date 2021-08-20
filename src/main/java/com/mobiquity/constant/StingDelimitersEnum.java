package com.mobiquity.constant;

public enum StingDelimitersEnum {
	
	COLON(":"),
    COMMA(","), 
    SPACE(" "),
    EMPTY(""),
	DASH("-"),
    BRACKETOPEN("("),
    EURO("€"),
    BRACKETCLOSE(")");
	
	private String delimeter;
	
	private StingDelimitersEnum(String delimeter) {
		this.delimeter = delimeter;
	}
	
	public String getDelimeter() {
		return delimeter;
	}

}
