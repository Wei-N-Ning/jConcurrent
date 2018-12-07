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

    public void testDoCountDown() {
        Thread t = new Thread(
            () -> {
                return;
            }
        );
    }
}
