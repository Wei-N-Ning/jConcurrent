import junit.framework.TestCase;

// java reference P915/950

// the term concurrent program refers to a program that
// makes extensive, integral use of concurrently executing
// threads

// An example of such a program is one that uses separate
// threads to simultaneously compute the partial results
// of a larger computation.

// another example is a program that coordinates the
// activities of several threads, each of which seeks
// a ccess to information in a database. In this case
// read only accesses might be handled differently from
// those that require read/write capabilities.

// Fork/Join framework streamlines the development of
// programs in which two or more pieces execute with
// true simultaneity (that is, true parallel execution)
// not just time-slicing.

// P917/952
// A Future contains a value that is returned by a
// thread after it executes. Thus its value becomes
// defined "in the future", when the thread terminates.

// Callable defines a thread that returns a value.

public class ConcurrencyBasisTest extends TestCase {

    public void testAll() {
        ;
    }
}
