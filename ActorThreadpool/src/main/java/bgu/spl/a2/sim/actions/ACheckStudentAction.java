package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class ACheckStudentAction<Boolean> extends Action {
    private Promise<Computer> computerPromise;
    private ArrayList<String> courses;

    /**
     * Constructor
     * @param computerPromise
     * @param courses
     */
    public ACheckStudentAction(Promise<Computer> computerPromise, ArrayList<String> courses) {
        super();
        setActionName("Administrative Student Check");
        this.computerPromise = computerPromise;
        this.courses = courses;
    }

    /**
     * runs the action
     * subscribing to a computer, when it's resolved, fix student's signature
     */
    @Override
    protected void start() {
        computerPromise.subscribe(()->{
            ((StudentPrivateState)actorState).setSignature(computerPromise.get()
                    .checkAndSign(courses, ((StudentPrivateState)actorState).getGrades()));
            complete(true);
        });
    }
}
