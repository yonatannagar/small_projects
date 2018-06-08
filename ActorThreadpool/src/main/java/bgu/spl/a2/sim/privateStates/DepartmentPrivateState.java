package bgu.spl.a2.sim.privateStates;

import bgu.spl.a2.PrivateState;

import java.util.LinkedList;
import java.util.List;

/**
 * this class describe department's private state
 */
public class DepartmentPrivateState extends PrivateState{
	private List<String> courseList;
	private List<String> studentList;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public DepartmentPrivateState() {
        courseList=new LinkedList<>();
        studentList=new LinkedList<>();
        super.init();
	}

    /**
     *
     * @return list of courses
     */
	public List<String> getCourseList() {
		return courseList;
	}

    /**
     *
     * @return list of students
     */
	public List<String> getStudentList() {
		return studentList;
	}

    /**
     * Tries to remove a course from the courses list
     * @param Name
     * @return true if successfully removed
     */
	public boolean removeCourse(String Name){
	    return courseList.remove(Name);
    }

    /**
     * Adds a course to the courses list
     * @param name
     */
    public void addCourse(String name) { courseList.add(name); }

    /**
     * Adds a student to the students list
     * @param name
     */
    public void addStudent(String name){ studentList.add(name);}

}
