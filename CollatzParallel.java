import java.util.concurrent.*;
import java.util.*;

public class CollatzParallel {

    // Обчислення кількості кроків за гіпотезою Коллатца
    static long collatzSteps(long n) {
        long steps = 0;
        while (n != 1) {
            if ((n & 1) == 0) {   // парне
                n >>= 1;
            } else {               // непарне
                n = 3 * n + 1;
            }
            steps++;
        }
        return steps;
    }

    // Завдання для потоку: обробка діапазону чисел
    static class CollatzTask implements Callable<Long> {
        private final long start;
        private final long end;

        CollatzTask(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Long call() {
            long total = 0;
            for (long i = start; i <= end; i++) {
                total += collatzSteps(i);
            }
            return total;
        }
    }

    public static void main(String[] args) throws Exception {
        final long N = 10_000_000L;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введіть кількість потоків (0 — автоматично): ");
        int numThreads = scanner.nextInt();
        if (numThreads <= 0) {
            numThreads = Runtime.getRuntime().availableProcessors();
        }

        System.out.println("Кількість чисел: " + N);
        System.out.println("Кількість потоків: " + numThreads);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Long>> futures = new ArrayList<>();

        long chunkSize = N / numThreads;
        long startNum = 1;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            long endNum = (i == numThreads - 1) ? N : startNum + chunkSize - 1;
            futures.add(executor.submit(new CollatzTask(startNum, endNum)));
            startNum = endNum + 1;
        }

        long totalSteps = 0;
        for (Future<Long> f : futures) {
            totalSteps += f.get();
        }

        executor.shutdown();

        double averageSteps = (double) totalSteps / N;
        long executionTime = System.currentTimeMillis() - startTime;

        System.out.println("Середня кількість кроків: " + averageSteps);
        System.out.println("Час виконання: " + executionTime + " мс");
    }
}