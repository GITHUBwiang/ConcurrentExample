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
                    //循环拿到左叉子
                    while (!takeLeft(forks)) {
                        Thread.onSpinWait();
                    }
                    System.out.println("takeLeft succeed  " + id);
                    //这里睡眠100ms,是为了复现会出现死锁的情况:
                    //  等所有的哲学家都拿到了叉子,那么现在所有的叉子都被拿到了,
                    //  但是每个哲学家都在等待右边的叉子,所以会出现死锁
                    Thread.sleep(100);
                    //循环拿到右叉子
                    while (!takeRight(forks)) {
                        Thread.onSpinWait();
                    }
                    System.out.println("takeRight succeed  " + id);
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
                r -> new Thread(r, "philosopher"));

        for (Philosopher philosopher : philosophers) {
            pool.submit(philosopher);
        }
    }

    public static void main(String[] args) {
        DiningPhilosopherDeadlock diningPhilosopherDeadlock = new DiningPhilosopherDeadlock();
        diningPhilosopherDeadlock.run();
    }
}
