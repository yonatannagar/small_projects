import bgu.spl.a2.VersionMonitor;
import jdk.nashorn.internal.ir.annotations.Ignore;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

/**
 * Created by yonatan on 12/5/17.
 */
public class VersionMonitorTest {
    private VersionMonitor vm;
    @Before
    public void setUp() {
        vm= new VersionMonitor();
    }

    @Test
    public void getVersion(){
        try {
            assertEquals("getVersion failed",0, vm.getVersion());
        }catch(AssertionError err){
            fail(err.getMessage());
        }
    }

    @Test
    public void inc(){
        for (int i = 0; i < 5; ++i) {
            try{
                assertEquals("inc failed", i, vm.getVersion());
            }catch(Exception err){
                fail(err.getMessage());
            }
            vm.inc();
        }
    }

    @Test (timeout = 8000)
    public void await() throws InterruptedException{
        boolean a[] = {false};
        Thread t = new Thread(()-> {
            try {
                vm.await(0);
                a[0]=true;
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });
        t.start();
        while(Thread.State.RUNNABLE == t.getState() ||
                Thread.State.NEW == t.getState());
        if(Thread.State.BLOCKED!=t.getState() &&
                Thread.State.WAITING!=t.getState()){
            fail("thread is not waiting");
        }
        vm.inc();
        t.join();
        try{
            assertTrue("await failed, didn't flip a[0]",a[0]);
        }catch(Exception err){
            fail(err.getMessage());
        }
        boolean b[] = {false};
        Thread t2 = new Thread(()->{ //should work instantly on start
            try {
                vm.await(5);
                if(Thread.currentThread().getState() == Thread.State.BLOCKED ||
                        Thread.currentThread().getState() == Thread.State.WAITING) {
                    fail("Thread was waiting/blocked when it shouldn't");
                }
                b[0]=true;
            }catch(Exception e){
                fail("Thread was waiting/blocked when it shouldn't");}
        });
        t2.start();
        t2.join();
        try{
            assertTrue("await failed, didn't flip b[0]",b[0]);
        }catch(Exception err){fail(err.getMessage());}
    }
}