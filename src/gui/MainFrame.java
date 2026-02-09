package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
/*
import services.UserService;
import services.PropertyService;
//import services.BookingService;
import services.ReviewService;
import services.MessageService;
*/

/**
 * Complete Airbnb-like rental system with all model classes integrated
 */
public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Services
    private UserService userService = new UserService();
    private PropertyService propertyService = new PropertyService();
    private BookingService bookingService = new BookingService();
    private MessageService messageService = new MessageService();
    private ReviewService reviewService = new ReviewService();

    // Current user
    private models.User currentUser;

    // GUI components
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JTextField registerUsernameField;
    private JPasswordField registerPasswordField;
    private JTextField registerEmailField;
    private JComboBox<String> registerRoleComboBox;
    private DefaultListModel<models.PropertyListing> searchResultsModel;
    private JList<models.PropertyListing> searchResultsList;
    private DefaultListModel<models.Booking> bookingsModel;
    private JList<models.Booking> bookingsList;
    private DefaultListModel<models.Review> reviewsModel;

    public MainFrame() {
        
        
        setTitle("Airbnb-Like Rental System");
    setSize(1000, 700);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // TEMPORARY: Create a dummy user for testing
    if (currentUser == null) {
        userService.registerUser("test", "test", "test@test.com", "guest");
        userService.login("test", "test");
        currentUser = userService.getCurrentUser();
    }

    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);

    mainPanel.add(createLoginPanel(), "LOGIN");
    mainPanel.add(createRegisterPanel(), "REGISTER");
    mainPanel.add(createGuestDashboardPanel(), "GUEST");
    mainPanel.add(createHostDashboardPanel(), "HOST");

    add(mainPanel);
    showLoginScreen();
    }

    // ============ LOGIN SCREEN ============
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Login", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        loginUsernameField = new JTextField(15);
        panel.add(loginUsernameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPasswordField = new JPasswordField(15);
        panel.add(loginPasswordField, gbc);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> showRegisterScreen());
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel();
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        panel.add(btnPanel, gbc);

        return panel;
    }

    // ============ REGISTER SCREEN ============
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Register New Account", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        registerUsernameField = new JTextField(15);
        panel.add(registerUsernameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        registerPasswordField = new JPasswordField(15);
        panel.add(registerPasswordField, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        registerEmailField = new JTextField(15);
        panel.add(registerEmailField, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        registerRoleComboBox = new JComboBox<>(new String[]{"guest", "host"});
        panel.add(registerRoleComboBox, gbc);

        JButton regBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");
        regBtn.addActionListener(e -> handleRegister());
        backBtn.addActionListener(e -> showLoginScreen());
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel();
        btnPanel.add(regBtn);
        btnPanel.add(backBtn);
        panel.add(btnPanel, gbc);

        return panel;
    }

    // ============ GUEST DASHBOARD ============
    private JPanel createGuestDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel label = new JLabel("Guest Dashboard - Welcome " + currentUser.getUsername(), JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.NORTH);

        JPanel btns = new JPanel();
        JButton searchBtn = new JButton("Search Properties");
        JButton bookingsBtn = new JButton("My Bookings");
        JButton reviewsBtn = new JButton("My Reviews");
        JButton messagesBtn = new JButton("Messages");
        JButton logoutBtn = new JButton("Logout");
        
        searchBtn.addActionListener(e -> showSearchPanel());
        bookingsBtn.addActionListener(e -> showGuestBookingsPanel());
        reviewsBtn.addActionListener(e -> showGuestReviewsPanel());
        messagesBtn.addActionListener(e -> showMessagesPanel());
        logoutBtn.addActionListener(e -> handleLogout());
        
        btns.add(searchBtn);
        btns.add(bookingsBtn);
        btns.add(reviewsBtn);
        btns.add(messagesBtn);
        btns.add(logoutBtn);
        panel.add(btns, BorderLayout.SOUTH);

        // Show upcoming bookings
        List<models.Booking> upcomingBookings = bookingService.getUpcomingBookings(currentUser.getUserId());
        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        StringBuilder infoText = new StringBuilder();
        infoText.append("Welcome to your Airbnb Dashboard!\n\n");
        infoText.append("Upcoming Bookings:\n");
        if (upcomingBookings.isEmpty()) {
            infoText.append("  No upcoming bookings\n");
        } else {
            for (models.Booking booking : upcomingBookings) {
                models.PropertyListing property = propertyService.getPropertyById(booking.getListingId());
                infoText.append(String.format("  %s - %s to %s (%s)\n", 
                    property.getTitle(), 
                    booking.getCheckInDate(), 
                    booking.getCheckOutDate(),
                    booking.getStatus()));
            }
        }
        infoText.append("\nAs a guest you can:\n");
        infoText.append("- Search available properties\n");
        infoText.append("- Book stays\n");
        infoText.append("- Manage bookings\n");
        infoText.append("- Message hosts\n");
        infoText.append("- Write reviews\n");
        
        info.setText(infoText.toString());
        panel.add(new JScrollPane(info), BorderLayout.CENTER);

        return panel;
    }

    // ============ HOST DASHBOARD ============
    private JPanel createHostDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel label = new JLabel("Host Dashboard - Welcome " + currentUser.getUsername(), JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.NORTH);

        JPanel btns = new JPanel();
        JButton manageListingsBtn = new JButton("Manage Listings");
        JButton manageBookingsBtn = new JButton("Manage Bookings");
        JButton reviewsBtn = new JButton("Property Reviews");
        JButton messagesBtn = new JButton("Messages");
        JButton logoutBtn = new JButton("Logout");
        
        manageListingsBtn.addActionListener(e -> showHostListingsPanel());
        manageBookingsBtn.addActionListener(e -> showHostBookingsPanel());
        reviewsBtn.addActionListener(e -> showHostReviewsPanel());
        messagesBtn.addActionListener(e -> showMessagesPanel());
        logoutBtn.addActionListener(e -> handleLogout());
        
        btns.add(manageListingsBtn);
        btns.add(manageBookingsBtn);
        btns.add(reviewsBtn);
        btns.add(messagesBtn);
        btns.add(logoutBtn);
        panel.add(btns, BorderLayout.SOUTH);

        // Show pending booking requests
        List<models.Booking> pendingBookings = bookingService.getPendingBookingsForHost(currentUser.getUserId());
        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        StringBuilder infoText = new StringBuilder();
        infoText.append("Welcome to your Host Dashboard!\n\n");
        infoText.append("Pending Booking Requests:\n");
        if (pendingBookings.isEmpty()) {
            infoText.append("  No pending requests\n");
        } else {
            for (models.Booking booking : pendingBookings) {
                models.PropertyListing property = propertyService.getPropertyById(booking.getListingId());
                infoText.append(String.format("  %s - %s to %s\n", 
                    property.getTitle(), 
                    booking.getCheckInDate(), 
                    booking.getCheckOutDate()));
            }
        }
        infoText.append("\nAs a host you can:\n");
        infoText.append("- Create and manage property listings\n");
        infoText.append("- View and manage booking requests\n");
        infoText.append("- Respond to guest inquiries\n");
        infoText.append("- View property reviews\n");
        
        info.setText(infoText.toString());
        panel.add(new JScrollPane(info), BorderLayout.CENTER);

        return panel;
    }

    // ============ SEARCH PANEL ============
    // ============ SEARCH PANEL ============
private void showSearchPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Create the main content panel
    JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

    // 1. SEARCH CRITERIA PANEL (Top)
    JPanel searchCriteriaPanel = new JPanel(new GridLayout(2, 2, 10, 10));
    searchCriteriaPanel.setBorder(BorderFactory.createTitledBorder("Search Properties"));

    JTextField locationField = new JTextField();
    JTextField maxPriceField = new JTextField();
    JTextField guestsField = new JTextField("1");
    
    searchCriteriaPanel.add(new JLabel("Location:"));
    searchCriteriaPanel.add(locationField);
    searchCriteriaPanel.add(new JLabel("Max Price:"));
    searchCriteriaPanel.add(maxPriceField);
    searchCriteriaPanel.add(new JLabel("Guests:"));
    searchCriteriaPanel.add(guestsField);

    contentPanel.add(searchCriteriaPanel, BorderLayout.NORTH);

    // 2. BUTTON PANEL (Center) - THIS IS WHAT'S MISSING!
    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton searchBtn = new JButton("🔍 SEARCH PROPERTIES");
    JButton clearBtn = new JButton("Clear");
    JButton backBtn = new JButton("Back to Dashboard");
    
    // Style the search button to make it prominent
    searchBtn.setBackground(new Color(0, 102, 204));
    searchBtn.setForeground(Color.WHITE);
    searchBtn.setFont(new Font("Arial", Font.BOLD, 14));
    searchBtn.setPreferredSize(new Dimension(180, 30));
    
    buttonPanel.add(searchBtn);
    buttonPanel.add(clearBtn);
    buttonPanel.add(backBtn);

    contentPanel.add(buttonPanel, BorderLayout.CENTER);

    // 3. SEARCH RESULTS (Bottom)
    searchResultsModel = new DefaultListModel<>();
    searchResultsList = new JList<>(searchResultsModel);
    searchResultsList.setCellRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean selected, boolean focus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            if (value instanceof models.PropertyListing) {
                models.PropertyListing p = (models.PropertyListing) value;
                lbl.setText(p.getTitle() + " - " + p.getLocation() + " - R" + p.getPricePerNight() + "/night");
            }
            return lbl;
        }
    });
    
    JScrollPane resultsScrollPane = new JScrollPane(searchResultsList);
    resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Search Results"));
    resultsScrollPane.setPreferredSize(new Dimension(400, 300));
    contentPanel.add(resultsScrollPane, BorderLayout.SOUTH);

    // Add the content panel to main panel
    panel.add(contentPanel, BorderLayout.CENTER);

    // SEARCH BUTTON ACTION
    searchBtn.addActionListener(e -> {
        try {
            String location = locationField.getText().trim();
            double maxPrice = maxPriceField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceField.getText());
            int guests = guestsField.getText().isEmpty() ? 1 : Integer.parseInt(guestsField.getText());
            
            List<models.PropertyListing> results = propertyService.searchListings(location, maxPrice, guests);
            searchResultsModel.clear();
            results.forEach(searchResultsModel::addElement);
            
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "No properties found matching your criteria!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, "Please enter valid numbers for price and guests!");
        }
    });

    // CLEAR BUTTON ACTION
    clearBtn.addActionListener(e -> {
        locationField.setText("");
        maxPriceField.setText("");
        guestsField.setText("1");
        searchResultsModel.clear();
    });

    // BACK BUTTON ACTION
    backBtn.addActionListener(e -> showDashboard());

    mainPanel.add(panel, "SEARCH");
    cardLayout.show(mainPanel, "SEARCH");
}

    // ============ BOOKING DIALOG ============
    private void showBookingDialog(models.PropertyListing property) {
        JDialog dialog = new JDialog(this, "Book " + property.getTitle(), true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(400, 300);

        JTextField checkInField = new JTextField(LocalDate.now().plusDays(1).toString());
        JTextField checkOutField = new JTextField(LocalDate.now().plusDays(4).toString());
        JTextField guestsField = new JTextField("2");

        dialog.add(new JLabel("Check-in (YYYY-MM-DD):"));
        dialog.add(checkInField);
        dialog.add(new JLabel("Check-out (YYYY-MM-DD):"));
        dialog.add(checkOutField);
        dialog.add(new JLabel("Number of Guests:"));
        dialog.add(guestsField);

        JButton bookBtn = new JButton("Book Now");
        JButton cancelBtn = new JButton("Cancel");

        bookBtn.addActionListener(e -> {
            try {
                LocalDate checkIn = LocalDate.parse(checkInField.getText());
                LocalDate checkOut = LocalDate.parse(checkOutField.getText());
                int guests = Integer.parseInt(guestsField.getText());
                
                if (checkIn.isAfter(checkOut)) {
                    JOptionPane.showMessageDialog(dialog, "Check-in date must be before check-out date!");
                    return;
                }
                
                if (guests > property.getMaxGuests()) {
                    JOptionPane.showMessageDialog(dialog, "Number of guests exceeds property maximum!");
                    return;
                }
                
                if (!property.isAvailable(checkIn, checkOut)) {
                    JOptionPane.showMessageDialog(dialog, "Property not available for selected dates!");
                    return;
                }
                
                long nights = checkIn.until(checkOut).getDays();
                double totalPrice = nights * property.getPricePerNight();
                
                models.Booking booking = bookingService.createBooking(
                    property.getListingId(), 
                    currentUser.getUserId(), 
                    checkIn, checkOut, guests, totalPrice
                );
                
                if (booking != null) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Booking request submitted! Total: R" + totalPrice + " for " + nights + " nights");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Booking failed. Please try again.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input format! Use YYYY-MM-DD for dates.");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.add(bookBtn);
        btnPanel.add(cancelBtn);
        dialog.add(new JLabel());
        dialog.add(btnPanel);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ============ GUEST BOOKINGS PANEL ============
    private void showGuestBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel title = new JLabel("My Bookings", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        bookingsModel = new DefaultListModel<>();
        bookingsList = new JList<>(bookingsModel);
        bookingsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean selected, boolean focus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                models.Booking b = (models.Booking) value;
                models.PropertyListing property = propertyService.getPropertyById(b.getListingId());
                String propName = property != null ? property.getTitle() : "Unknown Property";
                lbl.setText(propName + " - " + b.getCheckInDate() + " to " + b.getCheckOutDate() + " (" + b.getStatus() + ")");
                return lbl;
            }
        });

        // Load guest's bookings
        List<models.Booking> guestBookings = bookingService.getUserBookings(currentUser.getUserId());
        bookingsModel.clear();
        guestBookings.forEach(bookingsModel::addElement);

        panel.add(new JScrollPane(bookingsList), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton cancelBtn = new JButton("Cancel Booking");
        JButton reviewBtn = new JButton("Write Review");
        JButton backBtn = new JButton("Back");

        cancelBtn.addActionListener(e -> {
            models.Booking selected = bookingsList.getSelectedValue();
            if (selected != null && selected.canBeCancelled()) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Cancel this booking? Refund amount: R" + selected.calculateRefundAmount(),
                    "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (bookingService.cancelBooking(selected.getBookingId())) {
                        JOptionPane.showMessageDialog(this, "Booking cancelled!");
                        showGuestBookingsPanel(); // Refresh
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a valid booking to cancel!");
            }
        });

        reviewBtn.addActionListener(e -> {
            models.Booking selected = bookingsList.getSelectedValue();
            if (selected != null && selected.canBeReviewed()) {
                showReviewDialog(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Can only review completed bookings!");
            }
        });

        backBtn.addActionListener(e -> showDashboard());

        btnPanel.add(cancelBtn);
        btnPanel.add(reviewBtn);
        btnPanel.add(backBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "GUEST_BOOKINGS");
        cardLayout.show(mainPanel, "GUEST_BOOKINGS");
    }

    // ============ REVIEW DIALOG ============
    private void showReviewDialog(models.Booking booking) {
        JDialog dialog = new JDialog(this, "Write Review", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);

        models.PropertyListing property = propertyService.getPropertyById(booking.getListingId());
        
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        
        JLabel propertyLabel = new JLabel("Property: " + property.getTitle());
        propertyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JComboBox<Integer> ratingCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        JTextArea commentArea = new JTextArea(5, 20);
        commentArea.setLineWrap(true);

        formPanel.add(propertyLabel);
        formPanel.add(new JLabel("Rating (1-5 stars):"));
        formPanel.add(ratingCombo);
        formPanel.add(new JLabel("Comment:"));
        formPanel.add(new JScrollPane(commentArea));

        JPanel btnPanel = new JPanel();
        JButton submitBtn = new JButton("Submit Review");
        JButton cancelBtn = new JButton("Cancel");

        submitBtn.addActionListener(e -> {
            int rating = (Integer) ratingCombo.getSelectedItem();
            String comment = commentArea.getText().trim();
            
            if (comment.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a comment!");
                return;
            }

            models.Review review = reviewService.addReview(
                booking.getBookingId(),
                currentUser.getUserId(),
                property.getHostId(),
                rating, comment, "property"
            );
            
            if (review != null) {
                JOptionPane.showMessageDialog(dialog, "Review submitted successfully!");
                dialog.dispose();
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ============ GUEST REVIEWS PANEL ============
    private void showGuestReviewsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel title = new JLabel("My Reviews", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        reviewsModel = new DefaultListModel<>();
        JList<models.Review> reviewsList = new JList<>(reviewsModel);
        reviewsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean selected, boolean focus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                models.Review r = (models.Review) value;
                models.PropertyListing prop = propertyService.getPropertyById(r.getRevieweeId());
                String propName = prop != null ? prop.getTitle() : "Property Review";
                lbl.setText(propName + " - " + r.getRating() + " stars - " + 
                           r.getComment().substring(0, Math.min(50, r.getComment().length())) + "...");
                return lbl;
            }
        });

        // Load guest's reviews
        List<models.Review> guestReviews = reviewService.getUserReviews(currentUser.getUserId());
        reviewsModel.clear();
        guestReviews.forEach(reviewsModel::addElement);

        panel.add(new JScrollPane(reviewsList), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> showDashboard());
        btnPanel.add(backBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "GUEST_REVIEWS");
        cardLayout.show(mainPanel, "GUEST_REVIEWS");
    }

    // ============ HOST LISTINGS PANEL ============
    private void showHostListingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel title = new JLabel("My Property Listings", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        DefaultListModel<models.PropertyListing> listingsModel = new DefaultListModel<>();
        JList<models.PropertyListing> listingsList = new JList<>(listingsModel);
        listingsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean selected, boolean focus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                models.PropertyListing p = (models.PropertyListing) value;
                String status = p.isActive() ? "ACTIVE" : "INACTIVE";
                lbl.setText(p.getTitle() + " - " + p.getLocation() + " (R" + p.getPricePerNight() + ") [" + status + "]");
                return lbl;
            }
        });

        // Load host's listings
        List<models.PropertyListing> hostListings = propertyService.getHostListings(currentUser.getUserId());
        listingsModel.clear();
        hostListings.forEach(listingsModel::addElement);

        panel.add(new JScrollPane(listingsList), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add New Listing");
        JButton editBtn = new JButton("Edit Listing");
        JButton toggleBtn = new JButton("Toggle Active");
        JButton backBtn = new JButton("Back");

        addBtn.addActionListener(e -> showAddListingDialog());
        editBtn.addActionListener(e -> {
            models.PropertyListing selected = listingsList.getSelectedValue();
            if (selected != null) {
                showEditListingDialog(selected);
            }
        });
        toggleBtn.addActionListener(e -> {
            models.PropertyListing selected = listingsList.getSelectedValue();
            if (selected != null) {
                selected.setActive(!selected.isActive());
                propertyService.updateListing(selected);
                JOptionPane.showMessageDialog(this, "Listing status updated!");
                showHostListingsPanel(); // Refresh
            }
        });
        backBtn.addActionListener(e -> showDashboard());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(toggleBtn);
        btnPanel.add(backBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "HOST_LISTINGS");
        cardLayout.show(mainPanel, "HOST_LISTINGS");
    }

    // ============ ADD LISTING DIALOG ============
    private void showAddListingDialog() {
        JDialog dialog = new JDialog(this, "Add New Property Listing", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(500, 400);

        JTextField titleField = new JTextField();
        JTextField locationField = new JTextField();
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        JTextField priceField = new JTextField();
        JTextField maxGuestsField = new JTextField();
        JTextField bedroomsField = new JTextField();
        JTextField bathroomsField = new JTextField();
        JTextField amenitiesField = new JTextField();

        dialog.add(new JLabel("Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Location:"));
        dialog.add(locationField);
        dialog.add(new JLabel("Description:"));
        dialog.add(new JScrollPane(descriptionArea));
        dialog.add(new JLabel("Price per Night:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Max Guests:"));
        dialog.add(maxGuestsField);
        dialog.add(new JLabel("Bedrooms:"));
        dialog.add(bedroomsField);
        dialog.add(new JLabel("Bathrooms:"));
        dialog.add(bathroomsField);
        dialog.add(new JLabel("Amenities (comma-separated):"));
        dialog.add(amenitiesField);

        JButton saveBtn = new JButton("Save Listing");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String location = locationField.getText().trim();
                String description = descriptionArea.getText().trim();
                double price = Double.parseDouble(priceField.getText());
                int maxGuests = Integer.parseInt(maxGuestsField.getText());
                int bedrooms = Integer.parseInt(bedroomsField.getText());
                int bathrooms = Integer.parseInt(bathroomsField.getText());

                if (title.isEmpty() || location.isEmpty() || description.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Title, location and description are required!");
                    return;
                }

                models.PropertyListing listing = propertyService.addListing(
                    currentUser.getUserId(), title, description, location, 
                    price, maxGuests, bedrooms, bathrooms
                );
                
                // Add amenities
                String[] amenities = amenitiesField.getText().split(",");
                for (String amenity : amenities) {
                    String trimmed = amenity.trim();
                    if (!trimmed.isEmpty()) {
                        listing.addAmenity(trimmed);
                    }
                }

                if (listing != null) {
                    JOptionPane.showMessageDialog(dialog, "Listing added successfully!");
                    dialog.dispose();
                    showHostListingsPanel(); // Refresh
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please check number fields (price, guests, bedrooms, bathrooms)!");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(new JLabel());
        dialog.add(btnPanel);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ============ EDIT LISTING DIALOG ============
    private void showEditListingDialog(models.PropertyListing listing) {
        JDialog dialog = new JDialog(this, "Edit Property Listing", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(500, 400);

        JTextField titleField = new JTextField(listing.getTitle());
        JTextField locationField = new JTextField(listing.getLocation());
        JTextArea descriptionArea = new JTextArea(listing.getDescription(), 3, 20);
        descriptionArea.setLineWrap(true);
        JTextField priceField = new JTextField(String.valueOf(listing.getPricePerNight()));
        JTextField maxGuestsField = new JTextField(String.valueOf(listing.getMaxGuests()));
        JTextField bedroomsField = new JTextField(String.valueOf(listing.getBedrooms()));
        JTextField bathroomsField = new JTextField(String.valueOf(listing.getBathrooms()));
        JTextField amenitiesField = new JTextField(String.join(", ", listing.getAmenities()));

        dialog.add(new JLabel("Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Location:"));
        dialog.add(locationField);
        dialog.add(new JLabel("Description:"));
        dialog.add(new JScrollPane(descriptionArea));
        dialog.add(new JLabel("Price per Night:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Max Guests:"));
        dialog.add(maxGuestsField);
        dialog.add(new JLabel("Bedrooms:"));
        dialog.add(bedroomsField);
        dialog.add(new JLabel("Bathrooms:"));
        dialog.add(bathroomsField);
        dialog.add(new JLabel("Amenities (comma-separated):"));
        dialog.add(amenitiesField);

        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String location = locationField.getText().trim();
                String description = descriptionArea.getText().trim();
                double price = Double.parseDouble(priceField.getText());
                int maxGuests = Integer.parseInt(maxGuestsField.getText());
                int bedrooms = Integer.parseInt(bedroomsField.getText());
                int bathrooms = Integer.parseInt(bathroomsField.getText());

                if (title.isEmpty() || location.isEmpty() || description.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Title, location and description are required!");
                    return;
                }

                listing.setTitle(title);
                listing.setLocation(location);
                listing.setDescription(description);
                listing.setPricePerNight(price);
                listing.setMaxGuests(maxGuests);
                listing.setBedrooms(bedrooms);
                listing.setBathrooms(bathrooms);
                
                // Update amenities
                List<String> newAmenities = new ArrayList<>();
                String[] amenities = amenitiesField.getText().split(",");
                for (String amenity : amenities) {
                    String trimmed = amenity.trim();
                    if (!trimmed.isEmpty()) {
                        newAmenities.add(trimmed);
                    }
                }
                listing.setAmenities(newAmenities);

                if (propertyService.updateListing(listing)) {
                    JOptionPane.showMessageDialog(dialog, "Listing updated successfully!");
                    dialog.dispose();
                    showHostListingsPanel(); // Refresh
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please check number fields (price, guests, bedrooms, bathrooms)!");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(new JLabel());
        dialog.add(btnPanel);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

        // ============ HOST BOOKINGS PANEL ============
    private void showHostBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel title = new JLabel("Booking Requests", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        DefaultListModel<models.Booking> hostBookingsModel = new DefaultListModel<>();
        JList<models.Booking> hostBookingsList = new JList<>(hostBookingsModel);
        hostBookingsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean selected, boolean focus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                models.Booking b = (models.Booking) value;
                models.PropertyListing property = propertyService.getPropertyById(b.getListingId());
                String propName = property != null ? property.getTitle() : "Unknown Property";
                models.User guest = userService.getUserById(b.getGuestId());
                String guestName = guest != null ? guest.getUsername() : "Unknown Guest";
                lbl.setText(propName + " - " + guestName + " - " + b.getCheckInDate() + " to " + 
                           b.getCheckOutDate() + " (" + b.getStatus() + ")");
                return lbl;
            }
        });

        // Load host's booking requests
        List<models.Booking> hostBookings = bookingService.getHostBookings(currentUser.getUserId());
        hostBookingsModel.clear();
        hostBookings.forEach(hostBookingsModel::addElement);

        panel.add(new JScrollPane(hostBookingsList), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton approveBtn = new JButton("Approve Booking");
        JButton rejectBtn = new JButton("Reject Booking");
        JButton messageBtn = new JButton("Message Guest");
        JButton backBtn = new JButton("Back");

        approveBtn.addActionListener(e -> {
            models.Booking selected = hostBookingsList.getSelectedValue();
            if (selected != null && "pending".equals(selected.getStatus())) {
                if (bookingService.confirmBooking(selected.getBookingId())) {
                    JOptionPane.showMessageDialog(this, "Booking approved!");
                    showHostBookingsPanel(); // Refresh
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a pending booking to approve!");
            }
        });

        rejectBtn.addActionListener(e -> {
            models.Booking selected = hostBookingsList.getSelectedValue();
            if (selected != null && "pending".equals(selected.getStatus())) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Reject this booking request?", "Confirm Rejection", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (bookingService.rejectBooking(selected.getBookingId())) {
                        JOptionPane.showMessageDialog(this, "Booking rejected!");
                        showHostBookingsPanel(); // Refresh
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a pending booking to reject!");
            }
        });

        messageBtn.addActionListener(e -> {
            models.Booking selected = hostBookingsList.getSelectedValue();
            if (selected != null) {
                showMessageDialog(selected.getGuestId(), "Regarding your booking request");
            }
        });

        backBtn.addActionListener(e -> showDashboard());

        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(messageBtn);
        btnPanel.add(backBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "HOST_BOOKINGS");
        cardLayout.show(mainPanel, "HOST_BOOKINGS");
    }

    // ============ HOST REVIEWS PANEL ============
    private void showHostReviewsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel title = new JLabel("Property Reviews", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        DefaultListModel<models.Review> hostReviewsModel = new DefaultListModel<>();
        JList<models.Review> hostReviewsList = new JList<>(hostReviewsModel);
        hostReviewsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean selected, boolean focus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                models.Review r = (models.Review) value;
                models.PropertyListing prop = propertyService.getPropertyById(r.getRevieweeId());
                String propName = prop != null ? prop.getTitle() : "Unknown Property";
                models.User reviewer = userService.getUserById(r.getReviewerId());
                String reviewerName = reviewer != null ? reviewer.getUsername() : "Unknown User";
                lbl.setText(propName + " - " + reviewerName + " - " + r.getRating() + " stars: " + 
                           r.getComment().substring(0, Math.min(30, r.getComment().length())) + "...");
                return lbl;
            }
        });

        // Load reviews for host's properties
        List<models.Review> hostReviews = reviewService.getHostReviews(currentUser.getUserId());
        hostReviewsModel.clear();
        hostReviews.forEach(hostReviewsModel::addElement);

        // Calculate average rating
        double avgRating = hostReviews.stream()
            .mapToInt(models.Review::getRating)
            .average()
            .orElse(0.0);

        JPanel statsPanel = new JPanel();
        statsPanel.add(new JLabel("Average Rating: " + String.format("%.1f", avgRating) + " stars"));
        statsPanel.add(new JLabel("Total Reviews: " + hostReviews.size()));

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(hostReviewsList), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> showDashboard());
        btnPanel.add(backBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "HOST_REVIEWS");
        cardLayout.show(mainPanel, "HOST_REVIEWS");
    }

    // ============ MESSAGES PANEL ============
    private void showMessagesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel title = new JLabel("Messages", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        // Create tabs for inbox and sent messages
        JTabbedPane tabbedPane = new JTabbedPane();

        // Inbox Tab
        JPanel inboxPanel = new JPanel(new BorderLayout());
        DefaultListModel<models.Message> inboxModel = new DefaultListModel<>();
        JList<models.Message> inboxList = new JList<>(inboxModel);
        inboxList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean selected, boolean focus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                models.Message m = (models.Message) value;
                models.User sender = userService.getUserById(m.getSenderId());
                String senderName = sender != null ? sender.getUsername() : "Unknown User";
                String readStatus = m.isRead() ? "" : " [NEW]";
                lbl.setText(senderName + " - " + m.getSubject() + readStatus);
                return lbl;
            }
        });

        List<models.Message> inboxMessages = messageService.getUserInbox(currentUser.getUserId());
        inboxModel.clear();
        inboxMessages.forEach(inboxModel::addElement);

        JPanel inboxBtnPanel = new JPanel();
        JButton readBtn = new JButton("Read Message");
        JButton replyBtn = new JButton("Reply");
        JButton deleteBtn = new JButton("Delete");

        readBtn.addActionListener(e -> {
            models.Message selected = inboxList.getSelectedValue();
            if (selected != null) {
                showMessageDialog(selected);
            }
        });

        replyBtn.addActionListener(e -> {
            models.Message selected = inboxList.getSelectedValue();
            if (selected != null) {
                showMessageDialog(selected.getSenderId(), "Re: " + selected.getSubject());
            }
        });

        deleteBtn.addActionListener(e -> {
            models.Message selected = inboxList.getSelectedValue();
            if (selected != null) {
                if (messageService.deleteMessage(selected.getMessageId())) {
                    JOptionPane.showMessageDialog(this, "Message deleted!");
                    showMessagesPanel(); // Refresh
                }
            }
        });

        inboxBtnPanel.add(readBtn);
        inboxBtnPanel.add(replyBtn);
        inboxBtnPanel.add(deleteBtn);

        inboxPanel.add(new JScrollPane(inboxList), BorderLayout.CENTER);
        inboxPanel.add(inboxBtnPanel, BorderLayout.SOUTH);

        // Sent Messages Tab
        JPanel sentPanel = new JPanel(new BorderLayout());
        DefaultListModel<models.Message> sentModel = new DefaultListModel<>();
        JList<models.Message> sentList = new JList<>(sentModel);
        sentList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean selected, boolean focus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                models.Message m = (models.Message) value;
                models.User receiver = userService.getUserById(m.getReceiverId());
                String receiverName = receiver != null ? receiver.getUsername() : "Unknown User";
                lbl.setText("To: " + receiverName + " - " + m.getSubject());
                return lbl;
            }
        });

        List<models.Message> sentMessages = messageService.getUserSentMessages(currentUser.getUserId());
        sentModel.clear();
        sentMessages.forEach(sentModel::addElement);

        sentPanel.add(new JScrollPane(sentList), BorderLayout.CENTER);

        tabbedPane.addTab("Inbox", inboxPanel);
        tabbedPane.addTab("Sent", sentPanel);

        panel.add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton newMessageBtn = new JButton("New Message");
        JButton backBtn = new JButton("Back");

        newMessageBtn.addActionListener(e -> showNewMessageDialog());
        backBtn.addActionListener(e -> showDashboard());

        bottomPanel.add(newMessageBtn);
        bottomPanel.add(backBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "MESSAGES");
        cardLayout.show(mainPanel, "MESSAGES");
    }

    // ============ MESSAGE DIALOG ============
    private void showMessageDialog(String receiverId, String subject) {
        JDialog dialog = new JDialog(this, "New Message", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);

        models.User receiver = userService.getUserById(receiverId);
        String receiverName = receiver != null ? receiver.getUsername() : "Unknown User";

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        
        JLabel toLabel = new JLabel("To: " + receiverName);
        JTextField subjectField = new JTextField(subject);
        JTextArea contentArea = new JTextArea(10, 40);
        contentArea.setLineWrap(true);

        formPanel.add(toLabel);
        formPanel.add(new JLabel("Subject:"));
        formPanel.add(subjectField);
        formPanel.add(new JLabel("Message:"));
        formPanel.add(new JScrollPane(contentArea));

        JPanel btnPanel = new JPanel();
        JButton sendBtn = new JButton("Send Message");
        JButton cancelBtn = new JButton("Cancel");

        sendBtn.addActionListener(e -> {
            String messageSubject = subjectField.getText().trim();
            String content = contentArea.getText().trim();
            
            if (messageSubject.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Subject and message content are required!");
                return;
            }

            models.Message message = messageService.sendMessage(
                currentUser.getUserId(), receiverId, messageSubject, content
            );
            
            if (message != null) {
                JOptionPane.showMessageDialog(dialog, "Message sent successfully!");
                dialog.dispose();
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(sendBtn);
        btnPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showMessageDialog(models.Message message) {
        JDialog dialog = new JDialog(this, "View Message", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);

        models.User sender = userService.getUserById(message.getSenderId());
        String senderName = sender != null ? sender.getUsername() : "Unknown User";

        // Mark message as read
        messageService.markAsRead(message.getMessageId());

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        
        JTextArea messageContent = new JTextArea();
        messageContent.setEditable(false);
        messageContent.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        StringBuilder content = new StringBuilder();
        content.append("From: ").append(senderName).append("\n");
        content.append("Subject: ").append(message.getSubject()).append("\n");
        content.append("Date: ").append(message.getSentAt()).append("\n\n");
        content.append(message.getContent());
        
        messageContent.setText(content.toString());

        JPanel btnPanel = new JPanel();
        JButton replyBtn = new JButton("Reply");
        JButton closeBtn = new JButton("Close");

        replyBtn.addActionListener(e -> {
            dialog.dispose();
            showMessageDialog(message.getSenderId(), "Re: " + message.getSubject());
        });

        closeBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(replyBtn);
        btnPanel.add(closeBtn);

        contentPanel.add(new JScrollPane(messageContent), BorderLayout.CENTER);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showNewMessageDialog() {
        JDialog dialog = new JDialog(this, "New Message", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        
        // Get all users for recipient selection
        List<models.User> allUsers = userService.getAllUsers();
        DefaultComboBoxModel<String> userModel = new DefaultComboBoxModel<>();
        for (models.User user : allUsers) {
            if (!user.getUserId().equals(currentUser.getUserId())) {
                userModel.addElement(user.getUsername() + " (" + user.getRole() + ")");
            }
        }
        
        JComboBox<String> recipientCombo = new JComboBox<>(userModel);
        JTextField subjectField = new JTextField();
        JTextArea contentArea = new JTextArea(10, 40);
        contentArea.setLineWrap(true);

        formPanel.add(new JLabel("To:"));
        formPanel.add(recipientCombo);
        formPanel.add(new JLabel("Subject:"));
        formPanel.add(subjectField);
        formPanel.add(new JLabel("Message:"));
        formPanel.add(new JScrollPane(contentArea));

        JPanel btnPanel = new JPanel();
        JButton sendBtn = new JButton("Send Message");
        JButton cancelBtn = new JButton("Cancel");

        sendBtn.addActionListener(e -> {
            String selected = (String) recipientCombo.getSelectedItem();
            String recipientUsername = selected.split(" ")[0]; // Extract username from display
            models.User recipient = userService.getUserByUsername(recipientUsername);
            
            String messageSubject = subjectField.getText().trim();
            String content = contentArea.getText().trim();
            
            if (messageSubject.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Subject and message content are required!");
                return;
            }

            if (recipient != null) {
                models.Message message = messageService.sendMessage(
                    currentUser.getUserId(), recipient.getUserId(), messageSubject, content
                );
                
                if (message != null) {
                    JOptionPane.showMessageDialog(dialog, "Message sent successfully!");
                    dialog.dispose();
                    showMessagesPanel(); // Refresh messages
                }
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(sendBtn);
        btnPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ============ EVENT HANDLERS ============
    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        
        if (userService.login(username, password)) {
            currentUser = userService.getCurrentUser();
            showDashboard();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!");
        }
    }

    private void handleRegister() {
        String username = registerUsernameField.getText().trim();
        String password = new String(registerPasswordField.getPassword());
        String email = registerEmailField.getText().trim();
        String role = (String) registerRoleComboBox.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
        }
        
        if (userService.registerUser(username, password, email, role)) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please log in.");
            showLoginScreen();
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists!");
        }
    }

    private void handleLogout() {
        currentUser = null;
        userService.logout();
        showLoginScreen();
    }

    // ============ NAVIGATION ============
    private void showLoginScreen() { 
        loginUsernameField.setText("");
        loginPasswordField.setText("");
        cardLayout.show(mainPanel, "LOGIN"); 
    }
    
    private void showRegisterScreen() { 
        registerUsernameField.setText("");
        registerPasswordField.setText("");
        registerEmailField.setText("");
        cardLayout.show(mainPanel, "REGISTER"); 
    }
    
    private void showDashboard() { 
        if (currentUser != null) {
            if ("host".equals(currentUser.getRole())) {
                // Recreate host dashboard to refresh data
                mainPanel.remove(mainPanel.getComponentCount() - 1); // Remove old host panel
                mainPanel.add(createHostDashboardPanel(), "HOST");
                cardLayout.show(mainPanel, "HOST");
            } else {
                // Recreate guest dashboard to refresh data
                mainPanel.remove(mainPanel.getComponentCount() - 1); // Remove old guest panel
                mainPanel.add(createGuestDashboardPanel(), "GUEST");
                cardLayout.show(mainPanel, "GUEST");
            }
        }
    }

    public static void main(String[] args) {
    // Simple approach - just start the application
    javax.swing.SwingUtilities.invokeLater(() -> {
        new MainFrame().setVisible(true);
    });
}
    
    // ============================================================
// SERVICE CLASSES IMPLEMENTATION
// ============================================================

static class UserService {
    private final Map<String, models.User> users = new HashMap<>();
    private models.User currentUser;
    private int userCounter = 1;

    public boolean registerUser(String username, String password, String email, String role) {
        if (users.values().stream().anyMatch(u -> u.getUsername().equals(username))) {
            return false;
        }
        
        String userId = "user_" + userCounter++;
        String passwordHash = Integer.toHexString(password.hashCode()); // Simple hash for demo
        models.User user = new models.User(userId, username, passwordHash, email, role);
        users.put(userId, user);
        return true;
    }

    public boolean login(String username, String password) {
        String passwordHash = Integer.toHexString(password.hashCode());
        models.User user = users.values().stream()
            .filter(u -> u.getUsername().equals(username) && u.getPasswordHash().equals(passwordHash))
            .findFirst()
            .orElse(null);
        
        if (user != null && user.isActive()) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public models.User getCurrentUser() {
        return currentUser;
    }

    public models.User getUserById(String userId) {
        return users.get(userId);
    }

    public models.User getUserByUsername(String username) {
        return users.values().stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }

    public List<models.User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}

static class PropertyService {
    private final Map<String, models.PropertyListing> listings = new HashMap<>();
    private int listingCounter = 1;

    public PropertyService() {
        // Add some sample listings
        addListing("host_1", "Cozy Beach House", "Beautiful beachfront property", "Cape Town", 1200, 4, 2, 2);
        addListing("host_1", "Mountain Cabin", "Secluded mountain retreat", "Drakensberg", 800, 2, 1, 1);
        addListing("host_2", "Luxury Apartment", "Modern apartment in city center", "Johannesburg", 1500, 2, 1, 1);
    }

    public models.PropertyListing addListing(String hostId, String title, String description, 
                                           String location, double pricePerNight, int maxGuests, 
                                           int bedrooms, int bathrooms) {
        String listingId = "listing_" + listingCounter++;
        models.PropertyListing listing = new models.PropertyListing(
            listingId, hostId, title, description, location, pricePerNight, maxGuests, bedrooms, bathrooms
        );
        listings.put(listingId, listing);
        return listing;
    }

    public boolean updateListing(models.PropertyListing listing) {
        if (listings.containsKey(listing.getListingId())) {
            listings.put(listing.getListingId(), listing);
            return true;
        }
        return false;
    }

    public models.PropertyListing getPropertyById(String listingId) {
        return listings.get(listingId);
    }

    public List<models.PropertyListing> getHostListings(String hostId) {
        return listings.values().stream()
            .filter(listing -> listing.getHostId().equals(hostId))
            .toList();
    }

    public List<models.PropertyListing> searchListings(String location, double maxPrice, int guests) {
        return listings.values().stream()
            .filter(listing -> listing.isActive())
            .filter(listing -> location.isEmpty() || listing.getLocation().toLowerCase().contains(location.toLowerCase()))
            .filter(listing -> listing.getPricePerNight() <= maxPrice)
            .filter(listing -> listing.getMaxGuests() >= guests)
            .toList();
    }

    public boolean deleteListing(String listingId, String hostId) {
        models.PropertyListing listing = listings.get(listingId);
        if (listing != null && listing.getHostId().equals(hostId)) {
            listings.remove(listingId);
            return true;
        }
        return false;
    }
}

static class BookingService {
    private final Map<String, models.Booking> bookings = new HashMap<>();
    private int bookingCounter = 1;
    private PropertyService propertyService;

    public BookingService() {
        this.propertyService = new PropertyService();
    }

    public models.Booking createBooking(String listingId, String guestId, LocalDate checkIn, 
                                      LocalDate checkOut, int guests, double totalPrice) {
        String bookingId = "booking_" + bookingCounter++;
        models.Booking booking = new models.Booking(bookingId, listingId, guestId, checkIn, checkOut, guests, totalPrice);
        bookings.put(bookingId, booking);
        
        // Add booked dates to property
        models.PropertyListing property = propertyService.getPropertyById(listingId);
        if (property != null) {
            LocalDate current = checkIn;
            while (!current.isAfter(checkOut.minusDays(1))) {
                property.addBookedDate(current);
                current = current.plusDays(1);
            }
        }
        
        return booking;
    }

    public boolean cancelBooking(String bookingId) {
        models.Booking booking = bookings.get(bookingId);
        if (booking != null && booking.canBeCancelled()) {
            booking.setStatus("cancelled");
            
            // Remove booked dates from property
            models.PropertyListing property = propertyService.getPropertyById(booking.getListingId());
            if (property != null) {
                LocalDate current = booking.getCheckInDate();
                while (!current.isAfter(booking.getCheckOutDate().minusDays(1))) {
                    property.removeBookedDate(current);
                    current = current.plusDays(1);
                }
            }
            return true;
        }
        return false;
    }

    public boolean confirmBooking(String bookingId) {
        models.Booking booking = bookings.get(bookingId);
        if (booking != null && "pending".equals(booking.getStatus())) {
            booking.setStatus("confirmed");
            return true;
        }
        return false;
    }

    public boolean rejectBooking(String bookingId) {
        models.Booking booking = bookings.get(bookingId);
        if (booking != null && "pending".equals(booking.getStatus())) {
            booking.setStatus("rejected");
            
            // Remove booked dates from property
            models.PropertyListing property = propertyService.getPropertyById(booking.getListingId());
            if (property != null) {
                LocalDate current = booking.getCheckInDate();
                while (!current.isAfter(booking.getCheckOutDate().minusDays(1))) {
                    property.removeBookedDate(current);
                    current = current.plusDays(1);
                }
            }
            return true;
        }
        return false;
    }

    public List<models.Booking> getUserBookings(String userId) {
        return bookings.values().stream()
            .filter(booking -> booking.getGuestId().equals(userId))
            .toList();
    }

    public List<models.Booking> getHostBookings(String hostId) {
        return bookings.values().stream()
            .filter(booking -> {
                models.PropertyListing property = propertyService.getPropertyById(booking.getListingId());
                return property != null && property.getHostId().equals(hostId);
            })
            .toList();
    }

    public List<models.Booking> getPendingBookingsForHost(String hostId) {
        return getHostBookings(hostId).stream()
            .filter(booking -> "pending".equals(booking.getStatus()))
            .toList();
    }

    public List<models.Booking> getUpcomingBookings(String userId) {
        return getUserBookings(userId).stream()
            .filter(booking -> booking.isUpcoming())
            .toList();
    }

    public models.Booking getBookingById(String bookingId) {
        return bookings.get(bookingId);
    }
}

static class MessageService {
    private final Map<String, models.Message> messages = new HashMap<>();
    private int messageCounter = 1;

    public models.Message sendMessage(String senderId, String receiverId, String subject, String content) {
        String messageId = "msg_" + messageCounter++;
        models.Message message = new models.Message(messageId, senderId, receiverId, subject, content);
        messages.put(messageId, message);
        return message;
    }

    public List<models.Message> getUserInbox(String userId) {
        return messages.values().stream()
            .filter(message -> message.getReceiverId().equals(userId))
            .sorted((m1, m2) -> m2.getSentAt().compareTo(m1.getSentAt()))
            .toList();
    }

    public List<models.Message> getUserSentMessages(String userId) {
        return messages.values().stream()
            .filter(message -> message.getSenderId().equals(userId))
            .sorted((m1, m2) -> m2.getSentAt().compareTo(m1.getSentAt()))
            .toList();
    }

    public boolean markAsRead(String messageId) {
        models.Message message = messages.get(messageId);
        if (message != null) {
            message.markAsRead();
            return true;
        }
        return false;
    }

    public boolean deleteMessage(String messageId) {
        return messages.remove(messageId) != null;
    }

    public models.Message getMessageById(String messageId) {
        return messages.get(messageId);
    }
}

static class ReviewService {
    private final Map<String, models.Review> reviews = new HashMap<>();
    private int reviewCounter = 1;

    public models.Review addReview(String bookingId, String reviewerId, String revieweeId, 
                                 int rating, String comment, String type) {
        String reviewId = "review_" + reviewCounter++;
        models.Review review = new models.Review(reviewId, bookingId, reviewerId, revieweeId, rating, comment, type);
        reviews.put(reviewId, review);
        return review;
    }

    public List<models.Review> getUserReviews(String userId) {
        return reviews.values().stream()
            .filter(review -> review.getReviewerId().equals(userId))
            .toList();
    }

    public List<models.Review> getHostReviews(String hostId) {
        return reviews.values().stream()
            .filter(review -> review.getRevieweeId().equals(hostId) && "property".equals(review.getType()))
            .toList();
    }

    public List<models.Review> getPropertyReviews(String propertyId) {
        return reviews.values().stream()
            .filter(review -> review.getRevieweeId().equals(propertyId))
            .toList();
    }

    public double getAverageRating(String revieweeId) {
        return reviews.values().stream()
            .filter(review -> review.getRevieweeId().equals(revieweeId))
            .mapToInt(models.Review::getRating)
            .average()
            .orElse(0.0);
    }
}
}