package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.LinkedList;
import java.util.List;

public class UnregisterAction<Boolean> extends Action {
    private String student;

    /**
     * Constructor
     * @param student
     */
    public UnregisterAction(String student) {
        super();
        this.student=student;
        setActionName("Unregister");
    }

    /**
     * runs the action
     * tries to unregister a student from this course
     */
    @Override
    protected void start() {
        if(!((CoursePrivateState)actorState).getRegStudents().contains(student)){
            sendMessage(this, actorId, actorState);
            return;
        }

        List<Action> actions = new LinkedList<>();
        Action act = new RemoveCourseFromStudentAction(actorId);

        actions.add(act);
        then(actions, ()-> complete(((CoursePrivateState)actorState).removeStudent(student)));
        sendMessage(act, student, pool.getPrivateState(student));
    }
}
