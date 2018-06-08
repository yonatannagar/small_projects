package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.LinkedList;
import java.util.List;


public class CloseCourseAction<Boolean> extends Action {
    private String courseName;

    /**
     * runs the action
     */
    @Override
    protected void start() {
        if(!(pool.getActors().containsKey(courseName))) {
            sendMessage(this, actorId, actorState);
            return;
        }
        ((DepartmentPrivateState)actorState).removeCourse(courseName);
        List<Action> actions = new LinkedList<>();
        Action act = new CloseThisCourseAction();
        actions.add(act);
        then(actions, ()-> complete(true));
        sendMessage(act, courseName, pool.getPrivateState(courseName));
    }

    /**
     * Constructor
     * @param courseName
     */
    public CloseCourseAction(String courseName) {
        super();
        this.courseName=courseName;
        setActionName("Close Course");
    }
}
