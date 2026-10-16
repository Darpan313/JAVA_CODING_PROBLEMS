.# Ticket Booking System

## What is Ticket booking system?
Imagine a confined room with limited seats, and you get concurrent requests to book seats. 
Book seats only if it's available. We should show available seats. Prevent 2 users from booking the same seat.

### Questions
1. When system launch we'll get N seats?
2. Can one user book multiple seats? 
3. Will user provide seat number they want?
4. Are we going to reserve seat before booking? Why do you ask that? - In case user read-and-then-act, two users
might read same seat available but when they pay for it payment fails as seat becomes unavailable.
5. How do we guarantee fairness? First come first served

## Requirements
1. Users can search for movies by title.
2. Users can browse movies playing at a given theatre.
3. Theaters have mutiple screens; all screens share the same seat layout (rows A-Z, seat 0-20)
4. Users can view available seats for a showtime and select specific ones.
5. User can book multiple seats in a single reservation; booking returns a confirmation Id.
6. Concurrent booking of the same seat: exactly one suceeds.
7. Users can cancel reservation by confirmation ID, releasing the seats.  

### Out of Scope
1. Payment processing (assume payment always succeeds)
2. Variable seat layouts or seats types (all seats identical)
3. Rescheduling (cancel and rebook instead)
4. UI/rendering

## Core Entities & Relationships
### Key Relationships
**BookingSystem** -> `List<Theater>`  
**Theater** -> `List<Showtime>`   
**Showtime** -> `Movie (reference)`  
**Showtime** -> `List<Reservation> (booking records for this Showtime`  
**Reservation** -> `Showtime (back-reference for cancellation routing)`   
**Reservation** -> `List<String> (e.g., ["A5", "A6"])`  
