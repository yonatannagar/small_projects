package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.List;
import java.util.Map;

public class CheckAndRegister<Boolean> extends Action {
    private List<String> prequisites;
    private String courseName;
    private Integer grade;

    /**
     * Constructor
     * @param prereqs
     * @param cName
     * @param grade
     */
    public CheckAndRegister(List<String> prereqs, String cName, Integer grade) {
        super();
        setActionName("Check and Register");
        prequisites=prereqs;
        courseName=cName;
        this.grade=grade;
    }

    /**
     * runs the action
     * if the student is able to:
     * signs him up for a course
     */
    @Override
    protected void start() {
        Map<String, Integer> gradesSheet = ((StudentPrivateState)actorState).getGrades();
        boolean[] passedCourse = new boolean[prequisites.size()];
        int i=0;
        for(String course:prequisites){
            if(gradesSheet.containsKey(course) && gradesSheet.get(course)>=56){
                passedCourse[i]=true;
            }
            i++;
        }
        for(boolean valid: passedCourse)
            if(!valid){
                complete(false);
                return;
            }
        ((StudentPrivateState)actorState).addCourse(courseName, grade);
        complete(true);
    }
}
