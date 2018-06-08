import bgu.spl.a2.Promise;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by yonatan on 12/6/17.
 */
public class PromiseTest {
    @Test
    public void get(){
        Promise<Long> p1 = new Promise<>();
        try {
            p1.get();
            fail("get test failed, unresolved returned a value");
        }catch(IllegalStateException e){}
        p1.resolve(15L);
        try {
            Long l = 15L;
            assertEquals("get failed", l, p1.get());
        }catch(AssertionError err){
            fail(err.getMessage());
        }
    }

    @Test
    public void isResolved(){
        Promise<Object> p = new Promise<>();
        try{
            assertFalse("isResolved failed", p.isResolved());
        }catch(Exception err){
            fail(err.getMessage());
        }
        p.resolve(new Object());
        try{
            assertTrue("isResolved failed", p.isResolved());
        }catch(Exception err){
            fail(err.getMessage());
        }

    }

    @Test
    public void resolve(){
        Promise<Long> p = new Promise<>();
        p.resolve(15L);
        try{
            Long l = 15L;
            assertEquals("resolve failed", l, p.get());
        }catch(Exception err){
            fail(err.getMessage());
        }
        try {
            p.resolve(10L);
            fail("resolve failed, tried resolving an already resolved promise");
        }catch(IllegalStateException e){}
        catch(Exception e){ fail(e.getMessage()); }
    }

    @Test
    public void subscribe(){
        boolean a[]={false};
        Promise<Object> p = new Promise<>();
        p.subscribe( ()->a[0]=true );
        try{
            assertFalse("subscribe failed, operated instantly", a[0]);
        }catch(Exception err){
            fail(err.getMessage());
        }
        p.resolve(new Object());
        try{
            assertTrue("subscribe failed, did not operate", a[0]);
        }catch(Exception err){
            fail(err.getMessage());
        }
        p.subscribe( ()->a[0]=false );
        try{
            assertFalse("subscribe failed, did not operate instantly", a[0]);
        }catch(Exception err){
            fail(err.getMessage());
        }
        testMultiSubs();
    }
    //multi subscription testing
    private void testMultiSubs() {
        boolean a[]={true, true, true, true};
        Promise<Object> p = new Promise<>();
        p.subscribe(() -> a[0] = false);
        p.subscribe(() -> a[1] = false);
        p.subscribe(() -> a[2] = false);
        p.subscribe(() -> a[3] = false);
        p.resolve(new Object());

        try{
            boolean b[]={false, false, false, false};
            assertArrayEquals("multi-subs failed",b, a);
        }catch(Exception err){
            fail(err.getMessage());
        }
    }

}