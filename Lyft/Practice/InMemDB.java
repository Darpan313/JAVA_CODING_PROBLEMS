package Lyft.Practice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class InMemDB {
    private Map<String, Object> store;
    private Map<Object, Integer> valueCounts;
    private Stack<Map<String, Object>> transactions;

    public InMemDB() {
        store = new HashMap<>();
        valueCounts = new HashMap<>();
        transactions = new Stack<>();
    }

    // record only once and record if key has null value as well
    public void recordOldState(String key) {
        if(!transactions.isEmpty()) {
            Map<String, Object> currentTx = transactions.peek();
            if(!currentTx.containsKey(key)) {
                currentTx.put(key, store.get(key));
            }
        }
    }

    public void decrementCount(Object value) {
        if(!valueCounts.containsKey(value)) return;

        int count = valueCounts.get(value);

        if(count == 1) {
            valueCounts.remove(value);
        }
        else {
            valueCounts.put(value, count - 1);
        }
    }

    public void incrementCount(Object value) {
        valueCounts.put(value, valueCounts.getOrDefault(value, 0) + 1);
    }

    public void begin() {
        transactions.push(new HashMap<>());
    }

    public void set(String key, Object value) {
        recordOldState(key);

        if(store.containsKey(key)) {
            Object oldValue = store.get(key);
            decrementCount(oldValue);
        }
        store.put(key, value);
        incrementCount(value);
    }

    public Object get(String key) {
        return store.getOrDefault(key, "NULL");
    }

    public void unset(String key) {
        if(!store.containsKey(key)) return;

        recordOldState(key);

        decrementCount(store.get(key));
        store.remove(key);
    }

    public int numEqualTo(Object value) {
        return valueCounts.getOrDefault(value, 0);
    }

    public String commit() {
        if(transactions.isEmpty()) {
            return "NO TRANSACTION";
        }

        Map<String, Object> currentTx = transactions.pop();

        if(!transactions.isEmpty()) {
            Map<String, Object> parentTx = transactions.peek();
            for(Map.Entry<String, Object> entry : currentTx.entrySet()) {
                parentTx.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
        return null;
    }

    public String rollback() {
        if(transactions.isEmpty()) return "NO TRANSACTION";

        Map<String, Object> currentTx = transactions.pop();
        for(Map.Entry<String, Object> entry : currentTx.entrySet()) {
            String key = entry.getKey();
            Object oldValue = entry.getValue();
            Object currValue = store.get(key);

            if(currValue != null) {
                decrementCount(currValue);
            }
            if(oldValue != null) {
                store.put(key, oldValue);
                incrementCount(oldValue);
            }
            else {
                store.remove(key);
            }
        }
        return null;
    }


    static void runCommands(List<String> commands) {
        InMemDB db = new InMemDB();
        for(String line : commands) {
            String[] parts = line.split("\\s");
            String command = parts[0];
            if(command.equals("END")) break;

            switch (command) {
                case "BEGIN":
                    db.begin();
                    break;
                case "SET":
                    db.set(parts[1], parts[2]);
                    break;
                case "GET":
                    System.out.println(db.get(parts[1]));
                    break;
                case "UNSET":
                    db.unset(parts[1]);
                    break;
                case "NUMEQUALTO":
                    System.out.println(db.numEqualTo(parts[1]));
                    break;
                case "COMMIT" :
                    String cmtRes = db.commit();
                    if(cmtRes!=null) System.out.println(cmtRes);
                    break;
                case "ROLLBACK":
                    String rbRes = db.rollback();
                    if(rbRes!=null) System.out.println(rbRes);
                    break;
                default:
                    System.out.println("UNKNOWN COMMAND");
            }
        }
    }
    public static void main(String[] args) {
        List<String> commands = List.of(
                "SET A 10",
                "BEGIN",
                "SET A 11",
                "BEGIN",
                "SET B 12",
                "COMMIT",
                "ROLLBACK",
                "GET B",
                "GET A"
        );
        runCommands(commands);
    }
}
