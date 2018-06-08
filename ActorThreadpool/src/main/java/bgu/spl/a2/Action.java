package bgu.spl.a2;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the action result  type
 */
public abstract class Action<R>{
    protected String name;
    protected final Promise<R> promise = new Promise<>();
    protected callback mission;   //will be filled by then, should end with this.complete(result)
    protected ActorThreadPool pool;
    protected String actorId;
    protected PrivateState actorState;

    /**
     * start handling the action - note that this method is protected, a thread
     * cannot call it directly.
     */
    protected abstract void start();

    private void fillFields(ActorThreadPool pool, String actorId, PrivateState actorState){
        this.pool = pool;
        this.actorId=actorId;
        this.actorState=actorState;

    }
    /**
     *
     * start/continue handling the action
     *
     * this method should be called in order to start this action
     * or continue its execution in the case where it has been already started.
     *
     * IMPORTANT: this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     */

   /*package*/ final void handle(ActorThreadPool pool, String actorId, PrivateState actorState) {
        fillFields(pool,actorId,actorState);
        if(mission==null){//preconditions are needed
            start(); //fixes action name and preconditions
        }
        else{
            mission.call(); //call fixes the result, should complete
        }
    }


    /**
     * add a callback to be executed once *all* the given actions results are
     * resolved
     *
     * Implementors note: make sure that the callback is running only once when
     * all the given actions completed.
     *
     * @param actions
     * @param callback the callback to execute once all the results are resolved
     *                 push this prepared action into actor's queue
     */
    protected final void then(Collection<? extends Action<?>> actions, callback callback) {
        CountDownLatch latch = new CountDownLatch(actions.size());
        this.mission = callback;
        for (Action act:actions)
            act.getResult().subscribe(() -> {
                latch.countDown();
                synchronized (latch){
                    if (latch.getCount() == 0)
                        pool.submit(this, actorId, actorState);
                }
            });
        pool.fixLock(actorId);
        pool.incVersion();
    }

    /**
     * resolve the internal result - should be called by the action derivative
     * once it is done.
     *
     * @param result - the action calculated result
     */
    protected final void complete(R result) {
        promise.resolve(result);
        if(!getActionName().equals("Void Action"))
            actorState.addRecord(this.getActionName());
    }

    /**
     * @return action's promise (result)
     */
    public final Promise<R> getResult() {
        return promise;
    }

    /**
     * send an action to an other actor
     *
     * @param action
     * 				the action
     * @param actorId
     * 				actor's id
     * @param actorState
     * 				actor's private state (actor's information)
     *
     * @return promise that will hold the result of the sent action
     */
    public Promise<?> sendMessage(Action<?> action, String actorId, PrivateState actorState){
        pool.submit(action, actorId, actorState);
        return action.getResult();
    }

    /**
     * set action's name
     * @param actionName
     */
    public void setActionName(String actionName){
        name=actionName;
    }

    /**
     * @return action's name
     */
    public String getActionName(){
        return name;
    }
}