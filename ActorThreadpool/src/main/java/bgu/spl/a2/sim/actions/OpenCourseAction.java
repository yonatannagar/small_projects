package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.LinkedList;
import java.util.List;

public class OpenCourseAction extends Action{
    private int space;
    private String name;
    private CoursePrivateState ps;

    /**
     * Constructor
     * @param space
     * @param prereqs
     * @param name
     */
    public OpenCourseAction(Integer space, List prereqs, String name){
        super();
        setActionName("Open Course");
        ps = new CoursePrivateState();
        ps.setBaseValues(space, prereqs);
        this.space=space;
        this.name=name;
    }

    /**
     * runs the action
     * sends a void action to a course's actor
     * to open it and attach it's course privatestate
     */
    protected void start(){
        ((DepartmentPrivateState)actorState).addCourse(name);
        Action act;
        if(pool.getPrivateState(name)==null){
            act = new VoidAction();
            sendMessage(act, name, ps);
            complete(true);
        }else{
            act = new AddSpacesAction(this.space+1, true);
            List<Action> actions = new LinkedList<>();
            actions.add(act);
            then(actions, ()-> complete(true));
            sendMessage(act, name, pool.getPrivateState(name));
        }



    }
}
