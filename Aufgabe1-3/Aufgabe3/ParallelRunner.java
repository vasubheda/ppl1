package Aufgabe3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/*
    Modulisationunit of the class:
    Modul: the class is a modul, it is never instantiated, it only offers static procedures that run
    simulations.
*/
final class ParallelRunner {

    private ParallelRunner() {
    }

    /**
     * runs {@link BudgetSimulation#simulation(Location)} on every location at the same time one task per location, on thread pool sized to the number of available processor cores.
     * Blocks until every simulation is done.
     * precondition: locations != null: every element != null
     * and the simulations are independent (they only share immutable constants) so that running them in parallel is safe.
     * postcondition: every location has been simulated exactly as if the simulations had run one after another.
     */
    static void simulateAllParallel(List<Location> locations) {
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();

        for (Location loc : locations) {
            // each task is independent: it only touches its own location and the immutable constants
            futures.add(pool.submit(() -> BudgetSimulation.simulation(loc)));
        }

        try {
            for (Future<?> f : futures) {
                f.get(); // wait for the task and surface any exception from the worker thread
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("parallel run was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("a simulation failed in a worker thread", e.getCause());
        } finally {
            pool.shutdown();
        }
    }

    /**
     * the same work done sequentially. Only used as a baseline for the timing comparison in benchmark().
     * precondition: locations != null; every element != null.
     * postcondition: every location has been simulated.
     */
    static void simulateAllSequential(List<Location> locations) {
        for (Location loc : locations) {
            BudgetSimulation.simulation(loc);
        }
    }

    /**
     * runs the whole set once sequentially and once in parallel and prints both times plus the speedup
     * so that the goal of the parallel paradigm (finishing faster) becomes measurable.
     * A fresh set of locations is built for each run, because a simulation changes its location and a used location must not be simulated again.
     * precondition: factory != null and returns a fresh, non-empty list of fresh locations on every call
     * postcondition: two independent runs were executed and timed; nothing is returned
     */
    static void benchmark(Supplier<List<Location>> factory) {
        long seq = time(() -> simulateAllSequential(factory.get()));
        long par = time(() -> simulateAllParallel(factory.get()));

        System.out.printf("%n===== Parallel vs. sequential =====%n");
        System.out.printf("cores available : %d%n", Runtime.getRuntime().availableProcessors());
        System.out.printf("sequential      : %d ms%n", seq);
        System.out.printf("parallel        : %d ms%n", par);
        System.out.printf("speedup         : %.2fx%n", par == 0 ? 0.0 : (double) seq / par);
    }

    /**
     * higher-order helper: runs the given action and returns how long it took in milliseconds.
     * precondition: action != null.
     * postcondition: the action has run exactly once; the return value is the elapsed time in ms (>= 0).
     */
    private static long time(Runnable action) {
        long start = System.nanoTime();
        action.run();
        return (System.nanoTime() - start) / 1_000_000;
    }
}