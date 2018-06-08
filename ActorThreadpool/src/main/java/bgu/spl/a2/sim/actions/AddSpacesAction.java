package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

public class AddSpacesAction<Boolean> extends Action{
    private int amount;
    private boolean reopenAction;

    /**
     * Constructor
     * @param incByAmount
     */
    public AddSpacesAction(int incByAmount, boolean reopenAction){
        super();
        setActionName("Add Spaces");
        amount=incByAmount;
        this.reopenAction=reopenAction;
    }

    /**
     * runs the action
     */
    @Override
    protected void start() {
        if(!reopenAction && ((CoursePrivateState)actorState).getAvailableSpots()==-1){
            complete(false);
            return;
        }
        ((CoursePrivateState)actorState).incSpaces(amount);
        complete(true);
    }

}
