package services;

import models.Booking;
import models.PropertyListing;
import utils.FileHandler;
import utils.SecurityUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BookingService {
    private List<Booking> bookings;
    private PropertyService propertyService;
    private static final String BOOKINGS_FILE = "bookings.txt";

    public BookingService(PropertyService propertyService) {
        this.propertyService = propertyService;
        loadBookings();
    }

    private void loadBookings() {
        bookings = FileHandler.loadData(BOOKINGS_FILE, Booking::fromString);
    }

    public void saveBookings() {
        FileHandler.saveData(BOOKINGS_FILE, bookings);
    }

    // USER: Create booking with full validation
    public Map<String, Object> createBooking(String listingId, String guestId, 
                                           LocalDate checkIn, LocalDate checkOut, 
                                           int numberOfGuests) {
        Map<String, Object> result = new HashMap<>();
        
        Optional<PropertyListing> listingOpt = propertyService.getListingById(listingId);
        
        if (!listingOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "Property not found");
            return result;
        }

        PropertyListing listing = listingOpt.get();

        // Validate dates
        if (checkIn.isBefore(LocalDate.now())) {
            result.put("success", false);
            result.put("message", "Check-in date cannot be in the past");
            return result;
        }

        if (checkOut.isBefore(checkIn) || checkOut.equals(checkIn)) {
            result.put("success", false);
            result.put("message", "Check-out date must be after check-in date");
            return result;
        }

        // Check availability
        if (!listing.isAvailable(checkIn, checkOut)) {
            result.put("success", false);
            result.put("message", "Selected dates are not available");
            return result;
        }

        // Check guest capacity
        if (numberOfGuests > listing.getMaxGuests()) {
            result.put("success", false);
            result.put("message", "Number of guests exceeds property capacity");
            return result;
        }

        if (numberOfGuests < 1) {
            result.put("success", false);
            result.put("message", "Number of guests must be at least 1");
            return result;
        }

        // Calculate total price
        long nights = checkIn.until(checkOut).getDays();
        double totalPrice = nights * listing.getPricePerNight();

        // Create booking
        Booking booking = new Booking(
            SecurityUtils.generateId(),
            listingId,
            guestId,
            checkIn,
            checkOut,
            numberOfGuests,
            totalPrice
        );

        // Reserve dates immediately
        propertyService.addBookedDates(listingId, checkIn, checkOut);

        bookings.add(booking);
        FileHandler.appendData(BOOKINGS_FILE, booking);
        
        result.put("success", true);
        result.put("booking", booking);
        result.put("message", "Booking request submitted successfully!");
        return result;
    }

    // HOST: Confirm booking
    public Map<String, Object> confirmBooking(String bookingId, String hostId) {
        Map<String, Object> result = new HashMap<>();
        
        Optional<Booking> bookingOpt = getBookingById(bookingId);
        if (!bookingOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "Booking not found");
            return result;
        }

        Booking booking = bookingOpt.get();
        
        // Verify host ownership and booking status
        Optional<PropertyListing> listingOpt = propertyService.getListingById(booking.getListingId());
        if (!listingOpt.isPresent() || !listingOpt.get().getHostId().equals(hostId)) {
            result.put("success", false);
            result.put("message", "You are not authorized to confirm this booking");
            return result;
        }

        if (!booking.getStatus().equals("pending")) {
            result.put("success", false);
            result.put("message", "Only pending bookings can be confirmed");
            return result;
        }

        booking.setStatus("confirmed");
        saveBookings();
        
        result.put("success", true);
        result.put("message", "Booking confirmed successfully!");
        return result;
    }

    // HOST: Reject booking
    public Map<String, Object> rejectBooking(String bookingId, String hostId, String reason) {
        Map<String, Object> result = new HashMap<>();
        
        Optional<Booking> bookingOpt = getBookingById(bookingId);
        if (!bookingOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "Booking not found");
            return result;
        }

        Booking booking = bookingOpt.get();
        
        // Verify host ownership
        Optional<PropertyListing> listingOpt = propertyService.getListingById(booking.getListingId());
        if (!listingOpt.isPresent() || !listingOpt.get().getHostId().equals(hostId)) {
            result.put("success", false);
            result.put("message", "You are not authorized to reject this booking");
            return result;
        }

        if (!booking.getStatus().equals("pending")) {
            result.put("success", false);
            result.put("message", "Only pending bookings can be rejected");
            return result;
        }

        booking.setStatus("rejected");
        // Free up the dates
        propertyService.removeBookedDates(booking.getListingId(), 
            booking.getCheckInDate(), booking.getCheckOutDate());
        saveBookings();
        
        result.put("success", true);
        result.put("message", "Booking rejected. " + (reason != null ? "Reason: " + reason : ""));
        return result;
    }

    // USER/HOST: Cancel booking with refund calculation
    public Map<String, Object> cancelBooking(String bookingId, String userId) {
        Map<String, Object> result = new HashMap<>();
        
        Optional<Booking> bookingOpt = getBookingById(bookingId);
        if (!bookingOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "Booking not found");
            return result;
        }

        Booking booking = bookingOpt.get();
        
        // Verify authorization
        boolean isGuest = booking.getGuestId().equals(userId);
        boolean isHost = isUserHost(booking.getListingId(), userId);
        
        if (!isGuest && !isHost) {
            result.put("success", false);
            result.put("message", "You are not authorized to cancel this booking");
            return result;
        }

        if (!booking.canBeCancelled()) {
            result.put("success", false);
            result.put("message", "This booking cannot be cancelled");
            return result;
        }

        double refundAmount = booking.calculateRefundAmount();
        booking.setStatus("cancelled");
        
        // Free up the dates
        propertyService.removeBookedDates(booking.getListingId(), 
            booking.getCheckInDate(), booking.getCheckOutDate());
        saveBookings();
        
        result.put("success", true);
        result.put("refundAmount", refundAmount);
        if (isGuest && refundAmount > 0) {
            result.put("message", String.format("Booking cancelled. Refund amount: $%.2f", refundAmount));
        } else {
            result.put("message", "Booking cancelled successfully");
        }
        return result;
    }

    // HOST: Mark booking as completed
    public Map<String, Object> completeBooking(String bookingId, String hostId) {
        Map<String, Object> result = new HashMap<>();
        
        Optional<Booking> bookingOpt = getBookingById(bookingId);
        if (!bookingOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "Booking not found");
            return result;
        }

        Booking booking = bookingOpt.get();
        
        // Verify host ownership
        Optional<PropertyListing> listingOpt = propertyService.getListingById(booking.getListingId());
        if (!listingOpt.isPresent() || !listingOpt.get().getHostId().equals(hostId)) {
            result.put("success", false);
            result.put("message", "You are not authorized to complete this booking");
            return result;
        }

        if (!booking.getStatus().equals("confirmed")) {
            result.put("success", false);
            result.put("message", "Only confirmed bookings can be marked as completed");
            return result;
        }

        if (booking.getCheckOutDate().isAfter(LocalDate.now())) {
            result.put("success", false);
            result.put("message", "Cannot complete booking before check-out date");
            return result;
        }

        booking.setStatus("completed");
        saveBookings();
        
        result.put("success", true);
        result.put("message", "Booking marked as completed");
        return result;
    }

    // Query methods with enhanced filtering
    public List<Booking> getBookingsByGuest(String guestId) {
        return bookings.stream()
                .filter(booking -> booking.getGuestId().equals(guestId))
                .sorted((b1, b2) -> b2.getBookedAt().compareTo(b1.getBookedAt()))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsForHost(String hostId) {
        return bookings.stream()
                .filter(booking -> isUserHost(booking.getListingId(), hostId))
                .sorted((b1, b2) -> b2.getBookedAt().compareTo(b1.getBookedAt()))
                .collect(Collectors.toList());
    }

    public List<Booking> getPendingBookingsForHost(String hostId) {
        return bookings.stream()
                .filter(booking -> booking.getStatus().equals("pending"))
                .filter(booking -> isUserHost(booking.getListingId(), hostId))
                .sorted((b1, b2) -> b1.getBookedAt().compareTo(b2.getBookedAt()))
                .collect(Collectors.toList());
    }

    public List<Booking> getUpcomingBookingsForHost(String hostId) {
        return bookings.stream()
                .filter(booking -> booking.getStatus().equals("confirmed"))
                .filter(booking -> isUserHost(booking.getListingId(), hostId))
                .filter(booking -> booking.getCheckInDate().isAfter(LocalDate.now()))
                .sorted((b1, b2) -> b1.getCheckInDate().compareTo(b2.getCheckInDate()))
                .collect(Collectors.toList());
    }

    public List<Booking> getActiveBookingsForHost(String hostId) {
        return bookings.stream()
                .filter(booking -> booking.isActive())
                .filter(booking -> isUserHost(booking.getListingId(), hostId))
                .collect(Collectors.toList());
    }

    // Statistics and analytics
    public Map<String, Object> getBookingStatistics(String hostId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Booking> hostBookings = getBookingsForHost(hostId);
        
        stats.put("totalBookings", hostBookings.size());
        stats.put("pendingBookings", (int) hostBookings.stream().filter(b -> b.getStatus().equals("pending")).count());
        stats.put("confirmedBookings", (int) hostBookings.stream().filter(b -> b.getStatus().equals("confirmed")).count());
        stats.put("completedBookings", (int) hostBookings.stream().filter(b -> b.getStatus().equals("completed")).count());
        stats.put("cancelledBookings", (int) hostBookings.stream().filter(b -> b.getStatus().equals("cancelled")).count());
        
        double totalRevenue = hostBookings.stream()
                .filter(b -> b.getStatus().equals("completed"))
                .mapToDouble(Booking::getTotalPrice)
                .sum();
        stats.put("totalRevenue", totalRevenue);
        
        return stats;
    }

    // Helper methods
    private boolean isUserHost(String listingId, String userId) {
        Optional<PropertyListing> listingOpt = propertyService.getListingById(listingId);
        return listingOpt.isPresent() && listingOpt.get().getHostId().equals(userId);
    }

    public Optional<Booking> getBookingById(String bookingId) {
        return bookings.stream()
                .filter(booking -> booking.getBookingId().equals(bookingId))
                .findFirst();
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    // Check if user can review a booking
    public boolean canUserReviewBooking(String bookingId, String userId) {
        Optional<Booking> bookingOpt = getBookingById(bookingId);
        return bookingOpt.isPresent() && 
               bookingOpt.get().getGuestId().equals(userId) && 
               bookingOpt.get().canBeReviewed();
    }

    // Check availability for specific dates
    public boolean isListingAvailable(String listingId, LocalDate checkIn, LocalDate checkOut, String excludeBookingId) {
        Optional<PropertyListing> listingOpt = propertyService.getListingById(listingId);
        if (!listingOpt.isPresent()) return false;
        
        PropertyListing listing = listingOpt.get();
        
        // Check if any other booking conflicts with these dates
        boolean hasConflict = bookings.stream()
                .filter(booking -> !booking.getBookingId().equals(excludeBookingId))
                .filter(booking -> booking.getListingId().equals(listingId))
                .filter(booking -> booking.getStatus().equals("pending") || booking.getStatus().equals("confirmed"))
                .anyMatch(booking -> datesOverlap(booking.getCheckInDate(), booking.getCheckOutDate(), checkIn, checkOut));
        
        return listing.isAvailable(checkIn, checkOut) && !hasConflict;
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2.minusDays(1)) && !start2.isAfter(end1.minusDays(1));
    }
    
    // USER: Edit existing booking (only before confirmation)
public Map<String, Object> editBooking(String bookingId, String guestId, 
                                       LocalDate newCheckIn, LocalDate newCheckOut, 
                                       int newGuestCount) {
    Map<String, Object> result = new HashMap<>();

    Optional<Booking> bookingOpt = getBookingById(bookingId);
    if (!bookingOpt.isPresent()) {
        result.put("success", false);
        result.put("message", "Booking not found");
        return result;
    }

    Booking booking = bookingOpt.get();

    // Only guest who made booking can edit it
    if (!booking.getGuestId().equals(guestId)) {
        result.put("success", false);
        result.put("message", "You are not authorized to edit this booking");
        return result;
    }

    // Only pending bookings can be edited
    if (!booking.getStatus().equals("pending")) {
        result.put("success", false);
        result.put("message", "Only pending bookings can be edited");
        return result;
    }

    Optional<PropertyListing> listingOpt = propertyService.getListingById(booking.getListingId());
    if (!listingOpt.isPresent()) {
        result.put("success", false);
        result.put("message", "Property not found");
        return result;
    }

    PropertyListing listing = listingOpt.get();

    // Validate new dates
    if (newCheckIn.isBefore(LocalDate.now())) {
        result.put("success", false);
        result.put("message", "Check-in date cannot be in the past");
        return result;
    }

    if (!isListingAvailable(listing.getListingId(), newCheckIn, newCheckOut, bookingId)) {
        result.put("success", false);
        result.put("message", "New dates are not available");
        return result;
    }

    if (newGuestCount < 1 || newGuestCount > listing.getMaxGuests()) {
        result.put("success", false);
        result.put("message", "Invalid number of guests");
        return result;
    }

    // Update booking details
    booking.setCheckInDate(newCheckIn);
    booking.setCheckOutDate(newCheckOut);
    booking.setNumberOfGuests(newGuestCount);

    long nights = newCheckIn.until(newCheckOut).getDays();
    booking.setTotalPrice(nights * listing.getPricePerNight());

    saveBookings();

    result.put("success", true);
    result.put("booking", booking);
    result.put("message", "Booking updated successfully");
    return result;
}


// USER: Delete booking (only if pending or cancelled)
public Map<String, Object> deleteBooking(String bookingId, String guestId) {
    Map<String, Object> result = new HashMap<>();

    Optional<Booking> bookingOpt = getBookingById(bookingId);
    if (!bookingOpt.isPresent()) {
        result.put("success", false);
        result.put("message", "Booking not found");
        return result;
    }

    Booking booking = bookingOpt.get();

    // Verify guest ownership
    if (!booking.getGuestId().equals(guestId)) {
        result.put("success", false);
        result.put("message", "You are not authorized to delete this booking");
        return result;
    }

    // Allow deletion only if booking is pending or cancelled
    if (!(booking.getStatus().equals("pending") || booking.getStatus().equals("cancelled"))) {
        result.put("success", false);
        result.put("message", "Only pending or cancelled bookings can be deleted");
        return result;
    }

    bookings.remove(booking);
    saveBookings();

    result.put("success", true);
    result.put("message", "Booking deleted successfully");
    return result;
}


// ADMIN: View all bookings
public List<Booking> viewAllBookingsForAdmin(String adminId) {
    // Admin has read-only access, so we just return all bookings
    return getAllBookings()
            .stream()
            .sorted((b1, b2) -> b2.getBookedAt().compareTo(b1.getBookedAt()))
            .collect(Collectors.toList());
}


public enum UserRole {
    ADMIN, HOST, GUEST
}

public List<Booking> getBookingsByRole(String userId, UserRole role) {
    switch (role) {
        case ADMIN:
            return getAllBookings();
        case HOST:
            return getBookingsForHost(userId);
        case GUEST:
            return getBookingsByGuest(userId);
        default:
            return Collections.emptyList();
    }
}

}