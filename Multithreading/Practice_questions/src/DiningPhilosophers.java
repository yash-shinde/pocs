import java.util.concurrent.Semaphore;

public static class DiningPhilosophers {
    Semaphore philosophers;
    Semaphore[] forks;

    public DiningPhilosophers(int philosophers) {
        this.philosophers = new Semaphore(philosophers-1);
        this.forks = new Semaphore[philosophers];
        for (int i = 0; i < 5; i++) forks[i] = new Semaphore(1);
    }

    public void wantsToEat(int philosopherId) throws InterruptedException {
        philosophers.acquire();

        //calculate left and right indexes
        int right = (philosopherId+1)%5;

        forks[philosopherId].acquire();
        forks[right].acquire();

        System.out.println(philosopherId+" is eating");
        forks[philosopherId].release();
        forks[right].release();
        philosophers.release();
    }
}

void main(){
    DiningPhilosophers diners = new DiningPhilosophers(5);
    Thread phil1 = new Thread(()->{try {diners.wantsToEat(0);} catch (InterruptedException e) {throw new RuntimeException(e);}});
    Thread phil2 = new Thread(()->{try {diners.wantsToEat(1);} catch (InterruptedException e) {throw new RuntimeException(e);}});
    Thread phil3 = new Thread(()->{try {diners.wantsToEat(2);} catch (InterruptedException e) {throw new RuntimeException(e);}});
    Thread phil4 = new Thread(()->{try {diners.wantsToEat(3);} catch (InterruptedException e) {throw new RuntimeException(e);}});
    Thread phil5 = new Thread(()->{try {diners.wantsToEat(4);} catch (InterruptedException e) {throw new RuntimeException(e);}});

    phil1.start();phil2.start();phil3.start();phil4.start();phil5.start();
}

