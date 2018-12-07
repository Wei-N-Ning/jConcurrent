import junit.framework.TestCase;

import java.util.concurrent.*;

// java reference 9
// P939/974
//
// callable interface:
// represents a thread that returns a value;

// it facilitates the coding of many types of numerical
// computations in which partial results are computed
// simultaneously.

// it can also be used to run a thread that returns a
// status code that indicates the successful completion
// of the thread

public class FutureBasisTest extends TestCase {

    // see P941/976 example
    // note the use of pool.submit() to get a future
    // object
    public void testCallableInterface() {
        ExecutorService pool =
            Executors.newFixedThreadPool(4);
        Future<String> fut = pool.submit(
            () -> {
                Thread.sleep(10);
                return "there is a cow";
            }
        );
        try {
            String s = fut.get();
            assertEquals("there is a cow", s);
        }
        catch (InterruptedException e) {
            ;
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    // P943/978
    // note the semantics here:
    // (to convert) from UNIT to OTHER UNIT
    public void testTimeUnitConversion() {
        long asMicro = TimeUnit.SECONDS.toMicros(1);
        long asMilli = TimeUnit.SECONDS.toMillis(1);
        assertEquals(1000 * asMilli, asMicro);

    }
}
