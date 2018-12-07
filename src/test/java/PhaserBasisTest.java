import junit.framework.TestCase;

import java.util.Arrays;
import java.util.concurrent.Phaser;

// java reference 9
// P930/965

// its primary purpose is to enable the synchronisation of
// threads that represent one or more phases of activity.

// Phaser works a bit like a CyclicBarrier, except that
// it supports multiple phases.

// it is important to understand that Phaser can also be
// used to synchronise only a single phase. In this regard ,
// it acts much like a CyclicBarrier.  However its primary
// use is to synchronise multiple phases.

public class PhaserBasisTest extends TestCase {

    public void testCreatePhaser() {
        // has a registration count of 0
        Phaser ph = new Phaser();

        // has a registration count of N
        Phaser phN = new Phaser(4);

        // the term party is often applied to the objects
        // that register with a phaser.
    }

    public void testRegisterParty() {
        Phaser ph = new Phaser();

        int phase = ph.register();
        System.out.println(phase);
    }

    // P931/966 example
    //
    public void testDemoPhaser() {
        Phaser ph = new Phaser();
        int[] triggers = {0, 0, 0};
        int[] barrels = {0, 0, 0};

        Thread triggerMaker = new Thread(
            () -> {
                // register
                ph.register();

                // phase 1
                triggers[0] = 556;
                ph.arriveAndAwaitAdvance();

                // phase 2
                triggers[1] = 762;
                ph.arriveAndAwaitAdvance();

                // phase 3
                triggers[2] = 1270;
                ph.arriveAndDeregister();
            }
        );
        triggerMaker.start();

        Thread barrelMaker = new Thread(
            () -> {
                // register
                ph.register();

                // phase 1
                barrels[0] = 556;
                ph.arriveAndAwaitAdvance();

                // phase 2
                barrels[1] = 762;
                ph.arriveAndAwaitAdvance();

                // phase 3
                barrels[2] = 1270;
                ph.arriveAndDeregister();
            }
        );
        barrelMaker.start();

        try {
            Thread.sleep(20);

            triggerMaker.join();
            barrelMaker.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Arrays.toString(triggers));
        System.out.println(Arrays.toString(barrels));
    }
}
