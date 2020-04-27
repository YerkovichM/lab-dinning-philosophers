package com.company;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class Main {

    private static final Object KEY = new Object();
    static ConcurrentHashMap<Integer, String> forks = new ConcurrentHashMap<>();

    static {
        IntStream.rangeClosed(1,5)
                .forEach(i -> forks.put(i, "O"));
        forks.put(1, "O");
        forks.put(2, "O");
        forks.put(3, "O");
        forks.put(4, "O");
        forks.put(5, "O");
    }

    public static void main(String[] args) {
        IntStream.rangeClosed(1,5)
                .forEach(i -> forks.put(i, "O"));

        List<Philosopher> philosophers = List.of(
                new Philosopher("I", 1, 5),
                new Philosopher("II", 1, 2),
                new Philosopher("III", 2, 3),
                new Philosopher("IV", 3, 4),
                new Philosopher("V", 4, 5)
        );

        philosophers.forEach(Thread::start);

        while(true){
            System.out.println("------------");
            philosophers.forEach(ph -> System.out.println(String.format("%3s: %d", ph.name, ph.eatingTime)));
            System.out.println("------------");
            sleep(5000);
        }
    }

    static boolean occupyFork(int fork, String name){
        synchronized (KEY){
            if (Main.forks.get(fork).equals("O")) {
                Main.forks.put(fork, name);
                return true;
            }
            return false;
        }
    }

    static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class Philosopher extends Thread{
    String name;
    int smallerFork;
    int biggerFork;
    long eatingTime;

    public Philosopher(String name, int smallerFork, int biggerFork) {
        this.name = name;
        this.smallerFork = smallerFork;
        this.biggerFork = biggerFork;
    }

    @Override
    public void run(){
        while(true){
            takeFork(smallerFork);
            takeFork(biggerFork);
            Main.sleep(1000);
            eatingTime += 1;
            releaseForks();
            Main.sleep(2000);
        }
    }

    void takeFork(int fork){
        while(true) {
            if (Main.occupyFork(fork, name)) {
                return;
            }
            Main.sleep(50);
        }
    }

    void releaseForks(){
        Main.forks.put(smallerFork,"O");
        Main.forks.put(biggerFork, "O");
    }
}
