static class ZeroEvenOdd {
    int n;
    Semaphore zeroSemaphore;
    Semaphore evenSemaphore;
    Semaphore oddSemaphore;

    public ZeroEvenOdd(int n) {
        this.n = n;
        zeroSemaphore = new Semaphore(1);
        evenSemaphore = new Semaphore(0);
        oddSemaphore = new Semaphore(0);
    }

    public void printZero() throws InterruptedException {
        boolean isOdd = true;
        for (int i = 0; i < n; i++) {
            zeroSemaphore.acquire();
            IO.print(0);
            if (isOdd) {
                oddSemaphore.release();
            } else {
                evenSemaphore.release();
            }
            isOdd = !isOdd;
        }
    }

    public void printOdd() throws InterruptedException {
        for (int i = 1; i < n; i += 2) {
            oddSemaphore.acquire();
            IO.print(i);
            zeroSemaphore.release();
        }
    }

    public void printEven() throws InterruptedException {
        for (int i = 2; i < n; i += 2) {
            evenSemaphore.acquire();
            IO.print(i);
            zeroSemaphore.release();
        }
    }
}

void main() throws InterruptedException {
    ZeroEvenOdd zeroEvenOdd = new ZeroEvenOdd(10);
    Thread zeroPrinter = new Thread(() -> {
        try {
            zeroEvenOdd.printZero();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    });
    Thread evenPrinter = new Thread(() -> {
        try {
            zeroEvenOdd.printEven();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    });
    Thread oddPrinter = new Thread(() -> {
        try {
            zeroEvenOdd.printOdd();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    });

    zeroPrinter.start();
    oddPrinter.start();
    evenPrinter.start();
}

