package bgu.spl.a2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;


/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {
	private Thread[] pool;
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<Action>> actorList;
	private ConcurrentHashMap<String, PrivateState> actorStateList;
    private ConcurrentHashMap<String, AtomicBoolean> lockList; //each actor gets a lock, initialized true, flipped false if in use
    private boolean terminated;
    private VersionMonitor vm;

    /**
	 * creates a {@link ActorThreadPool} which has nthreads. Note, threads
	 * should not get started until calling to the {@link #start()} method.
	 *
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads
	 *            the number of threads that should be started by this thread
	 *            pool
	 */
	public ActorThreadPool(int nthreads) {
		pool=new Thread[nthreads];
		actorList=new ConcurrentHashMap<>();
		actorStateList=new ConcurrentHashMap<>();
		lockList=new ConcurrentHashMap<>();
		terminated=false;
		vm=new VersionMonitor();
		Object o = new Object();
		for(int i=0; i<nthreads; i++) {
            pool[i] = new Thread(() -> {
                int version = vm.getVersion();
                while (!terminated) {
                    for (Map.Entry<String, ConcurrentLinkedQueue<Action>> current : actorList.entrySet()) {
                        String actorId = current.getKey();
						synchronized (o) {
							try {
								if (lockList.get(actorId).compareAndSet(true, false)) {
									if (!(current.getValue().isEmpty())) {//q isnt empty + im allowed to grab from it: lock, grab, handle
										current.getValue().poll().handle(this, actorId, actorStateList.get(actorId));
										vm.inc();
									}
									fixLock(actorId);
								}
							}catch(NullPointerException ignored){}
                        }
                    }
                    try {
                        vm.await(version);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
	}

	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 *
	 * @param action
	 *            the action to execute
	 * @param actorId
	 *            corresponding actor's id
	 * @param actorState
	 *            actor's private state (actor's information)
	 */
	public void submit(Action<?> action, String actorId, PrivateState actorState) {
		if(!actorList.containsKey(actorId)){
			synchronized (actorId) {
				actorList.put(actorId, new ConcurrentLinkedQueue<>());
				actorStateList.put(actorId, actorState);
				lockList.put(actorId, new AtomicBoolean(true));
			}
		}
		actorList.get(actorId).add(action);
		vm.inc();
	}

	/**
	 * closes the thread pool - this method interrupts all the threads and waits
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 *
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException
	 *             if the thread that shut down the threads is interrupted
	 */
	public void shutdown() throws InterruptedException {
	    terminated=true;
	    for(Map.Entry<String, AtomicBoolean> currLock: lockList.entrySet())
	        currLock.getValue().set(false);
	    vm.inc();
	    for (Thread t:pool){
	        if(t!=null && t.getState()!=Thread.State.TERMINATED)
            t.interrupt();
        }
        for(Thread t:pool){
	        if(t!=null)
	            t.join();
        }

	}

	/**rns *only* when there are no live t
	 * start the threads belongs to this thread pool
	 */
	public void start() {
	    for(Thread t:pool){
	        t.start();
        }
	}

	protected void incVersion(){
	    vm.inc();
    }

    /**
     * Unlocks actor: actorId
     * @param actorId
     */
    protected void fixLock(String actorId){
        lockList.get(actorId).compareAndSet(false, true);
    }

    /**
     * getter for actors
     * @return actors
     */
    public Map<String, PrivateState> getActors(){
        Map map = new HashMap<String, PrivateState>(actorStateList);
        return map;
    }

    /**
     * getter for actor's private state
     * @param actorId actor's id
     * @return actor's private state
     */
    public PrivateState getPrivateState(String actorId){
        return actorStateList.get(actorId);
    }

}
