static class PrintFizzBuzzNumbers {
    int n;
    Semaphore numbersSem;
    Semaphore fizzSem;
    Semaphore buzzSem;
    Semaphore fizzBUzzSem;

    PrintFizzBuzzNumbers(int n) {
        this.n = n;
        numbersSem = new Semaphore(1);
        fizzSem = new Semaphore(0);
        buzzSem = new Semaphore(0);
        fizzBUzzSem = new Semaphore(0);
    }

    void printNumbers() throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            numbersSem.acquire();
            if (i % 3 == 0 && i % 5 == 0) {
                fizzBUzzSem.release();
            } else if (i % 3 == 0) {
                fizzSem.release();
            } else if (i % 5 == 0) {
                buzzSem.release();
            } else {
                IO.print(i + " ");
                numbersSem.release();
            }
        }
    }

    void printFizz() throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            if (i % 3 == 0 && i % 5 != 0) {
                fizzSem.acquire();
                IO.print("Fizz" + " ");
                numbersSem.release();
            }
        }
    }

    void printBuzz() throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            if (i % 3 != 0 && i % 5 == 0) {
                buzzSem.acquire();
                IO.print("Buzz" + " ");
                numbersSem.release();
            }
        }
    }

    void printFizzBUzz() throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            if (i % 3 == 0 && i % 5 == 0) {
                fizzBUzzSem.acquire();
                IO.print("FizzBuzz" + " ");
                numbersSem.release();
            }
        }
    }
}

void main() {
    PrintFizzBuzzNumbers printFizzBuzzNumbers = new PrintFizzBuzzNumbers(31);
    Thread numsPrinter = new Thread(() -> {
        try {
            printFizzBuzzNumbers.printNumbers();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    });

    Thread fizzPrinter = new Thread(() -> {
        try {
            printFizzBuzzNumbers.printFizz();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    });

    Thread buzzPrinter = new Thread(() -> {
        try {
            printFizzBuzzNumbers.printBuzz();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    });

    Thread fizzBuzzPrinter = new Thread(() -> {
        try {
            printFizzBuzzNumbers.printFizzBUzz();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    });

    numsPrinter.start();
    fizzPrinter.start();
    buzzPrinter.start();
    fizzBuzzPrinter.start();
}
