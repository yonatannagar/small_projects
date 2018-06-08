package bgu.spl.a2.sim;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class Computer {
    @SerializedName("Type")
	String computerType;
    @SerializedName("Sig Fail")
	long failSig;
    @SerializedName("Sig Success")
	long successSig;
	
	public Computer(String computerType) {
		this.computerType = computerType;
	}
	
	/**
	 * this method checks if the courses' grades fulfill the conditions
	 * @param courses
	 * 							courses that should be pass
	 * @param coursesGrades
	 * 							courses' grade
	 * @return a signature if courses Grades grades meet the conditions
	 */
	public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades){
	    for(int i=0; i<courses.size(); i++){
	        if(!coursesGrades.containsKey(courses.get(i)))
	            return failSig;
	        if(coursesGrades.get(courses.get(i))<56)
	            return failSig;
        }
        return successSig;
	}
}
