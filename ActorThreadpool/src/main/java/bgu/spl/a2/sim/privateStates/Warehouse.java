package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.ArrayList;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 *
 * Holding a list of Suspending Mutexes, each holding its own computer
 */
public class Warehouse {
    private ArrayList<SuspendingMutex> computerSuspMutex = new ArrayList<>();

    /**
     * Adds a computer to the list
     * @param computer
     */
    public void addComputerByName(Computer computer){
        computerSuspMutex.add(new SuspendingMutex(computer));
    }

    /**
     * Tries to acquire a computer by name
     * @param name
     * @return Promise to the computer
     */
    public Promise<Computer> getComputerByName(String name){
        for(SuspendingMutex mutex:computerSuspMutex){
            if(mutex.getComputersName().equals(name))
                return mutex.down();
        }
        //no specified computer
        return null;
    }

    /**
     * Tries to release a computer by name
     * @param name
     */
    public void releaseComputerByName(String name){
        for(SuspendingMutex mutex:computerSuspMutex){
            if(mutex.getComputersName().equals(name)){
                mutex.up();
            }
        }
    }
}
