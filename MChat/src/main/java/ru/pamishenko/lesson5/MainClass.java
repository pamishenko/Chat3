package ru.pamishenko.lesson5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class MainClass {
    public static final int CARS_COUNT = 4;
    static Semaphore s = new Semaphore(4);

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        s.release();
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }



        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");

        CyclicBarrier cb = new CyclicBarrier(CARS_COUNT);
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();

        }
        cb.await();
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }
}

