package services;

import models.Review;
import utils.FileHandler;
import utils.SecurityUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ReviewService {
    private List<Review> reviews;
    private static final String REVIEWS_FILE = "reviews.txt";

    public ReviewService() {
        loadReviews();
    }

    private void loadReviews() {
        reviews = FileHandler.loadData(REVIEWS_FILE, Review::fromString);
    }

    public boolean addReview(Review review) {
        reviews.add(review);
        return FileHandler.appendData(REVIEWS_FILE, review);
    }

    public List<Review> getReviewsForProperty(String listingId) {
        return reviews.stream()
                .filter(review -> review.getType().equals("property") && 
                                review.getRevieweeId().equals(listingId))
                .collect(Collectors.toList());
    }

    public List<Review> getReviewsForUser(String userId) {
        return reviews.stream()
                .filter(review -> review.getRevieweeId().equals(userId))
                .collect(Collectors.toList());
    }

    public double getAverageRatingForProperty(String listingId) {
        return reviews.stream()
                .filter(review -> review.getType().equals("property") && 
                                review.getRevieweeId().equals(listingId))
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public double getAverageRatingForUser(String userId) {
        return reviews.stream()
                .filter(review -> review.getRevieweeId().equals(userId))
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public List<Review> getAllReviews() {
        return new ArrayList<>(reviews);
    }
}