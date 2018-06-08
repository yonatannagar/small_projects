package bgu.spl.a2.sim;
import bgu.spl.a2.Promise;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * 
 * Note: this class can be implemented without any synchronization. 
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 *
 */
public class SuspendingMutex {
	private Computer comp;
	private ConcurrentLinkedQueue<Promise> promisesQ;
	private AtomicBoolean isFree;

	/**
	 * Constructor
	 * @param computer
	 */
	public SuspendingMutex(Computer computer){
	    comp=computer;
        promisesQ=new ConcurrentLinkedQueue<>();
        isFree=new AtomicBoolean(true);
	}
	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 * 
	 * @return a promise for the requested computer
	 */
	public Promise<Computer> down(){
        Promise<Computer> p = new Promise<>();
	    if(isFree.compareAndSet(true, false)){//computer is free at this moment
            p.resolve(comp);
        }else promisesQ.add(p);
        return p;
	}
	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public void up(){
	    if(!promisesQ.isEmpty())
            promisesQ.poll().resolve(comp);
        else
            isFree.set(true);
	}

	protected String getComputersName(){
	    return comp.computerType;
    }
}
