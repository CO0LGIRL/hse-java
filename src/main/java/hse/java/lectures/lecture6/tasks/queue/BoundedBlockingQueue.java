package hse.java.lectures.lecture6.tasks.queue;

public class BoundedBlockingQueue<T> {

    private class Node<T> {
        T value;
        Node<T> next;

        Node(T value_) {
            value = value_;
            next = null;
        }
    }

    private final int capacity_;
    private int size_;
    private Node<T> first = null;
    private Node<T> last = null;

    public BoundedBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        capacity_ = capacity;
    }

    public synchronized void put(T item) throws InterruptedException {
        if (item == null) {
            throw new NullPointerException("");
        }
        Node<T> node = new Node<>(item);
        while (isFull()) {
            this.wait();
        }
        if (first == null) {
            first = node;
            last = node;
        } else {
            last.next = node;
            last = node;
        }
        size_++;
        this.notifyAll();
    }

    public synchronized boolean isFull() {
        return size_ == capacity_;
    }

    public synchronized boolean isEmpty() {
        return size_ == 0;
    }

    public synchronized  T take() throws InterruptedException {
        while (isEmpty()) {
            this.wait();
        }
    
        T ret = first.value;

        first = first.next;
        if (first == null) {
            last = null;
        }
        size_--;
        this.notifyAll();

        return ret;
    }

    public synchronized int size() {
        return size_;
    }

    public synchronized int capacity() {
        return capacity_;
    }
}
