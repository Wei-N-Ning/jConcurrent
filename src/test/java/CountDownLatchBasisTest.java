import junit.framework.TestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// java reference 9
// P923/958
//
// sometimes you will want a thread to wait until one or more
// events have occurred.

public class CountDownLatchBasisTest extends TestCase {

    public void testCreateLatch() {
        // num specifies the number of events that must occur
        // in order for the latch to open
        CountDownLatch latch = new CountDownLatch(10);
    }

    public void testWaitForLatch() {
        CountDownLatch latch = new CountDownLatch(1);

        // waits only for the period of time specified by wait
        // the unit is specified by TimeUnit
        boolean flag = true;
        try {
            flag = latch.await(10, TimeUnit.NANOSECONDS);
        }
        catch (InterruptedException e) {
            ;
        }
        assertFalse(flag);
    }

    // P925/960 example
    public void testDemoCountDownLatch() {
        CountDownLatch[] latch = {new CountDownLatch(3)};
        int[] result = {0};

        Thread workThread = new Thread(
            () -> {
                try {
                    latch[0].await();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                result[0] = 101;
            }
        );
        workThread.start();

        Thread countThread = new Thread(
            () -> {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                latch[0].countDown();
                latch[0].countDown();
                latch[0].countDown();
            }
        );
        countThread.start();

        try {
            countThread.join();
            workThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(101, result[0]);
    }

}
