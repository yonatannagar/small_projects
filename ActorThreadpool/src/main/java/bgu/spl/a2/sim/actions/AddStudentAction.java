package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.LinkedList;
import java.util.List;

public class AddStudentAction<Boolean> extends Action {
    private StudentPrivateState ps;
    private String name;

    /**
     * Constructor
     * @param name
     */
    public AddStudentAction(String name) {
        super();
        setActionName("Add Student");
        this.ps = new StudentPrivateState();
        this.name = name;
    }

    /**
     * runs the action
     * sends a void action to a student's actor
     * to open it and attach it's student's privatestate
     */
    @Override
    protected void start() {
        Action act = new VoidAction();
        if (pool.getPrivateState(name) == null) {
            sendMessage(act, name, ps);
            List<Action> actions = new LinkedList<>();
            actions.add(act);
        }
        ((DepartmentPrivateState)actorState).addStudent(name);
        complete(true);

    }
}
