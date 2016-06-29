package graphTheory.steinLib;

/**
 * This exception occurs when a file .stp contains an error.
 * 
 * @author Watel Dimitri
 * 
 */
public class STPTranslationWindFarmException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private STPTranslationWindFarmExceptionEnum errorValue;
	private String filePath;
	private int lineNumber;
	private String lineValue;

	public STPTranslationWindFarmException(STPTranslationWindFarmExceptionEnum errorValue,
										   String filePath, int lineNumber, String lineValue) {
		super();
		this.errorValue = errorValue;
		this.filePath = filePath;
		this.lineNumber = lineNumber;
		this.lineValue = lineValue;

	}

	@Override
	public String getMessage() {

		return "While parsing " + filePath + "\n" + "At line " + lineNumber
				+ " : " + lineValue + "\n" + "Error : " + errorValue.toString();
	}

}
