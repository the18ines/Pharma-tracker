package com.example.phartracfabmod;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class InscriptionController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    // Méthode pour gérer l'inscription
    @FXML
    private void handleSignup(ActionEvent event) {
        String name = nameField.getText();
        String firstName = firstNameField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (name.isEmpty() || firstName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || role == null) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        String url = "jdbc:mysql://localhost:3306/pharmaTrack"; // Adapter à votre configuration
        String user = "root";
        String pass = "";

        // Charger le driver MySQL
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Chargement du driver MySQL
        } catch (ClassNotFoundException e) {
            showAlert(AlertType.ERROR, "Erreur", "Pilote MySQL non trouvé.\n" + e.getMessage());
            return; // Arrêter l'exécution si le driver n'est pas trouvé
        }

        String query = "INSERT INTO utilisateur (nom, prenom, email, nomUtili, mot_de_passe, role, date_inscription) VALUES (?, ?, ?, ?, ?, ?, NOW())";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, username);
            preparedStatement.setString(5, password); 
            preparedStatement.setString(6, role);

            preparedStatement.executeUpdate();
            showAlert(AlertType.INFORMATION, "Succès", "Utilisateur enregistré avec succès !");

            // Redirection vers la page d'authentification après l'inscription
            goToLogin(event);

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible d'enregistrer l'utilisateur.\n" + e.getMessage());
        }
    }

    // Méthode pour rediriger vers la page Authentification.fxml
    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("Authentification.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger la page d'authentification.\n" + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}