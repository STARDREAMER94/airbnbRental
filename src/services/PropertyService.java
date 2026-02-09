package services;

import models.PropertyListing;
import utils.FileHandler;
import utils.SecurityUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class PropertyService {
    private List<PropertyListing> listings;
    private static final String LISTINGS_FILE = "listings.txt";

    public PropertyService() {
        loadListings();
    }

    private void loadListings() {
        listings = FileHandler.loadData(LISTINGS_FILE, PropertyListing::fromString);
    }

    public void saveListings() {
        FileHandler.saveData(LISTINGS_FILE, listings);
    }

    public boolean addListing(PropertyListing listing) {
        listings.add(listing);
        return FileHandler.appendData(LISTINGS_FILE, listing);
    }

    /*public boolean updateListing(PropertyListing updatedListing) {
        Optional<PropertyListing> existingOpt = listings.stream()
                .filter(listing -> listing.getListingId().equals(updatedListing.getListingId()))
                .findFirst();

        if (existingOpt.isPresent()) {
            listings.remove(existingOpt.get());
            listings.add(updatedListing);
            saveListings();
            return true;
        }
        return false;
    }*/
    public boolean updateListing(PropertyListing updatedListing) {
    for (int i = 0; i < listings.size(); i++) {
        if (listings.get(i).getListingId().equals(updatedListing.getListingId())) {
            listings.set(i, updatedListing);
            saveListings();
            return true;
        }
    }
    return false;
}

    public boolean deleteListing(String listingId, String hostId) {
        Optional<PropertyListing> listingOpt = listings.stream()
                .filter(listing -> listing.getListingId().equals(listingId) && 
                                 listing.getHostId().equals(hostId))
                .findFirst();

        if (listingOpt.isPresent()) {
            PropertyListing listing = listingOpt.get();
            listing.setActive(false);
            saveListings();
            return true;
        }
        return false;
    }

    public List<PropertyListing> getListingsByHost(String hostId) {
        return listings.stream()
                .filter(listing -> listing.getHostId().equals(hostId) && listing.isActive())
                .collect(Collectors.toList());
    }

    public List<PropertyListing> searchListings(String location, LocalDate checkIn, LocalDate checkOut, int guests) {
        return listings.stream()
                .filter(PropertyListing::isActive)
                .filter(listing -> location == null || listing.getLocation().toLowerCase().contains(location.toLowerCase()))
                .filter(listing -> guests == 0 || listing.getMaxGuests() >= guests)
                .filter(listing -> checkIn == null || checkOut == null || 
                                 listing.isAvailable(checkIn, checkOut))
                .collect(Collectors.toList());
    }

    public Optional<PropertyListing> getListingById(String listingId) {
        return listings.stream()
                .filter(listing -> listing.getListingId().equals(listingId))
                .findFirst();
    }

    public List<PropertyListing> getAllListings() {
        return new ArrayList<>(listings);
    }
    
    // method to add booked dates to a listing
    public boolean addBookedDates(String listingId, LocalDate startDate, LocalDate endDate) {
        Optional<PropertyListing> listingOpt = getListingById(listingId);
        if (listingOpt.isPresent()) {
            PropertyListing listing = listingOpt.get();
            LocalDate current = startDate;
            while (!current.isAfter(endDate.minusDays(1))) {
                listing.addBookedDate(current);
                current = current.plusDays(1);
            }
            saveListings();
            return true;
        }
        return false;
    }

    // method to remove booked dates (for cancellations)
    public boolean removeBookedDates(String listingId, LocalDate startDate, LocalDate endDate) {
        Optional<PropertyListing> listingOpt = getListingById(listingId);
        if (listingOpt.isPresent()) {
            PropertyListing listing = listingOpt.get();
            LocalDate current = startDate;
            while (!current.isAfter(endDate.minusDays(1))) {
                listing.removeBookedDate(current);
                current = current.plusDays(1);
            }
            saveListings();
            return true;
        }
        return false;
    }
}