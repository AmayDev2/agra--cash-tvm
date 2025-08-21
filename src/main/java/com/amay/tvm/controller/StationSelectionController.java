package com.amay.tvm.controller;

import com.amay.tom.ViewFactory;
import com.amay.tom.agent.Agent;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.model.Station;
import com.amay.tom.model.TicketType;
import com.amay.tom.repository.FareLine3;
import com.amay.tom.repository.StationData;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;  // Changed from Button to ToggleButton
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.tinylog.Logger;

import java.io.IOException;

public class StationSelectionController {

    // Journey Selection Toggle Buttons
    @FXML private ToggleButton journey_01;  // Changed to ToggleButton
    @FXML private ToggleButton journey_02;  // Changed to ToggleButton

    // Passenger Count Toggle Buttons
    @FXML private ToggleButton pass_1;      // Changed to ToggleButton
    @FXML private ToggleButton pass_2;      // Changed to ToggleButton
    @FXML private ToggleButton pass_3;      // Changed to ToggleButton
    @FXML private ToggleButton pass_4;      // Changed to ToggleButton
    @FXML private ToggleButton pass_5;      // Changed to ToggleButton
    @FXML private ToggleButton pass_6;      // Changed to ToggleButton

    // Labels
    @FXML private Label destination;
    @FXML private Label calculatedFare;

    // Core components
    private BorderPane borderPane;
    private StackPane stackPane;
    private Agent agent;
    private StationData stationData;
    private Station selectedDestination;
    private TicketType ticketType;
    private int quantity;
    private int fare = 0;

    // Toggle Groups for mutual exclusion
    private ToggleGroup journeyToggleGroup;
    private ToggleGroup passengerCountToggleGroup;

    /**
     * Constructor
     */
    public StationSelectionController(BorderPane borderPane, StackPane pane, Agent agent,
                                      StationData stationData, TicketType ticketType,Station selectedDestination) {
        this.borderPane = borderPane;
        this.stackPane = pane;
        this.agent = agent;
        this.stationData = stationData;
        this.selectedDestination = selectedDestination;
        this.ticketType=ticketType;
    }

    /**
     * Initialize UI components and setup ToggleGroups
     */
    @FXML
    public void initialize() {
        try {
            // Initialize default values
            this.quantity = 0;
            this.fare = 0;

            // Set destination label
            if (selectedDestination != null) {
                this.destination.setText(selectedDestination.getStationName());
            }

            // Setup ToggleGroups
            setupToggleGroups();

            Logger.info("StationSelectionController initialized successfully");

        } catch (Exception e) {
            Logger.error("Error initializing StationSelectionController: {}", e.getMessage());
        }
    }

    /**
     * Setup ToggleGroups for journey and passenger selection
     */
    private void setupToggleGroups() {
        try {
            // Journey Selection ToggleGroup
            journeyToggleGroup = new ToggleGroup();
            if (journey_01 != null) journey_01.setToggleGroup(journeyToggleGroup);
            if (journey_02 != null) journey_02.setToggleGroup(journeyToggleGroup);

            // Passenger Count ToggleGroup
            passengerCountToggleGroup = new ToggleGroup();
            if (pass_1 != null) pass_1.setToggleGroup(passengerCountToggleGroup);
            if (pass_2 != null) pass_2.setToggleGroup(passengerCountToggleGroup);
            if (pass_3 != null) pass_3.setToggleGroup(passengerCountToggleGroup);
            if (pass_4 != null) pass_4.setToggleGroup(passengerCountToggleGroup);
            if (pass_5 != null) pass_5.setToggleGroup(passengerCountToggleGroup);
            if (pass_6 != null) pass_6.setToggleGroup(passengerCountToggleGroup);

            // Add listeners for selection changes
            journeyToggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
                if (newToggle != null) {
                    ToggleButton selectedButton = (ToggleButton) newToggle;
                    Logger.info("Journey type changed to: {}", selectedButton.getId());
                }
            });

            passengerCountToggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
                if (newToggle != null) {
                    ToggleButton selectedButton = (ToggleButton) newToggle;
                    Logger.info("Passenger count changed to: {}", selectedButton.getId());
                }
            });

            Logger.info("ToggleGroups setup completed");

        } catch (Exception e) {
            Logger.error("Error setting up toggle groups: {}", e.getMessage());
        }
    }

    /**
     * Handle journey type selection
     */
    @FXML
    private void selectJourney(ActionEvent actionEvent) {
        try {
            ToggleButton clickedButton = (ToggleButton) actionEvent.getSource();

            if (clickedButton.isSelected()) {
                String buttonId = clickedButton.getId();
                this.ticketType = TicketType.getTicket(buttonId.split("_")[1]);

                Logger.info("Journey type selected: {} ({})", this.ticketType, buttonId);
                calculateFare();
            } else {
                // If deselected, clear ticket type
                this.ticketType = null;
                calculateFare();
            }

        } catch (Exception e) {
            Logger.error("Error handling journey selection: {}", e.getMessage());
        } finally {
            actionEvent.consume();
        }
    }

    /**
     * Handle passenger count selection
     */
    @FXML
    public void selectPass(ActionEvent actionEvent) {
        try {
            ToggleButton clickedButton = (ToggleButton) actionEvent.getSource();

            if (clickedButton.isSelected()) {
                String buttonId = clickedButton.getId().split("_")[1];
                this.quantity = Integer.parseInt(buttonId);

                Logger.info("Passenger count selected: {}", this.quantity);
                calculateFare();
            } else {
                // If deselected, clear quantity
                this.quantity = 0;
                calculateFare();
            }

        } catch (Exception e) {
            Logger.error("Error handling passenger selection: {}", e.getMessage());
        } finally {
            actionEvent.consume();
        }
    }

    /**
     * Calculate fare based on selections
     */
    private void calculateFare() {
        try {
            fare = 0;

            if (this.selectedDestination != null && this.ticketType != null && this.quantity > 0) {
                int sourceId = Integer.parseInt(SystemConfig.getInstance().getCurrentStation().getStationId()) - 1;
                int destId = Integer.parseInt(this.selectedDestination.getStationId()) - 1;

                int baseFare = FareLine3.distanceMatrix[sourceId][destId];
                int multiplier = this.ticketType.equals(TicketType.SINGLE) ? 1 : 2;

                fare = baseFare * this.quantity * multiplier;

                Logger.info("Fare calculated: Base={}, Quantity={}, Multiplier={}, Total={}",
                        baseFare, this.quantity, multiplier, fare);
            }

            // Update UI
            Platform.runLater(() -> {
                calculatedFare.setText("Total Fare : ₹ " + fare);
            });

        } catch (Exception e) {
            Logger.error("Error calculating fare: {}", e.getMessage());
            fare = 0;
            Platform.runLater(() -> {
                calculatedFare.setText("Total Fare : ₹ 0");
            });
        }
    }

    /**
     * Get selected journey type from ToggleGroup
     */
    private TicketType getSelectedJourneyType() {
        if (journeyToggleGroup.getSelectedToggle() == null) {
            return null;
        }

        ToggleButton selectedButton = (ToggleButton) journeyToggleGroup.getSelectedToggle();
        String buttonId = selectedButton.getId();
        return TicketType.getTicket(buttonId.split("_")[1]);
    }

    /**
     * Get selected passenger count from ToggleGroup
     */
    private int getSelectedPassengerCount() {
        if (passengerCountToggleGroup.getSelectedToggle() == null) {
            return 0;
        }

        ToggleButton selectedButton = (ToggleButton) passengerCountToggleGroup.getSelectedToggle();
        String buttonId = selectedButton.getId().split("_")[1];
        return Integer.parseInt(buttonId);
    }

    /**
     * Navigate back to home page
     */
    @FXML
    public void navigateToHomePage(ActionEvent actionEvent) {
        try {
            Logger.info("Navigating back to home page");

            // Clear selections when going back
            clearAllSelections();

            int count = this.stackPane.getChildren().size();
            if (count > 0) {
                this.stackPane.getChildren().remove(count - 1);
            }

        } catch (Exception e) {
            Logger.error("Error navigating to home page: {}", e.getMessage());
        } finally {
            actionEvent.consume();
        }
    }

    /**
     * Confirm selection and proceed to payment
     */
    @FXML
    public void confirmSelection(ActionEvent actionEvent) {
        try {
            // Get current selections from ToggleGroups
            TicketType currentTicketType = getSelectedJourneyType();
            int currentQuantity = getSelectedPassengerCount();

            // Update instance variables if they changed
            if (this.ticketType != currentTicketType) {
                this.ticketType = currentTicketType;
                calculateFare();
            }

            if (this.quantity != currentQuantity) {
                this.quantity = currentQuantity;
                calculateFare();
            }

            // Validation
            if (fare <= 0 || this.selectedDestination == null || this.ticketType == null || this.quantity <= 0) {
                Logger.error("Invalid selection - Destination: {}, TicketType: {}, Quantity: {}, Fare: {}",
                        this.selectedDestination, this.ticketType, this.quantity, fare);
                // TODO: Show user error message
                return;
            }

            Logger.info("Selection confirmed - Destination: {}, TicketType: {}, Quantity: {}, Fare: {}",
                    this.selectedDestination.getStationName(), this.ticketType, this.quantity, fare);

            // Navigate to payment screen
            FXMLLoader fxmlLoader = ViewFactory.getPaymentSummeryView();
            fxmlLoader.setControllerFactory(param -> new PaymentController(
                    this.stackPane, this.borderPane, this.agent, this.stationData,
                    this.selectedDestination, this.ticketType, this.quantity, fare));

            this.stackPane.getChildren().add(fxmlLoader.load());
            this.borderPane.setCenter(this.stackPane);

        } catch (RuntimeException | IOException e) {
            Logger.error("Error confirming selection: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            actionEvent.consume();
        }
    }

    /**
     * Clear all selections (utility method)
     */
    public void clearAllSelections() {
        try {
            if (journeyToggleGroup != null) {
                journeyToggleGroup.selectToggle(null);
            }
            if (passengerCountToggleGroup != null) {
                passengerCountToggleGroup.selectToggle(null);
            }

            this.ticketType = null;
            this.quantity = 0;
            this.fare = 0;

            Platform.runLater(() -> {
                calculatedFare.setText("Total Fare : ₹ 0");
            });

            Logger.info("All selections cleared");

        } catch (Exception e) {
            Logger.error("Error clearing selections: {}", e.getMessage());
        }
    }

    /**
     * Programmatically select journey type
     */
    public void selectJourneyType(TicketType type) {
        try {
            switch (type) {
                case SINGLE -> {
                    if (journey_01 != null) journey_01.setSelected(true);
                }
                case RETURN -> {
                    if (journey_02 != null) journey_02.setSelected(true);
                }
            }
            this.ticketType = type;
            calculateFare();
        } catch (Exception e) {
            Logger.error("Error programmatically selecting journey type: {}", e.getMessage());
        }
    }

    /**
     * Programmatically select passenger count
     */
    public void selectPassengerCount(int count) {
        try {
            ToggleButton targetButton = switch (count) {
                case 1 -> pass_1;
                case 2 -> pass_2;
                case 3 -> pass_3;
                case 4 -> pass_4;
                case 5 -> pass_5;
                case 6 -> pass_6;
                default -> null;
            };

            if (targetButton != null) {
                targetButton.setSelected(true);
                this.quantity = count;
                calculateFare();
            }
        } catch (Exception e) {
            Logger.error("Error programmatically selecting passenger count: {}", e.getMessage());
        }
    }
}
