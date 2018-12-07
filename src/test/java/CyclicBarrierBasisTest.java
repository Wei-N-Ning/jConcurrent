import junit.framework.TestCase;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

// java reference 9
// P925/960
//
// when a set of two or more threads must wait at a
// predetermined execution point until all threads
// in the set have reached that point
//
// to handle such situation, the concurrent API supplies
// the CyclicBarrier class. It enables you to define
// a synchronisation object that suspends until the
// specified number of threads has reached the
// barrier point.

public class CyclicBarrierBasisTest extends TestCase {

    public void testCyclicBarrierCreation() {
        // here numThreads specifies the number of threads that
        // much reach the barrier before execution continues;

        // the second form, action specifies a thread that will
        // be executed when the barrier is reached
        CyclicBarrier barrier = new CyclicBarrier(4);
    }

    // P925/960 example
    public void testDemoCyclicBarrier() {
        int[] parts = {0, 0, 0};
        Thread assemble = new Thread(
            () -> {
                System.out.println(Arrays.toString(parts));
            }
        );
        CyclicBarrier barrier = new CyclicBarrier(3, assemble);

        Thread makeTrigger = new Thread(
            () -> {
                try {
                    parts[0] = 103;
                    barrier.await();
                }
                catch (
                    BrokenBarrierException|
                    InterruptedException e) {
                    e.printStackTrace();
                }
            }
        );
        makeTrigger.start();

        Thread makeBarrel = new Thread(
            () -> {
                try {
                    parts[1] = 29;
                    barrier.await();
                }
                catch (
                    BrokenBarrierException|
                        InterruptedException e) {
                    e.printStackTrace();
                }
            }
        );
        makeBarrel.start();

        Thread makeGrip = new Thread(
            () -> {
                try {
                    parts[2] = 46;
                    barrier.await();
                }
                catch (
                    BrokenBarrierException|
                        InterruptedException e) {
                    e.printStackTrace();
                }
            }
        );
        makeGrip.start();

        try {
            assemble.join();
            makeBarrel.join();
            makeGrip.join();
            makeTrigger.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
