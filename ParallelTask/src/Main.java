import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class Main {
    public static void main(String[] args) {
        runBenchmark3003();
    }

    public static void runBenchmark3003() {
        ParallelBenchmark parallelBenchmark = new ParallelBenchmark();
        long n = 10_000_000;
        System.out.println("--- Benchmark start of task 3003 ---");
        System.out.println("Data generating (" + n + " amount of number)...");

        List<Double> listOfDoubles = DoubleStream.generate(() -> ThreadLocalRandom.current().nextDouble(10000))
                .parallel()
                .limit(n)
                .boxed()
                .collect(Collectors.toList());

        System.out.println("--- Datas are ready. Start of time measure... \n");

        long startTimeOfSequential = System.nanoTime();
        parallelBenchmark.assortmentSequential(listOfDoubles);
        long endTimeOfSequential = System.nanoTime();

        long durationTimeOfSequential = (endTimeOfSequential - startTimeOfSequential) / 1_000_000;//ms
        System.out.println("Sequential time: " + durationTimeOfSequential + " ms");

        long startTimeOfParallel = System.nanoTime();
        parallelBenchmark.assortmentParallel(listOfDoubles);
        long endTimeOfParallel = System.nanoTime();

        long durationTimeOfParallel = (endTimeOfParallel - startTimeOfParallel) / 1_000_000;//ms
        System.out.println("Parallel time: " + durationTimeOfParallel + " ms");


        double speedup = (double) durationTimeOfSequential / durationTimeOfParallel;
        System.out.println("---------------------------------------------");
        System.out.printf(" (Speedup): %.2fx\n", speedup);

        if (speedup > 1.0) {
            System.out.println("Result: The parallel benchmark is faster!.");
        } else {
            System.out.println("Result: The parallel benchmark is slower!");
        }
    }
}