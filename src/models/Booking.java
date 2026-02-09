package models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking implements Serializable {
    private String bookingId;
    private String listingId;
    private String guestId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
    private double totalPrice;
    private String status; // "pending", "confirmed", "cancelled", "completed", "rejected"
    private LocalDateTime bookedAt;

    public Booking(String bookingId, String listingId, String guestId, 
                  LocalDate checkInDate, LocalDate checkOutDate, 
                  int numberOfGuests, double totalPrice) {
        this.bookingId = bookingId;
        this.listingId = listingId;
        this.guestId = guestId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.totalPrice = totalPrice;
        this.status = "pending";
        this.bookedAt = LocalDateTime.now();
    }

    // Getters
    public String getBookingId() { return bookingId; }
    public String getListingId() { return listingId; }
    public String getGuestId() { return guestId; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public int getNumberOfGuests() { return numberOfGuests; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public LocalDateTime getBookedAt() { return bookedAt; }

    // Setters for full functionality
    public void setStatus(String status) { 
        if (isValidStatusTransition(this.status, status)) {
            this.status = status; 
        }
    }
    
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    // Validation for status transitions
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        switch (currentStatus) {
            case "pending":
                return newStatus.equals("confirmed") || newStatus.equals("rejected") || newStatus.equals("cancelled");
            case "confirmed":
                return newStatus.equals("completed") || newStatus.equals("cancelled");
            case "completed":
                return false; // Cannot change completed bookings
            case "cancelled":
                return false; // Cannot change cancelled bookings
            case "rejected":
                return false; // Cannot change rejected bookings
            default:
                return true;
        }
    }

    // Business logic methods
    public boolean canBeCancelled() {
        return status.equals("pending") || status.equals("confirmed");
    }

    public boolean canBeReviewed() {
        return status.equals("completed") && checkOutDate.isBefore(LocalDate.now());
    }

    public boolean isUpcoming() {
        return (status.equals("confirmed") || status.equals("pending")) && 
               checkInDate.isAfter(LocalDate.now());
    }

    public boolean isActive() {
        return status.equals("confirmed") && 
               !checkInDate.isAfter(LocalDate.now()) && 
               !checkOutDate.isBefore(LocalDate.now());
    }

    public long getNumberOfNights() {
        return checkInDate.until(checkOutDate).getDays();
    }

    public double calculateRefundAmount() {
        if (!canBeCancelled()) return 0.0;
        
        LocalDate today = LocalDate.now();
        long daysUntilCheckIn = today.until(checkInDate).getDays();
        
        if (daysUntilCheckIn >= 7) {
            return totalPrice * 0.8; // 80% refund
        } else if (daysUntilCheckIn >= 3) {
            return totalPrice * 0.5; // 50% refund
        } else {
            return 0.0; // No refund
        }
    }

    @Override
    public String toString() {
        return String.join(",",
            bookingId, listingId, guestId,
            checkInDate.toString(), checkOutDate.toString(),
            String.valueOf(numberOfGuests), String.valueOf(totalPrice),
            status, bookedAt.toString()
        );
    }

    public static Booking fromString(String data) {
        String[] parts = data.split(",");
        Booking booking = new Booking(
            parts[0], parts[1], parts[2],
            LocalDate.parse(parts[3]), LocalDate.parse(parts[4]),
            Integer.parseInt(parts[5]), Double.parseDouble(parts[6])
        );
        booking.status = parts[7];
        booking.bookedAt = LocalDateTime.parse(parts[8]);
        return booking;
    }
}