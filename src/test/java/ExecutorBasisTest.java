import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// java reference 9
// P937/972

// initiates and controls the execution of threads;
// as such an executor offers an alternative to managing
// threads through hte Thread class

// at the code of an executor is the Executor interface;

// The ExecutorService interface extends Executor by
// adding methods that help manage and control the
// execution of threads. For example ExecutorService
// defines shutdown(), which stops the invoking
// ExecutorService.

// the concurrent API defines 3 predefined executor
// classes:
// ThreadPoolExecutor
// ScheduledThreadPoolExecutor
// ForkJoinPool

// A thread pool provides a set of threads that is used
// to execute .... in stead of each task using its own
// thread. This reduces the overhead associated with
// creating many separate threads.

public class ExecutorBasisTest extends TestCase {

    public void testCreateFixedThreadPool() {
        int workload = 10;
        int numThread = 4;

        ExecutorService pool =
            Executors.newFixedThreadPool(numThread);
        ArrayList<Object> al = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(workload);

        for (int i = 0; i < workload; ++i) {
            pool.execute(
                () -> {
                    // expensive computation
                    int result = (int)(Math.random() * 100);
                    try {
                        Thread.sleep(result);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // upload result, synchronised
                    synchronized (al) {
                        al.add(result);
                        // log access
                        al.add(
                            Thread.currentThread().getName()
                        );
                    }
                    latch.countDown();
                }
            );
        }
        try {
            latch.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        pool.shutdown();
        al.forEach(System.out::println);
    }
}
