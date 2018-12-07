import junit.framework.TestCase;

import java.util.concurrent.Exchanger;

// java reference 9
// P927/962

// designed to simplify the exchange of data between two
// threads

// it waits until two separate threads call its exchange()
// method. When that occurs, it exchanges the data supplied
// by the threads.

// for example one thread might prepare a buffer for
// receiving information over a network connection.
// Another thread might fill that buffer with the information
// from the connection. The two threads work together so
// that each time a new buffer is needed, an exchange is made.

public class ExchangerBasisTest extends TestCase {

    public void testCreateExchanger() {
        Exchanger<String> ex = new Exchanger<>();
    }

    public void testDemoExchangingData() {
        Exchanger<String> ex = new Exchanger<>();
        String[] result = {""};

        Thread producer = new Thread(
            () -> {
                byte[] message = {'t', 'h', 'e', 'r', 'e', '\n'};
                try {
                    ex.exchange(new String(message));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        );
        producer.start();

        Thread consumer = new Thread(
            () -> {
                try {
                    String s = ex.exchange("");
                    result[0] = s;
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        );
        consumer.start();

        try {
            consumer.join();
            producer.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("there\n", result[0]);
    }
}
