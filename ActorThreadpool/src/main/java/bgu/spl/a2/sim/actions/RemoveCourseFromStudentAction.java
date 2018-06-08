package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class RemoveCourseFromStudentAction<Boolean> extends Action {
    private String toRemove;

    /**
     * Constructor
     * @param courseName
     */
    public RemoveCourseFromStudentAction(String courseName) {
        super();
        setActionName("Remove Course From Student");
        toRemove=courseName;
    }

    /**
     * runs the action
     * removes the given course from this student's courses list
     */
    @Override
    protected void start() {
        complete(((StudentPrivateState)actorState).removeCourse(toRemove));
    }
}
