import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

// java reference 9
// P918/953

// A semaphore controls access to a shared resource
// through the use of a counter. If the counter is
// greater than zero, then access is allowed.
// If it is zero then access is denied. What the counter
// is counting are permits that allow access to the
// shared resource.

// In general to use a semaphore, the thread that wants
// access to the shared resource tries to acquire a
// permit (counter--). If the semaphore's count is greater than
// zero, then the thread acquires a permit, which causes
// the semaphore's count to be decremented. Otherwise
// the thread will be blocked...
// when the thread no longer needs access to the shared
// resource, it releases the permit (counter++)

// A shared resource
class Shared {
    static int count = 10;
    static ArrayList<String> accessSequence;

    static {
        accessSequence = new ArrayList<>(24);
    }
}

class IncThread implements Runnable {

    private String name;
    private Semaphore sem;

    IncThread(Semaphore s, String n) {
        sem = s;
        name = n;
    }

    public void run() {
        for (int i = 0; i < 10; ++i) {
            runOnce();
        }
    }

    private void runOnce() {
        try {
            sem.acquire();
            Shared.count++;
            Shared.accessSequence.add(this.name);
            sem.release();
            Thread.sleep(10);
        }
        catch (InterruptedException e) {
            System.out.println(e.toString());
        }
    }
}

class DecThread implements Runnable {

    private String name;
    private Semaphore sem;

    DecThread(Semaphore s, String n) {
        sem = s;
        name = n;
    }

    public void run() {
        for (int i = 0; i < 5; ++i) {
            runOnce();
        }
    }

    private void runOnce() {
        try {
            sem.acquire();
            Shared.count--;
            Shared.accessSequence.add(this.name);
            sem.release();
            Thread.sleep(20);
        }
        catch (InterruptedException e) {
            System.out.println(e.toString());
        }
    }
}

// See P920/955
// NOTE this demo is a modified version of the example
// in the book. It shows interleaved access pattern

// access to the shared resource is allowed only after
// a permit is acquired from the controlling Semaphore.
// After access is complete, the permit is released.
// In this way only one thread at a time will access...

public class SemaphoreBasisTest extends TestCase {

    public void testAll() {
        Semaphore sem = new Semaphore(1);

        IncThread inc = new IncThread(sem, "A");
        DecThread dec = new DecThread(sem, "B");

        Thread t1 = new Thread(inc);
        Thread t2 = new Thread(dec);

        t1.start();
        t2.start();

        try {
            Thread.sleep(120);

            t1.join();
            t2.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(Shared.count);
        StringBuilder sbld = new StringBuilder();
        Shared.accessSequence.forEach(sbld::append);
        System.out.println(sbld.toString());

        // verify the post-state
        assertEquals(15, Shared.count);

        // verify the total number of accesses
        // the access sequence is deliberately interlaced
        // ABAABAABAABAABA
        assertEquals(15, Shared.accessSequence.size());
    }
}
