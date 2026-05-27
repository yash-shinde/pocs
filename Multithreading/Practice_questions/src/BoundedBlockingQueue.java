import java.util.concurrent.Semaphore;

public static class BoundedBlockingQueue {
    private Semaphore full;
    private Semaphore empty;
    private ConcurrentLinkedDeque<Integer> queue;

    public BoundedBlockingQueue(int n) {
        queue = new ConcurrentLinkedDeque<>();
        full = new Semaphore(0);
        empty = new Semaphore(n);
    }

    public void enqueue(Integer i) throws InterruptedException {
        empty.acquire();
        queue.addFirst(i);
        full.release();
    }

    public int dequeue() throws InterruptedException {
        int result = -1;
        full.acquire();
        result = queue.pollLast();
        empty.release();
        return result;
    }

    public int size() throws InterruptedException {
        int result = 0;
        // Retrieve and return the size of the deque
        result = queue.size();
        return result;
    }
}

void main(){
    BoundedBlockingQueue bbq = new BoundedBlockingQueue(5);
    Thread t1 = new Thread(()->{
       for(int i=0;i<5;i++){
           try {
               System.out.println("Adding element : "+i);
               bbq.enqueue(i);
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
       }
    });

    Thread t2 = new Thread(()->{
        for(int i = 0; i < 5; i++) {
            try {
                System.out.println("Element polled : " + bbq.dequeue());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    });

    t1.start();t2.start();
}
