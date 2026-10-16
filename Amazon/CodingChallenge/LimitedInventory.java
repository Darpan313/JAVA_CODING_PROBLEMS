package Amazon.CodingChallenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/*
    Allocate Limited Inventory
    During an Amazon flash sale, customers submit requests for a limited-inventory product. Each request is represented by four integers:

    [customerId, quantity, bidAmount, timestamp]

    where:

    customerId is the customer ID; each customer submits at most one request.
    quantity is the requested number of items, with quantity >= 1.
    bidAmount is the customer's bid.
    timestamp is the request submission time.
    Given totalInventory, allocate items according to the following rules:

    Requests with higher bids have higher priority.
    Among customers with the same bidAmount, allocate in round-robin order by increasing timestamp.
    In every round, each customer whose request is not yet fulfilled receives at most one item.
    Stop when inventory is exhausted or every request is fulfilled.
    If timestamps are equal, preserve the original input order.
    Return the IDs of customers who receive zero items, in their original input order.

    Input Format
    First line: two integers, n totalInventory.
    Next n lines: four integers per line: customerId quantity bidAmount timestamp.
    Output Format
    Print the list of customer IDs that received no items, for example [4, 7]. Print [] if every customer receives at least one item.

    Example
    Input:

    4 18
    1 5 5 0
    2 7 8 1
    3 7 5 1
    4 10 3 3
    Output:

    [4]
    Constraints
    1 <= n <= 100000
    0 <= totalInventory <= 10^9
    1 <= quantity, bidAmount <= 10^9
    0 <= timestamp <= 10^9
    Example
    Input
    4 18
    1 5 5 0
    2 7 8 1
    3 7 5 1
    4 10 3 3
    Output
    [4]
 */
public class LimitedInventory {
    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int N = sc.nextInt();
//        int inventory = sc.nextInt();
        // customerId, quantity, bidAmount, timestamp
//        int[][] orders = new int[N][4];
//        for(int i=0; i<N; i++) {
//            orders[i][0] = sc.nextInt();
//            orders[i][1] = sc.nextInt();
//            orders[i][2] = sc.nextInt();
//            orders[i][3] = sc.nextInt();
//        }

        int N = 6;
        int inventory = 4;
        int[][] orders = {{1, 3, 10, 1}, {2, 1, 10, 2}, {3, 2, 10, 2}, {4, 5, 10, 3}, {5, 2, 15, 10}, {6, 10, 5, 0}};
        List<Integer> customerList = getUnservedCustomers(orders, inventory, N);

        System.out.println(customerList);
    }

    private static List<Integer> getUnservedCustomers(int[][] orders, int inventory, int customers) {
        List<Integer> res = new ArrayList<>();
        Arrays.sort(orders, Comparator.comparingInt((int[] o) -> o[2]).reversed().thenComparingInt(o -> o[3]));
        int[] prefixQuantity = new int[customers + 1];
        prefixQuantity[0] = 0;
        for (int i = 1; i <= customers; i++) {
            prefixQuantity[i] = prefixQuantity[i - 1] + orders[i - 1][1];
        }
        int left = 0;
        while (left < orders.length) {
            if (inventory == 0) {
                res.add(orders[left][0]);
                left++;
                continue;
            }
            int right = left + 1;
            while (right < customers && orders[right][2] == orders[left][2]) {
                right++;
            }
            int totalCustomerWithSameBid = right - left;
            if (totalCustomerWithSameBid == 1) {
                inventory -= (orders[left][1]);
            } else {
                if (inventory < totalCustomerWithSameBid) {
                    for (int k = left + inventory; k < right; k++) {
                        res.add(orders[k][0]);
                    }
                    inventory = 0;
                } else {
                    int totalQuantityInCurrWin = prefixQuantity[right] - prefixQuantity[left];
                    if (totalQuantityInCurrWin < inventory) {
                        inventory -= (totalQuantityInCurrWin);
                        if(inventory<0) {
                            inventory = 0;
                        }
                    } else {
                        inventory = 0;
                    }
                }
            }
            left = right;
        }
        return res;
    }
}
