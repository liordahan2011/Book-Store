package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReferenceArray;
import static java.lang.Thread.sleep;

/** This is the Main class of the application.
 *
 <p>
 *
 * parsing the input file, creating the different instances of the objects, and running the system.
 *
 <p>
 *
 * outputs serialized objects
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        HashMap<Integer, Customer> customersOutput = new HashMap<>();

        try {
            Gson gson = new Gson();
            JsonObject json = (JsonObject) new JsonParser().parse(new FileReader(args[0]));
            JsonArray array = json.get("initialInventory").getAsJsonArray();
            Inventory.getInstance().load(createInv(array));
            array = json.getAsJsonArray("initialResources").getAsJsonArray();
            ResourcesHolder.getInstance().load(createVehicles(array));
            JsonObject services = json.get("services").getAsJsonObject();
            int counter = services.get("selling").getAsInt() + services.get("inventoryService").getAsInt() +
                    services.get("logistics").getAsInt() + services.get("resourcesService").getAsInt() + services.get("customers").getAsJsonArray().size();
            ExecutorService executor = Executors.newFixedThreadPool(counter + 1);
            for (int j = 0; j < services.get("selling").getAsInt(); j++) {
                executor.execute(new Thread(new SellingService("sellingService" + j)));
            }
            for (int i = 0; i < services.get("inventoryService").getAsInt(); i++) {
                executor.execute(new InventoryService("inventoryService" + i));
            }
            for (int k = 0; k < services.get("logistics").getAsInt(); k++) {
                executor.execute(new LogisticsService("LogisticServices" + k));
            }
            for (int m = 0; m < services.get("resourcesService").getAsInt(); m++) {
                executor.execute(new ResourceService("ResourceService" + m));
            }
            createAPI(services.get("customers").getAsJsonArray(), executor, customersOutput);

            while (InitializeManager.getInstance().getCounter() < counter) {
                sleep(1000);
            }
            JsonObject time = services.get("time").getAsJsonObject();
            executor.execute(new TimeService(time.get("speed").getAsInt(), time.get("duration").getAsInt()));
            executor.shutdown();
            while (!executor.awaitTermination(2, TimeUnit.SECONDS));


        } catch (FileNotFoundException | InterruptedException e) {
        }
        printCustomers(customersOutput, args[1]);
        Inventory.getInstance().printInventoryToFile(args[2]);
        MoneyRegister.getInstance().printOrderReceipts(args[3]);
        printMoneyReg(args[4]);
    }


    /**
     * converts the books given in the input to array of books to load the inventory
     *
     * @param array represents the books that should be in the inventory due to input file
     <p>
     * @return {@link BookInventoryInfo[]} array of books
     *
     */
    public static BookInventoryInfo[] createInv(JsonArray array) {
        BookInventoryInfo[] books = new BookInventoryInfo[array.size()];
        for (int i = 0; i < array.size(); i++) {
            String bookTitle = array.get(i).getAsJsonObject().get("bookTitle").getAsString();
            int amount = array.get(i).getAsJsonObject().get("amount").getAsInt();
            int price = array.get(i).getAsJsonObject().get("price").getAsInt();
            books[i] = new BookInventoryInfo(bookTitle, amount, price);
        }
        return books;
    }
    /**
     * converts the vehicles given in the input to array of vehicle to load in the inventory
     <p>
     * @param array represents the vehicles that should be in the Resource holder due to input file
     <p>
     * @return {@link DeliveryVehicle[]} array of vehicles
     *
     */
    public static DeliveryVehicle[] createVehicles(JsonArray array) {
        JsonArray jsonVehicle = array.get(0).getAsJsonObject().get("vehicles").getAsJsonArray();
        DeliveryVehicle[] vehicles = new DeliveryVehicle[jsonVehicle.size()];
        for (int i = 0; i < jsonVehicle.size(); i++) {
            int license = jsonVehicle.get(i).getAsJsonObject().get("license").getAsInt();
            int speed = jsonVehicle.get(i).getAsJsonObject().get("speed").getAsInt();
            vehicles[i] = new DeliveryVehicle(license, speed);
        }
        return vehicles;
    }

    /**
     * gets the details of the customer and its orders and manages the orders
     *
     * @param array given as input and includes customer's details, order schedule
     * @param executor service that manages the orders
     * @param hash map which needs to be filled with the orders made by the customer (used as part of the output)
     */
    public static void createAPI(JsonArray array, ExecutorService executor, HashMap<Integer, Customer> hash) {
        for (int i = 0; i < array.size(); i++) {
            int id = array.get(i).getAsJsonObject().get("id").getAsInt();
            String name = array.get(i).getAsJsonObject().get("name").getAsString();
            String address = array.get(i).getAsJsonObject().get("address").getAsString();
            int distance = array.get(i).getAsJsonObject().get("distance").getAsInt();
            int creditCard = array.get(i).getAsJsonObject().get("creditCard").getAsJsonObject().get("number").getAsInt();
            int creditAmount = array.get(i).getAsJsonObject().get("creditCard").getAsJsonObject().get("amount").getAsInt();
            Customer customer = new Customer(name, id, address, distance, creditCard, creditAmount);
            hash.put(id, customer);
            JsonArray order = array.get(i).getAsJsonObject().get("orderSchedule").getAsJsonArray();
            AtomicReferenceArray<Pair> orderSchedule = new AtomicReferenceArray<>(order.size());
            for (int j = 0; j < order.size(); j++) {
                String bookTitle = order.get(j).getAsJsonObject().get("bookTitle").getAsString();
                int tick = order.get(j).getAsJsonObject().get("tick").getAsInt();
                orderSchedule.set(j, new Pair(bookTitle, tick));
            }
            executor.execute(new APIService("Api" + i, customer, orderSchedule));
        }
    }

    /**
     * prints the customers to output file
     *
     * @param customers map of all of the customers of the bookstore
     * @param filename output file
     */
    public static void printCustomers(HashMap<Integer, Customer> customers, String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(customers);
            oos.close();
            fos.close();
        } catch (IOException ioe) {
        }
    }
    /**
     * prints the money register to output file
     *
     * @param filename output file
     */
    public static void printMoneyReg(String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(MoneyRegister.getInstance());
            oos.close();
            fos.close();
        } catch (IOException ioe) {
        }

    }
}


