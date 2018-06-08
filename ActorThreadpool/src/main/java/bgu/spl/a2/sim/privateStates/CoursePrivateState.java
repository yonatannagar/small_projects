package bgu.spl.a2.sim.privateStates;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.a2.PrivateState;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState{

	private Integer availableSpots;
	private Integer registered;
	private List<String> regStudents;
	private List<String> prequisites;

	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public CoursePrivateState() {
		availableSpots=0;
		registered=0;
		regStudents= new LinkedList<>();
		prequisites= new LinkedList<>();
        super.init();
	}

    /**
     * Initializing courses values
     * @param space
     * @param preq
     */
    public void setBaseValues(int space, List preq){
	    availableSpots=space;
	    this.prequisites=preq;
    }
    public void incSpaces(int incByAmount){
	    this.availableSpots+=incByAmount;
    }

    /**
     * Removes a student from the roster
     * @param student
     * @return true if successfully removed
     */
    public boolean removeStudent(String student){
	    if(regStudents.remove(student)){
	        registered--;
	        availableSpots++;
	        return true;
        }
        return false;
    }

    /**
     * Fixes a course state to closed
     */
    public void clearCourse(){
	    availableSpots=-1;
	    registered=0;
	    regStudents=new LinkedList<>();
    }

    public void decAvailableNum(){
	    availableSpots--;
    }

    public void incAvailableNum(){
        availableSpots++;
    }

    /**
     * Adds a student to the course
     * @param student
     */
    public void addStudent(String student){
	    regStudents.add(student);
        registered++;
    }

    /**
     *
     * @return true if there is room to register
     */
    public boolean isThereRoom(){
	    return availableSpots>0;
    }

    /**
     *
     * @return number of available spots
     */
	public Integer getAvailableSpots() {
		return availableSpots;
	}

    /**
     *
     * @return number of registered students
     */
	public Integer getRegistered() {
		return registered;
	}

    /**
     *
     * @return list of registered students
     */
	public List<String> getRegStudents() {
		return regStudents;
	}

    /**
     *
     * @return list of prerequisite courses
     */
	public List<String> getPrequisites() {
		return prequisites;
	}

}
