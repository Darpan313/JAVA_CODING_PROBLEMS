package ImplementProblems.JobScheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Main {
    public static void main(String[] args) {
        // input: jobs = [["J1", "0023", "45"], ["J2", "0025", "10"], ["J3", "0100", "60"], ["J4", "0300", "10"]]
        // output: [["J1", "M1"], ["J2", "M2"], ["J3", "M2"], ["J4", "M1"]]

        // Time : 00:00 -> 01:00 -> ... -> 12:00 -> 13:00 -> ... 23:59 -> 00:00
        // Minute: 0 ->    60 ->        -> 720 ->   780 ->       1439 ->  1440

        List<List<String>> jobs = new ArrayList<>();
        jobs.add(List.of("J1", "0023", "45"));
        jobs.add(List.of("J2", "0025", "10"));
        jobs.add(List.of("J3", "0100", "60"));
        jobs.add(List.of("J4", "0300", "10"));

        List<List<String>> res = jobScheduler(jobs);
        System.out.println(res);
        // ----
        jobs = new ArrayList<>();
        jobs.add(List.of("J1","0100","30"));
        jobs.add(List.of("J2","0100","20"));
        jobs.add(List.of("J3","0100","10"));

        res = jobScheduler(jobs);
        System.out.println(res);
        // ---
        jobs = new ArrayList<>();
        jobs.add(List.of("J1","0000","60"));
        jobs.add(List.of("J2","0030","20"));
        jobs.add(List.of("J3","0050","10"));
        jobs.add(List.of("J5","0055","30"));
        jobs.add(List.of("J4","0060","20"));

        res = jobScheduler(jobs);
        System.out.println(res);
        // ---
        jobs = new ArrayList<>();
        jobs.add(List.of("J1","0100","50"));
        jobs.add(List.of("J2","0105","30"));
        jobs.add(List.of("J3","0110","20"));
        jobs.add(List.of("J4","0120","30"));

        res = jobScheduler(jobs);
        System.out.println(res);
    }

    public static List<List<String>> jobScheduler(List<List<String>> jobs) {
        List<List<String>> res = new ArrayList<>();

        PriorityQueue<Integer> availableMachines = new PriorityQueue<>();
        PriorityQueue<Machine> busyMachines = new PriorityQueue<>(Comparator.comparingInt(Machine::endTime)
                .thenComparing(Machine::id));

        int machineId = 1;
        if(!jobs.isEmpty()) availableMachines.add(machineId);

        for(List<String> job :jobs) {
            String jobId = job.get(0);
            int jobStartTime = getJobStartTime(job.get(1));
            int jobFinishTime = jobStartTime + Integer.parseInt(job.get(2));

            while(!busyMachines.isEmpty() && busyMachines.peek().endTime <= jobStartTime) {
                availableMachines.add(busyMachines.poll().id);
            }

            Integer machineToUse = availableMachines.poll();
            if(machineToUse == null) {
                // Post decrement will not work here since evaluated after assignment
                machineToUse = ++machineId;
            }
            busyMachines.add(new Machine(machineToUse, jobFinishTime));
            res.add(List.of(jobId, "M"+machineToUse));
        }
        return res;
    }

    public static int getJobStartTime(String hhmm) {
        int HH = Integer.parseInt(hhmm.substring(0,2));
        int MM = Integer.parseInt(hhmm.substring(2));

        return HH*60 + MM;
    }

    public record Machine(int id, int endTime) {}

//    static class Pair<K,V> {
//        private final K key;
//        private final V value;
//
//        public Pair(K key, V value) {
//            this.key = key;
//            this.value = value;
//        }
//
//        public K getKey() {
//            return key;
//        }
//
//        public V getValue() {
//            return value;
//        }
//    }
}
