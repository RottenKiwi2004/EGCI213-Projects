package Project2;

import java.util.*;
import java.util.concurrent.*;

public class SellerThread extends Thread{
    private int maxDrop, days;
    private DeliveryShop shop;
    private ArrayList<DeliveryShop> allShops;
    private CyclicBarrier barrier;

    public SellerThread(String name, int maxDrop, int days, ArrayList<DeliveryShop> allShops){
        super(name);
        this.maxDrop = maxDrop;
        this.days = days;
        this.allShops = allShops;
    }

    protected void setBarrier(CyclicBarrier barrier){
        this.barrier = barrier;
    }

    synchronized private void randomShop(){
        this.shop = allShops.get(new Random().nextInt(0, allShops.size()));
    }

     private void drop(){
        int random = new Random().nextInt(1,maxDrop+1);
        randomShop();
        this.shop.received(random);
        System.out.printf("%15s  >>  drop %3d parcels at %-15s  shop\n",Thread.currentThread().getName(), random, shop.getThread().getName());
    }

    private int barrierWait(){
        try{
            return this.barrier.await();
        }
        catch (Exception e){ }
        return -1;
    }

    @Override
    public void run(){
        for(int i = 1; i <= days; i++) {
            int temp = barrierWait();
            if(temp == barrier.getParties()-1){
                System.out.printf("%15s  >>\n",Thread.currentThread().getName());
                System.out.printf("%15s  >> %s\n",Thread.currentThread().getName(), "=".repeat(15));
                System.out.printf("%15s  >>  Day  %d\n",Thread.currentThread().getName(), i);
            }
            barrierWait();
            drop();
            barrierWait();

            barrierWait(); //wait for delivery report
            barrierWait(); //wait for delivery deliver
        }
    }
}
