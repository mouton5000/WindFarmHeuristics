package graphTheory.utils;

import java.util.Set;

/**
 * Define an object where different parameters can be added to.
 * 
 * @author Watel Dimitri
 * 
 */
public interface Parametable {

	void defineParam(String s, Object obj);

	boolean containsParam(String s);

	Object getParam(String s);

	Integer getParamInteger(String s);

	Double getParamDouble(String s);

	Long getParamLong(String s);

	String getParamString(String s);

	Boolean getParamBoolean(String s);

	void clearParams();

	void copyParams(Parametable a);

	Set<String> getParamsNames();
}
