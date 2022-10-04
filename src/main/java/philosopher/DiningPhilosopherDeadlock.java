package philosopher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
            look:
            while (true) {
                try {
                    thinking();
                    //循环拿到左叉子
                    while (!takeLeft(forks)) {
                        Thread.onSpinWait();
                    }

                    //这里睡眠100ms,是为了复现会出现死锁的情况:
                    //  等所有的哲学家都拿到了叉子,那么现在所有的叉子都被拿到了,
                    //  但是每个哲学家都在等待右边的叉子,所以会出现死锁
                    // Thread.sleep(100);

                    //循环拿到右叉子
                    //v1.0 解决死锁的方法:
                    // 在循环拿到右叉子的时候,如果循环了100次还是没有拿到,那么就把左叉子放下,然后重新进入饥饿状态
                    // 这样就可以保证每个哲学家都能拿到左叉子,然后再去拿右叉子
                    //这里睡眠100ms,是为了复现会出现活锁锁的情况:
                    //Thread.sleep(100);
                    int cycleIndex = 0;

                    while (!takeRight(forks)) {
                        Thread.onSpinWait();
                        //要跳转到外层while
                        cycleIndex++;
                        if (cycleIndex>10) {
                            //初始化
                            putLeft(forks);
                            // System.out.println("****************************************************************** putLeft   " + id);
                            setState(State.HUNGRY);
                            // Thread.sleep(1000);
                            continue look;
                        }
                    }

                    eating(forks);
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
