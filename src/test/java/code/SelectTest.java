package code;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class SelectTest {
    @BeforeAll
    static void clearMetricsFile() {
        try {
            Files.deleteIfExists(Paths.get("target/trackmetrics.csv"));
        } catch (IOException e) {
            System.err.println("Error while trying to delete metrics file: " + e.getMessage());
        }
    }
    @BeforeEach
    void resetStaticMetrics() {
        Select.lastRunMetrics = null;
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 2000})
    public void testDeterministicSelect(int size) throws IOException {
        int[] arr = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(size * 10);
        }
        int k = size / 2;

        long startTime = System.nanoTime();

        int selected = Select.deterministicSelect(arr, k);

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        TrackMertics metrics = Select.lastRunMetrics;
        assertNotNull(metrics, "Metrics should have been generated");

        System.out.println("Deterministic Select on " + size + " elements took: " + duration + " nanoseconds.");

        metrics.writeMetricsToCSV(duration, "DeterministicSelect" + size);

        int[] sortedArr = arr.clone();
        Arrays.sort(sortedArr);
        assertEquals(sortedArr[k], selected, "Selected element should be the " + k + "-th element.");
    }
}
