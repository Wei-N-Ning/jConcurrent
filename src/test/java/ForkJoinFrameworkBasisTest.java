import junit.framework.TestCase;

// jave reference 9
// P948/983

// parallel programming is the name commonly given to
// the techniques that take advantage of computers that
// contain two or more processors (multicore).

// Fork/Join framework enhances multithreaded programming:

// > it simplifies the creation and use of multiple threads,

// > it automatically makes use of multiple processors

// in the past most computers had a single CPU and
// multithreading was primarily used to take advantage
// of idle time, such as when a program is waiting for
// user input.

// in other words, on a single-CPU system, multithreading
// is used to allow two or more tasks to share the CPU.

// when multiple CPUs are present, a second type of multi
// threading... it is possible to execute portions of a
// program simultaneously, with each part executing on
// its own CPU.

// ForkJoinTask<V> is an abstract class that defines a
// task that can be managed by a ForkJoinPool..
// ForkJoinTask differs from Thread in that ForkJoinTask
// represents lightweight abstraction of a task, rather
// than a thread of execution.

public class ForkJoinFrameworkBasisTest extends TestCase {

    public void testAll() {
        ;
    }
}

