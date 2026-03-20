package hse.java.lectures.lecture6.tasks.synchronizer;

public class StreamingMonitor {
    private final int[] orderedIds;
    private final int maxTotalTicks;
    private int currentTurnIndex = 0;
    private int currentTotalTicks = 0;

    public StreamingMonitor(int[] orderedIds, int ticksPerWriter) {
        this.orderedIds = orderedIds;
        this.maxTotalTicks = orderedIds.length * ticksPerWriter;
    }

    public synchronized boolean waitMyTurn(int id) throws InterruptedException {
        while (currentTotalTicks < maxTotalTicks && orderedIds[currentTurnIndex] != id) {
            wait();
        }
        return currentTotalTicks < maxTotalTicks;
    }

    public synchronized void finishMyTurn() {
        currentTotalTicks++;
        currentTurnIndex = (currentTurnIndex + 1) % orderedIds.length;
        notifyAll();
    }

    public synchronized void awaitCompletion() throws InterruptedException {
        while (currentTotalTicks < maxTotalTicks) {
            wait();
        }
    }
}