package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static bgu.spl.a2.sim.Simulator.warehouse;

public class AdministrativeCheckAction<Boolean> extends Action {
    private ArrayList<String> studentsList;
    private String computerType;
    private ArrayList<String> coursesList;
    private Promise<Computer> computerPromise;

    /**
     * Constructor
     * @param studentsList
     * @param computerType
     * @param coursesList
     */
    public AdministrativeCheckAction(ArrayList<String> studentsList, String computerType, ArrayList<String> coursesList) {
        super();
        setActionName("Administrative Check");
        this.studentsList = studentsList;
        this.computerType = computerType;
        this.coursesList = coursesList;
    }

    /**
     * runs the action
     * sends each student an action and computer promise to resolve his signature
     */
    @Override
    protected void start() {
        computerPromise=warehouse.getComputerByName(computerType);
        List<Action> actions = new LinkedList<>();
        for(String student:studentsList){
            Action act = new ACheckStudentAction(computerPromise, coursesList);
            sendMessage(act, student, pool.getPrivateState(student));
            actions.add(act);
        }
        then(actions, ()->{
            if(!this.getResult().isResolved()) {
                warehouse.releaseComputerByName(computerType);
                complete(true);
            }
        });
    }
}
