package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Review implements Serializable {
    private String reviewId;
    private String bookingId;
    private String reviewerId;
    private String revieweeId; // host or guest being reviewed
    private int rating;
    private String comment;
    private String type; // "property" or "guest"
    private LocalDateTime createdAt;

    public Review(String reviewId, String bookingId, String reviewerId, 
                 String revieweeId, int rating, String comment, String type) {
        this.reviewId = reviewId;
        this.bookingId = bookingId;
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.rating = rating;
        this.comment = comment;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getReviewId() { return reviewId; }
    public String getBookingId() { return bookingId; }
    public String getReviewerId() { return reviewerId; }
    public String getRevieweeId() { return revieweeId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.join(",",
            reviewId, bookingId, reviewerId, revieweeId,
            String.valueOf(rating), comment, type, createdAt.toString()
        );
    }

    public static Review fromString(String data) {
        String[] parts = data.split(",");
        return new Review(
            parts[0], parts[1], parts[2], parts[3],
            Integer.parseInt(parts[4]), parts[5], parts[6]
        );
    }
}