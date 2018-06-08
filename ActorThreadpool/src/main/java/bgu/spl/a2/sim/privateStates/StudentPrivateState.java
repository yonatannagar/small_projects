package bgu.spl.a2.sim.privateStates;

import bgu.spl.a2.PrivateState;

import java.util.HashMap;

/**
 * this class describe student private state
 */
public class StudentPrivateState extends PrivateState{

	private HashMap<String, Integer> grades;
	private long signature;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public StudentPrivateState() {
		grades=new HashMap<>();
        super.init();
	}

    /**
     *
     * @return grades map
     */
	public HashMap<String, Integer> getGrades() {
		return grades;
	}

	public long getSignature() {
		return signature;
	}

    /**
     * Sets the signature
     * @param sig
     */
	public void setSignature(long sig){
	    signature=sig;
    }

    /**
     * Tries to remove a course
     * @param cName
     * @return true if successfully removed
     */
	public boolean removeCourse(String cName){
	    if(grades.containsKey(cName)){
	        grades.remove(cName);
	        return true;
        }
        return false;
    }

    /**
     * Tries to add a course
     * @param cName
     * @param grade
     */
    public void addCourse(String cName, Integer grade){
	    grades.put(cName, grade);
    }

}

