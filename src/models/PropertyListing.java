package models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PropertyListing implements Serializable {
    private String listingId;
    private String hostId;
    private String title;
    private String description;
    private String location;
    private double pricePerNight;
    private int maxGuests;
    private int bedrooms;
    private int bathrooms;
    private List<String> amenities;
    private List<LocalDate> bookedDates;
    private boolean isActive;

    public PropertyListing(String listingId, String hostId, String title, String description, 
                         String location, double pricePerNight, int maxGuests, 
                         int bedrooms, int bathrooms) {
        this.listingId = listingId;
        this.hostId = hostId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.pricePerNight = pricePerNight;
        this.maxGuests = maxGuests;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.amenities = new ArrayList<>();
        this.bookedDates = new ArrayList<>();
        this.isActive = true;
    }

    // Getters and setters
    public String getListingId() { return listingId; }
    public String getHostId() { return hostId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public double getPricePerNight() { return pricePerNight; }
    public int getMaxGuests() { return maxGuests; }
    public int getBedrooms() { return bedrooms; }
    public int getBathrooms() { return bathrooms; }
    public List<String> getAmenities() { return amenities; }
    public List<LocalDate> getBookedDates() { return bookedDates; }
    public boolean isActive() { return isActive; }

    public void setActive(boolean active) { isActive = active; }
    public void addAmenity(String amenity) { amenities.add(amenity); }
    public void addBookedDate(LocalDate date) { bookedDates.add(date); }
    public void removeBookedDate(LocalDate date) { bookedDates.remove(date); }

    public boolean isAvailable(LocalDate startDate, LocalDate endDate) {
        return bookedDates.stream()
            .noneMatch(bookedDate -> 
                !bookedDate.isBefore(startDate) && !bookedDate.isAfter(endDate));
    }

    @Override
    public String toString() {
        return String.join(",",
            listingId, hostId, title, description, location,
            String.valueOf(pricePerNight), String.valueOf(maxGuests),
            String.valueOf(bedrooms), String.valueOf(bathrooms),
            String.join(";", amenities),
            String.join(";", bookedDates.stream().map(LocalDate::toString).toArray(String[]::new)),
            String.valueOf(isActive)
        );
    }

    public static PropertyListing fromString(String data) {
        String[] parts = data.split(",");
        PropertyListing listing = new PropertyListing(
            parts[0], parts[1], parts[2], parts[3], parts[4],
            Double.parseDouble(parts[5]), Integer.parseInt(parts[6]),
            Integer.parseInt(parts[7]), Integer.parseInt(parts[8])
        );
        
        // Parse amenities
        if (!parts[9].isEmpty()) {
            for (String amenity : parts[9].split(";")) {
                listing.addAmenity(amenity);
            }
        }
        
        // Parse booked dates
        if (!parts[10].isEmpty()) {
            for (String dateStr : parts[10].split(";")) {
                listing.addBookedDate(LocalDate.parse(dateStr));
            }
        }
        
        listing.setActive(Boolean.parseBoolean(parts[11]));
        return listing;
    }
    
       // Add these setters to the PropertyListing class
public void setTitle(String title) { this.title = title; }
public void setDescription(String description) { this.description = description; }
public void setLocation(String location) { this.location = location; }
public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
public void setMaxGuests(int maxGuests) { this.maxGuests = maxGuests; }
public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }
public void setBathrooms(int bathrooms) { this.bathrooms = bathrooms; }
public void setAmenities(List<String> amenities) { this.amenities = amenities; }

}