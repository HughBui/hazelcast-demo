import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class Seller1 {

  public static void main(String[] args) throws Exception {
    HazelcastInstance hz = getHazelcastInstance("localhost:5701");

    // Create a Distributed Map in the cluster
    System.out.println(">>> Initialize my-distributed-inventory");
    IMap<String, Integer> inventory = hz.getMap("my-distributed-inventory");

    //Standard Put and Get
    System.out.println(">>> Load inventory");
    inventory.put("Books", 100);

    for (int i = 0; i < 12; i++) {
      inventory.put("Books", inventory.get("Books") - 5);
      System.out.print(">>> Selling 5 books - ");
      System.out.println(inventory.get("Books") + " books left");
      Thread.sleep(1000);
    }

    System.out.println(">>> Inventory: " + mapToString(inventory));
    hz.shutdown();
  }

  private static HazelcastInstance getHazelcastInstance(String address) {
    ClientConfig clientConfig = new ClientConfig();
    clientConfig.getNetworkConfig().addAddress(address);

    // Start the client and connect to the cluster
    HazelcastInstance hz = HazelcastClient.newHazelcastClient(clientConfig);
    return hz;
  }

  private static String mapToString(Map map) {
    return (String) map.keySet().stream()
      .map(key -> key + "=" + map.get(key))
      .collect(Collectors.joining(", ", "{", "}"));
  }
}
