package Lyft.TransactionalInMemoryKeyValueStore;

import java.util.*;

public class InMemDb {
    static class KeyValueStore {
        Map<String, String> store = new HashMap<>();
        // given value count number of keys with same value;
        Map<String, Integer> valueCount = new HashMap<>();
        Stack<Map<String, String>> transactions = new Stack<>();

        public void decrementCount(String value) {
            int count = valueCount.get(value);
            if(count == 1) {
                valueCount.remove(value);
            }
            else {
                valueCount.put(value, count-1);
            }
        }

        public void incrementCount(String value) {
            valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);
        }

        public void set(String key, String value) {
            recordCurrentStateOfKey(key);
            String oldValue = store.get(key);

            if(oldValue!=null) {
                decrementCount(oldValue);
            }

            store.put(key, value);
            incrementCount(value);
        }

        public void unset(String key) {
            if(!store.containsKey(key)) return;

            recordCurrentStateOfKey(key);

            String oldValue = store.get(key);
            decrementCount(oldValue);
            store.remove(key);
        }

        public String get(String key) {
            return store.getOrDefault(key, "NULL");
        }

        public int numEqualTo(String value) {
            return valueCount.getOrDefault(value, 0);
        }

        public void begin() {
            transactions.push(new HashMap<>());
        }

        public void recordCurrentStateOfKey(String key) {

            if(!transactions.isEmpty()) {
                Map<String, String> currentTx = transactions.peek();
                // Only record the FIRST time we touch this key in current transaction
                if(!currentTx.containsKey(key)) {
                    currentTx.put(key, store.get(key));
                }
            }
            // empty implies no running transaction
        }

        public String rollback() {
            if(transactions.isEmpty()) return "NO TRANSACTION";

            Map<String, String> undoLogs = transactions.pop();

            for(Map.Entry<String, String> entry : undoLogs.entrySet()) {
                String key = entry.getKey();
                String oldValue = entry.getValue();
                String currValue = store.get(key);

                if(currValue != null) {
                    decrementCount(currValue);
                }

                if(oldValue == null) {
                    store.remove(key);
                }
                else {
                    store.put(key, oldValue);
                    incrementCount(oldValue);
                }
            }
            return null;
        }

        public String commit() {
            if(transactions.isEmpty()) return "NO TRANSACTION";

            transactions.pop();
            return null;
        }
    }

    static void executeCommand(List<String> commands) {
        KeyValueStore db = new KeyValueStore();

        for(String line : commands) {
            String[] parts = line.split("\\s+");
            String cmd = parts[0];

            // Can this be end of nested transaction
            if(cmd.equals("END")) break;

            switch(cmd) {
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
                case "BEGIN":
                    db.begin();
                    break;
                case "ROLLBACK":
                    String rbResult = db.rollback();
                    if(rbResult != null) System.out.println(rbResult);
                    break;
                case "COMMIT":
                    String cmtResult = db.commit();
                    if(cmtResult != null) System.out.println(cmtResult);
                    break;
                default:
                    System.out.println("UNKNOWN COMMAND");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("---Test Case-1---");

        executeCommand(Arrays.asList(
                "SET a 10",
                "GET a",
                "UNSET a",
                "GET a",
                "END"
        ));

        System.out.println("---Test Case-2---");
        executeCommand(Arrays.asList(
                "SET a 10",
                "BEGIN",
                "NUMEQUALTO 10",
                "BEGIN",
                "UNSET a",
                "NUMEQUALTO 10",
                "ROLLBACK",
                "NUMEQUALTO 10",
                "COMMIT",
                "END"
        ));

//        Scanner sc = new Scanner(System.in);
//        List<String> inputCommands = new ArrayList<>();
//        while (sc.hasNextLine()) {
//            inputCommands.add(sc.nextLine());
//        }
//        executeCommand(inputCommands);
//        sc.close();
    }
}
