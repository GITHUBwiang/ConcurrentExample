package philosopher;

import java.util.concurrent.*;

public class DiningPhilosopherDeadlock {

    Philosopher[] philosophers = new Philosopher[5];
    int[] forks = new int[philosophers.length];

    public DiningPhilosopherDeadlock() {
        for (int i = 0; i < philosophers.length; i++) {
            philosophers[i] = new Phi(i + 1);
        }
    }


    class Phi extends Philosopher {
        public Phi(int id) {
            super(id);
        }

        @Override
        protected synchronized boolean takeLeft(int[] forks) {
            return super.takeLeft(forks);
        }

        @Override
        public synchronized boolean takeRight(int[] forks) {
            return super.takeRight(forks);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    thinking();
                    // System.out.println("thinking " + id);
                    while (!takeLeft(forks)) {
                        Thread.onSpinWait();
                    }
                    Thread.sleep(100);
                    /* System.out.println("takeLeft succeed  " + id);*/
                    int cycleIndex = 0;
                    while (!takeRight(forks)) {
                        cycleIndex++;
                        if (cycleIndex == 100) {
                            cycleIndex = 0;
                            putLeft(forks);
                            setState(State.HUNGRY);
                        }
                        Thread.onSpinWait();
                    }

                    eating();
                    putLeft(forks);
                    putRight(forks);
                    finished();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

    public void run() {
        ExecutorService pool = new ThreadPoolExecutor(5,
                5,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "philosopher");
                    }
                });

        for (Philosopher philosopher : philosophers) {
            pool.submit(philosopher);
        }
    }

    public static void main(String[] args) {
        DiningPhilosopherDeadlock diningPhilosopherDeadlock = new DiningPhilosopherDeadlock();
        diningPhilosopherDeadlock.run();
    }
}
