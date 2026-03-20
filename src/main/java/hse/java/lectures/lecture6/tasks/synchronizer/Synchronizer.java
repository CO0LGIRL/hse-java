package hse.java.lectures.lecture6.tasks.synchronizer;

import java.util.Comparator;
import java.util.List;

public class Synchronizer {

    public static final int DEFAULT_TICKS_PER_WRITER = 10;
    private final List<StreamWriter> tasks;
    private final int ticksPerWriter;

    public Synchronizer(List<StreamWriter> tasks) {
        this(tasks, DEFAULT_TICKS_PER_WRITER);
    }

    public Synchronizer(List<StreamWriter> tasks, int ticksPerWriter) {
        this.tasks = tasks;
        this.ticksPerWriter = ticksPerWriter;
    }

    public void execute() {
        tasks.sort(Comparator.comparingInt(StreamWriter::getId));
        int[] orderedIds = tasks.stream().mapToInt(StreamWriter::getId).toArray();

        StreamingMonitor monitor = new StreamingMonitor(orderedIds, ticksPerWriter);

        for (StreamWriter writer : tasks) {
            writer.attachMonitor(monitor);
            Thread worker = new Thread(writer, "stream-writer-" + writer.getId());
            worker.setDaemon(true);
            worker.start();
        }

        try {
            monitor.awaitCompletion();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}