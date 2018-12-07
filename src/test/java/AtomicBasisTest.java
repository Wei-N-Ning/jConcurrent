import junit.framework.TestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// java reference 9
// P946/981

// methods that get, set or compare the value of a variable
// in one uninterruptible (atomic) operation
// this means that no lock or other synchronisation
// mechanism is required.

public class AtomicBasisTest extends TestCase {

    public void testAtomicInt() {
        AtomicInteger res = new AtomicInteger(0);
        res.getAndAdd(10);

        assertEquals(10, res.get());
    }

    public void testConcurrentModification() {
        AtomicInteger res = new AtomicInteger(0);
        int numThreads = 8;
        int workload = 32;

        ExecutorService pool =
            Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < workload; ++i)
        pool.execute(
            () -> {
                try {
                    Thread.sleep((int)(Math.random() * 10));
                }
                catch (InterruptedException e) {
                    ;
                }
                res.getAndAdd(2);
            }
        );
        try {
            pool.awaitTermination(100, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            ;
        }
        System.out.println(res.get());
    }
}
