import junit.framework.TestCase;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.function.UnaryOperator;

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
// ForkJoinTasks are executed by threads managed by a thread
// pool of type ForkJoinPool. This mechanism allows a large
// number of tasks to be managed by a small number of
// actual threads. Thus ForkJoinTasks are very efficient
// when compared to threads.

// RecursiveAction class:
// the compute() method represents the computational
// portion of the task
// in general, RecursiveAction is used to implement a
// recursive, divide-and-conquer strategy for tasks
// that don't return results

// common pool: a static ForkJoinPool that is automatically
// available for your use.

// P952/987
// the divide-and-conquer strategy is based on recursively
// dividing a task into smaller subtasks until the size of
// a subtask is small enough to be handled sequentially.
// ... the process of dividing will continue until a
// threshold is reached in which a sequential solution is
// faster than creating another division

// one of the keys to best employing the divide-and-conquer
// strategy is correctly selecting the threshold at which
// sequential processing (rather than further division) is
// used. Typically an optimal threshold is obtained through
// profiling the executing characteristics.

class ExampleConcurrentTask extends RecursiveTask<String> {

    private UnaryOperator<String> f;
    private String input = "";

    ExampleConcurrentTask(UnaryOperator<String> f) {
        this.f = f;
    }

    public void setInput(String i) {
        input = i;
    }

    public String compute() {
        return f.apply(input);
    }
}

class ExampleSquareTransform extends RecursiveAction {

    private double[] data;
    private int start;
    private int end;

    // P952/987
    // the java API document for ForkJoinTask<T> states that
    // as a rule-of-thumb, a task should perform somewhere
    // between 100 and 10000 computational steps
    final int seqThreshold = 1000;

    ExampleSquareTransform(double[] vals, int s, int e) {
        data = vals;
        start = s;
        end = e;
    }

    // start parallel computation
    public void compute() {
        if ((end - start) < seqThreshold) {
            // use sequential strategy
            for (int i = start; i < end; ++i) {
                data[i] = Math.sqrt(data[i]);
            }
        }
        else {
            // use recursive divide-and-conquer strategy
            int mid = (start + end) / 2;
            invokeAll(
                new ExampleSquareTransform(data, start, mid),
                new ExampleSquareTransform(data, mid, end)
            );
        }
    }
}

class ExampleSumTask extends RecursiveTask<Double> {

    final int seqThreshold = 500;
    private double[] data;
    private int start;
    private int end;

    ExampleSumTask(double[] vals, int s, int e) {
        data = vals;
        start = s;
        end = e;
    }

    public Double compute() {
        if ((end - start) < seqThreshold) {
            // use sequential strategy
            double sum = 0;
            for (int i = start; i < end; ++i) {
                sum += data[i];
            }
            return sum;
        }
        else {
            // use divide-and-conquer strategy
            int mid = (start + end) / 2;
            ExampleSumTask first =
                new ExampleSumTask(data, start, mid);
            ExampleSumTask second =
                new ExampleSumTask(data, mid, end);

            // fork() starts a task but does not wait for it
            // to finish (async)
            // the result of each task is obtained by calling
            // join()
            first.fork();
            second.fork();

            return first.join() + second.join();
        }
    }
}

public class ForkJoinFrameworkBasisTest extends TestCase {

    public void testCreateForkJoinPool() {
        // supports a level of parallelism equals to
        // the number of processors available in the
        // system
        ForkJoinPool p0 = new ForkJoinPool();

        // specify the level of parallelism
        ForkJoinPool p4 = new ForkJoinPool(4);

        // level of parallelism is only a target,
        // not a guarantee
    }

    public void testCreateFirstTask() {
        // the first task started is often thought of as the main
        // task. Frequently the main task begins subtasks that
        // are also managed by the pool
        ForkJoinPool p = new ForkJoinPool();

        String result = p.invoke(new ExampleConcurrentTask(
            (String s) -> {
                return "there is a cow";
            }
        ));
        assertEquals("there is a cow", result);

        // or use the common pool
        // which has a default level of parallelism
        result = ForkJoinPool.commonPool().invoke(
            new ExampleConcurrentTask(
                (String s) -> {
                    return "there is a cow";
                }
            )
        );
        assertEquals("there is a cow", result);

        // you can call ForkJoinTask methods such as fork()
        // or invoke() on the task from outside its
        // computational portion. In this case, the common
        // pool will automatically be used.
        // In other words, fork() and invoke() will start
        // a task using the common pool if the task is
        // not already running within a ForkJoinPool

    }

    // see the example on P954/989
    // see also the profiling example on P958/993
    // Note that ForkJoinAction does not return the computation
    // result back to the caller.
    public void testDemoForkJoinAction() {
        ForkJoinPool pool =
            new ForkJoinPool();

        // observe the CPU usage in htop
        double[] nums = new double[10000000];
        for (int i = 0; i < nums.length; ++i) {
            nums[i] = (double)i;
        }

        ExampleSquareTransform task =
            new ExampleSquareTransform(nums, 0, nums.length);

        pool.invoke(task);

        assertEquals(3.0, nums[9]);
    }

    // P959/993
    // ForkJoinTask class's compute() method returns a result.
    // Thus you must aggregate the results so that when the
    // first invocation finishes, it returns the overall result.
    // Another difference is that you will typically start a
    // subtask by calling fork() and join() explicitly (rather
    // than implicitly by calling invokeAll() )
    public void testDemoForkJoinTask() {
        ForkJoinPool pool =
            new ForkJoinPool();

        double[] nums = new double[10000000];
        for (int i = 0; i < nums.length; ++i) {
            nums[i] = (double)
                ((i % 2) == 0 ? i : -i);
        }

        ExampleSumTask task =
            new ExampleSumTask(nums, 0, nums.length);

        double s = pool.invoke(task);

    }
}

