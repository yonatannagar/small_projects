package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.LinkedList;
import java.util.List;

public class CloseThisCourseAction<Boolean> extends Action {
    /**
     * Constructor
     */
    public CloseThisCourseAction() {
        setActionName("Close Current Course");
    }

    /**
     * runs the action
     */
    @Override
    protected void start() {
        List<String> l = ((CoursePrivateState)actorState).getRegStudents();
        ((CoursePrivateState) actorState).clearCourse();
        if(!l.isEmpty()) {
            LinkedList<Action> actions = new LinkedList<>();
            for (String student : l) {
                Action unreg = new RemoveCourseFromStudentAction<>(actorId);
                actions.add(unreg);
                sendMessage(unreg, student, pool.getPrivateState(student));
            }
            then(actions, () -> complete(true));
        }else{
            complete(true);
        }


    }
}
