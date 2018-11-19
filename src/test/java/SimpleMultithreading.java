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



public class SimpleMultithreading {

    public static void main(String[] args) {
        ;
    }

}
