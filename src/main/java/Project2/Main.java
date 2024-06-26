package Project2;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        Main mainApp = new Main();
        mainApp.task();
    }

    private ArrayList<Integer> argsFromLine(String line) {
        String[] cols = line.split(",");
        ArrayList<Integer> args = new ArrayList<>();
        for (int i = 1; i < cols.length; i++)
            args.add(Integer.parseInt(cols[i].trim()));
        return args;
    }

    private void task() {
        int days = 0, bikeNum = 0, bikeMaxLoad = 0, truckNum = 0, truckMaxLoad = 0, sellerNum = 0, maxDrop = 0, deliByBike = 0, deliByTruck = 0;
        boolean fileLoaded = false;
        String fileName = "config_1.txt";
        while (!fileLoaded) {
            try (Scanner fileScanner = new Scanner(new File("src/main/java/Project2/" + fileName))) {
                fileLoaded = true;
                ArrayList<Integer> fileValue = new ArrayList<>();
                while (fileScanner.hasNext())
                    fileValue.addAll(argsFromLine(fileScanner.nextLine()));

                days = fileValue.get(0);
                bikeNum = fileValue.get(1);
                bikeMaxLoad = fileValue.get(2);
                truckNum = fileValue.get(3);
                truckMaxLoad = fileValue.get(4);
                sellerNum = fileValue.get(5);
                maxDrop = fileValue.get(6);
                deliByBike = fileValue.get(7);
                deliByTruck = fileValue.get(8);
            } catch (Exception e) {
                Scanner keyboardScanner = new Scanner(System.in);
                System.err.println(e + "The system cannot find the file specified \nEnter correct file name =");
                fileName = keyboardScanner.next();
                keyboardScanner.close();
            }
        }



        CyclicBarrier barrier = new CyclicBarrier(deliByBike + deliByTruck + sellerNum +1);

        ArrayList<String> sellThreadName = new ArrayList<>();
        ArrayList<String> deliveryThreadName = new ArrayList<>();


        Fleet bike = new Fleet("Bike", bikeMaxLoad, bikeNum);
        Fleet truck = new Fleet("Truck", truckMaxLoad, truckNum);

        ArrayList<DeliveryShop> shopArrayList = new ArrayList<>();
        for (int i = 0; i < deliByBike; i++)
            shopArrayList.add(new DeliveryShop("BikeDelivery_" + i, bike, barrier, days));

        for (int i = 0; i < deliByTruck; i++)
            shopArrayList.add(new DeliveryShop("TruckDelivery_" + i, truck, barrier, days));

        ArrayList<SellerThread> sellerThreads = new ArrayList<>();
        for (int i = 0; i < sellerNum; i++) {
            sellerThreads.add(new SellerThread("Seller_" + i, maxDrop, days, shopArrayList));
        }
        for (SellerThread thread : sellerThreads){
            sellThreadName.add(thread.getName());
            thread.setBarrier(barrier);
        }

        for (DeliveryShop shop : shopArrayList){
            deliveryThreadName.add(shop.getThread().getName());
        }

        String sellName = String.join(", ", sellThreadName);
        String deliveryName = String.join(", ", deliveryThreadName);

        System.out.printf("%15s  >>  %s Parameters %s\n",Thread.currentThread().getName(), "=".repeat(15), "=".repeat(15));
        System.out.printf("%15s  >>  days of simulation = %2d\n",Thread.currentThread().getName(), days);
        System.out.printf("%15s  >>  %-5s, total %-6s = %3d, max_load = %4d parcels, min_load = %4d parcels\n",
                Thread.currentThread().getName(), bike.getVehicle(), bike.getVehicle().toLowerCase()+"s",
                bike.getTotalVehicle(), bike.getMaxLoad(), bike.getMinLoad());
        System.out.printf("%15s  >>  %-5s, total %-6s = %3d, max_load = %4d parcels, min_load = %4d parcels\n",
                Thread.currentThread().getName(), truck.getVehicle(), truck.getVehicle().toLowerCase()+"s",
                truck.getTotalVehicle(), truck.getMaxLoad(), truck.getMinLoad());
        System.out.printf("%15s  >>  SellerThreads = [%s]\n",Thread.currentThread().getName() ,sellName);
        System.out.printf("%15s  >>  max parcel drop = %d\n",Thread.currentThread().getName() ,maxDrop);
        System.out.printf("%15s  >>  DeliveryThreads = [%s]\n",Thread.currentThread().getName() ,deliveryName);

        for (DeliveryShop shop : shopArrayList)
            shop.getThread().start();

        for (SellerThread thread : sellerThreads)
            thread.start();

        for(int i = 1; i <= days; i++) {
            int temp = -1;
            try {
                temp = barrier.await();
            } catch (Exception e) { }

            if (temp == barrier.getParties() - 1){
                System.out.printf("%15s  >>\n",Thread.currentThread().getName());
                System.out.printf("%15s  >> %s\n",Thread.currentThread().getName(), "=".repeat(15));
                System.out.printf("%15s  >>  Day  %d\n", Thread.currentThread().getName(), i);
            }
            try { //wait for day printing
                barrier.await();
            } catch (Exception e) { }

            try { //wait for seller drop
                barrier.await();
            } catch (Exception e) { }

            try { //wait for delivery report
                barrier.await();
            } catch (Exception e) { }

            try { //wait for delivery deliver
                barrier.await();
            } catch (Exception e) { }
        }

        try{
            for (DeliveryShop shop : shopArrayList)
                shop.getThread().join();
            for (SellerThread thread : sellerThreads)
                thread.join();
        }catch (Exception e){
            System.err.println(e);
        }

        Collections.sort(shopArrayList);
        System.out.printf("%15s >> \n",Thread.currentThread().getName());
        System.out.printf("%15s >> %s\n",Thread.currentThread().getName(),"=".repeat(15));
        System.out.printf("%15s >> summary\n",Thread.currentThread().getName());
        for(DeliveryShop shop: shopArrayList)
           shop.reportSummary();
    }
}

