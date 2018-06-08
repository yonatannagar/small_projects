package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;

/**
 * Action extending class for actors creation
 * @param <Boolean>
 */
public class VoidAction<Boolean> extends Action {
    /**
     * Constructor
     */
    public VoidAction() {
        setActionName("Void Action");
    }

    /**
     * runs this action
     */
    @Override
    protected void start() {
        complete(true);
    }
}
