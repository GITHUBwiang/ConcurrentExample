package philosopher;

import java.util.concurrent.atomic.AtomicInteger;

public class Philosopher implements Runnable {
    int id;
    State state;

    int count = 0;
    static AtomicInteger total = new AtomicInteger(0);
    static  long startTime = System.currentTimeMillis();

    public Philosopher(int id) {
        this.id = id;
        state = State.THINKING;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void thinking() throws InterruptedException {
        if (state.equals(State.THINKING)) {
            Thread.sleep((long) (Math.random() * 100));
        }
    }

    public void eating() throws InterruptedException {
        if (state.equals(State.HUNGRY)) {
            setState(State.EATING);
            Thread.sleep((long) (Math.random() * 100));
        }
    }

    // 1 2 3 4 5 1 ID
    //0 1 2 3 4 0 1fork

    public Integer left() {
        return id - 1;
    }

    public int right() {
        return (id) % 5;
    }


    /**
     * 叉子数组
     * @param forks 所有的叉子的数组【0，1，2，0，3】 0表示无人占用，其余表示占用人id
     * @param fork 要拿起的叉子
     * @return
     */
    private boolean _take(int[] forks, int fork) {
        //没人使用
        if (forks[fork] == 0) {
            //锁？
            forks[fork] = id;
            return true;
        }
        return false;
    }


    protected boolean takeLeft(int[] forks) {
        return _take(forks, left());
    }

    public boolean takeRight(int[] forks) {
        return _take(forks, right());
    }

    /**
     * 放下叉子
     * @param forks 所有的叉子的数组【0，1，2，0，3】 0表示无人占用，其余表示占用人id
     */
    public void putLeft(int[] forks) {
        if (forks[left()] == id) {
            forks[left()] = 0;
        }
    }

    public void putRight(int[] forks) {
        if (forks[right()] == id) {
            forks[right()] = 0;
        }
    }

    public boolean checkLeft(int[] forks) {
        return forks[left()] == 0;
    }

    public boolean checkRight(int[] forks) {
        return forks[right()] == 0;
    }

    public void finished() {
        int t = total.getAndIncrement();
        count++;
        double speed = (t * 1000.0) / (System.currentTimeMillis() - startTime);
        setState(State.THINKING);
        System.out.println("Philosopher " + id + " finished " + count + " times, speed: " + speed + " times/s");
    }
    @Override
    public void run() {
        throw new UnsupportedOperationException();
    }
}
