// source
// java reference 9th
//
// P234/269
// single threaded systems uses an approach called an
// event loop with polling
// in this model, a single thread of control runs in an
// infinite loop, polling a single event queue to decide
// what to do next
// in general, in a single-threaded environment when a
// thread blocks (that is, suspends execution) because it
// is waiting for some resource, the entire program
// stops running

// in a single-core system, concurrently executing threads share
// the CPU, with each thread receiving a slice of CPU time.
// therefore in a single-core system, two or more threads do not
// actually run at the same time, but idle CPU time is utilized

// however in multi-core systems, it is possible for two or more
// threads to actually execute simultaneously

// a thread's priority is used to decide when to switch from one
// running thread to the next.

// P235/270
// the rules that determine when a context switch takes place are

// > a thread can voluntarily relinquish control
// by explicitly yielding, sleeping, or blocking on pending IO
// all other threads are examined and the highest-priority thread
// that is ready to run is given the CPU

// > a thread can be preempted by a higher-priority thread
// no matter what it (the preempted thread) is doing;
// basically as soon as a higher-priority thread wants to run,
// it does.

// P236/271
// an age-old model of interprocess synchronisation: the monitor
// you can think of a monitor as a very small box that can hold
// hold only one thread. Once a thread enters a monitor, all other
// threads must wait until that thread exits the monitor;
// in this way a monitor can be used to protect a shared asset
// from being manipulated by more than one thread at a time

// in java .... instead each object has its own implicit monitor
// that is automatically entered when one of the object's
// synchronised methods is called.
// once a thread is inside a synchronised method, no other thread
// can call any other synchronised method on the same object.

public class SimpleMultithreading {

    private void verify(boolean expr, String msg) {
        if (!expr) {
            System.out.println(msg);
            System.exit(1);
        }
    }

    private void testGetCurrentThread() {
        // java reference 9th
        // P237/272

        Thread t = Thread.currentThread();
        System.out.println("curr:" + t);
        t.setName("thereisacow");
        verify("thereisacow".equals(t.getName()),
            "set/get thread name");
    }

    private void testThreadSleep() {
        // NOTE that use of try-catch
        // sleep() takes milliseconds
        // sleep() method in Thread might throw an InterruptedExecption,
        // this would happen if some other thread wanted to interrupt
        // this sleeping one.... in a real program you would need to
        // handle this differently

        Thread t = Thread.currentThread();
        try {
            for (int i = 0; i < 5; ++i) {
                System.out.print(i);
                Thread.sleep(100);
            }
        }
        catch (InterruptedException e) {
            System.out.println("main thread interrupted");
        }
    }

    private void testThreadCreation() {
        // java defines two ways to create a thread

        // > implement the Runnable interface

        class Worker implements Runnable {
            private int m_result;
            public void run() {
                // this thread will end when run() returns
                m_result = 10;
            }
        }
        Worker w = new Worker();
        Thread t = new Thread(w, "worker_1");
        t.start();
        try {
            t.join();
        }
        catch (InterruptedException e) {
            verify(false, "unexpected interruption");
        }
        verify(w.m_result == 10, "worker #1 result");

        // > extend the Thread class

        class WorkerThread extends Thread {
            private int result;
            private WorkerThread(String name) {
                super(name);
            }
            public void run() {
                result = 11;
            }
        }
        WorkerThread wth = new WorkerThread("worker_2");
        wth.start();
        try {
            wth.join();
        }
        catch (InterruptedException e) {
            verify(false, "unexpected interruption");
        }
        verify(wth.result == 11, "worker #2 result");
    }

    private void testSynchronizedMethod() {
        // java reference 9th P249/284
        // to serialize access to xxx() method, you must
        // restrict its access to only one thread at a time,
        // keyword: synchronized

        // shared resource
        int[] variables = {0, 0, 0};

        class Incrementer implements Runnable {

            // failing the mark impl() method synchronized results
            // in data race;
            // the result, expected to be 64, varies among 61, 62,
            // 63...
            synchronized private void impl() {
                int var = variables[0];
                for (int i = 0; i < 100; ++i) {
                    var += 1;
                    var -= 1;
                }
                variables[0] = var + 1;
            }

            public void run() {
                impl();
            }
        }
        Incrementer inc = new Incrementer();

        // threads
        Thread[] threads = new Thread[64];
        for (int i = 0; i < 64; ++i) {
            threads[i] = new Thread(inc, "incrementer_" + i);
            threads[i].start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            }
            catch (InterruptedException e) {
                verify(false, "unexpected interruption");
            }
        }

        System.out.println("" + variables[0]);
    }

    private void testSynchronizedStatement() {
        // P249/284
        // when using a third party class that is not thread safe,

        // shared resources
        class Resource {
            int[] variables = {0, 0, 0};
        }
        Resource resource = new Resource();
        synchronized (resource) {

        }

        // alternatively I can use a wrapper class that has the
        // synchronized keyword
    }

    private void spuriousWakeup() {
        // P251/286
        // oracle recommends that calls to wait() should take
        // place within a loop that checks the condition on which
        // the thread is waiting.

        class Q {
            private int n;
            public boolean valueSet = false;
            synchronized int get() {
                while (!valueSet) {
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {
                        System.out.println(e.toString());
                    }
                }
                System.out.println("Got: " + n);
                valueSet = false;
                notify();
                return n;
            }
            synchronized void put(int n) {
                while (valueSet) {
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {
                        System.out.println(e.toString());
                    }
                }
                this.n = n;
                valueSet = true;
                //System.out.println("Put: " + n);
                notify();
            }
        }
        class Producer implements Runnable {
            private Q queue;
            Producer(Q queue) {
                this.queue = queue;
                new Thread(this, "Producer").start();
            }
            public void run() {
                for (int i = 0; i < 10; ++i) {
                    queue.put(i);
                }
            }
        }
        class Consumer implements Runnable {
            private Q queue;
            Consumer(Q queue) {
                this.queue = queue;
                new Thread(this, "Consumer").start();
            }
            public void run() {
                for (int i = 0; i < 10; ++i) {
                    queue.get();
                }
            }
        }

        Q queue = new Q();
        new Producer(queue);
        new Consumer(queue);
    }

    public static void main(String[] args) {
        SimpleMultithreading prog = new SimpleMultithreading();
        prog.testGetCurrentThread();
        prog.testThreadSleep();
        prog.testThreadCreation();
        prog.testSynchronizedMethod();
        prog.testSynchronizedStatement();
        prog.spuriousWakeup();
    }
}
