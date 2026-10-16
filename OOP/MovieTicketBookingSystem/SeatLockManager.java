package OOP.MovieTicketBookingSystem;

import OOP.MovieTicketBookingSystem.entities.Seat;
import OOP.MovieTicketBookingSystem.entities.Show;
import OOP.MovieTicketBookingSystem.enums.SeatStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SeatLockManager {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<Show, Map<Seat, String>> lockedSeats = new ConcurrentHashMap<>();
    private final Map<Show, Map<Seat, ScheduledFuture<?>>> expiryTasks = new ConcurrentHashMap<>();

    private static final long LOCK_TIMEOUT_MS = 500;
    public boolean lockSeats(Show show, List<Seat> seats, String userId) {
        synchronized (show.getLock()) {
            for (Seat seat : seats) {
                if(seat.getStatus() != SeatStatus.AVAILABLE) {
                    System.out.println("Seat " + seat.getId() + " is not available.");
                    return false;
                }
            }

            for(Seat seat : seats) {
                seat.setStatus(SeatStatus.LOCKED);
            }

            lockedSeats.computeIfAbsent(show, k -> new HashMap<>());
            for(Seat seat : seats) {
                lockedSeats.get(show).put(seat, userId);
            }

            ScheduledFuture<?> task = scheduler.schedule(() ->
                    unlockSeats(show, seats, userId), LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            expiryTasks.computeIfAbsent(show, k -> new HashMap<>());
            for(Seat seat : seats) {
                expiryTasks.get(show).put(seat, task);
            }

            System.out.println("Locked seats: " + seats.stream().map(Seat::getId)
                    .collect(Collectors.toList()) + " for user " + userId);
            return true;
        }
    }

    // Atomically confirm the booking: succeed only if the user still holds the lock on every seat,
    // then mark those seats BOOKED. This closes the window where a lock expired during a slow payment.
    public boolean confirmSeats(Show show, List<Seat> seats, String userId) {
        synchronized (show.getLock()) {
            Map<Seat, String> showLocks = lockedSeats.get(show);
            if(showLocks == null) {
                return false;
            }

            for (Seat seat : seats) {
                if(!userId.equals(showLocks.get(seat))) {
                    return false; // lock expired or was taken by another user
                }
            }

            for(Seat seat : seats) {
                seat.setStatus(SeatStatus.BOOKED);
                showLocks.remove(seat);
                cancelExpiry(show, seat); // the seat is booked, so the timeout is no longer needed
            }

            if(showLocks.isEmpty()) {
                lockedSeats.remove(show);
            }
            return true;
        }
    }

    public void unlockSeats(Show show, List<Seat> seats, String userId) {
        synchronized (show.getLock()) {
            Map<Seat, String> showLocks = lockedSeats.get(show);
            if(showLocks == null) {
                return;
            }

            for(Seat seat : seats) {
                // Only the user who currently holds the lock can release it
                if(userId.equals(showLocks.get(seat))) {
                    showLocks.remove(seat);
                     cancelExpiry(show, seat);
                    // Revert only a seat that is still LOCKED; a BOOKED seat is left untouched
                    if (seat.getStatus() == SeatStatus.LOCKED) {
                        seat.setStatus(SeatStatus.AVAILABLE);
                        System.out.println("Released lock on seat: " + seat.getId());
                    }
                }
            }
            if (showLocks.isEmpty()) {
                lockedSeats.remove(show);
            }
        }
    }

    // Cancel and forget the pending expiry task for a seat once its lock is resolved.
    private void cancelExpiry(Show show, Seat seat) {
        Map<Seat, ScheduledFuture<?>> tasks = expiryTasks.get(show);
        if(tasks != null) {
            ScheduledFuture<?> task = tasks.remove(seat);
            if(task != null) {
                task.cancel(false);
            }
            if(tasks.isEmpty()) {
                expiryTasks.remove(show);
            }
        }
    }

    public void shutdown() {
        System.out.println("Shutting down SeatLockManager scheduler.");
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
