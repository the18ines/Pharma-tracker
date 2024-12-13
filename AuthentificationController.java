package com.example.phartracfabmod;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthentificationController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    // Méthode pour gérer la connexion
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Champs vides", "Veuillez entrer votre nom d'utilisateur et mot de passe.");
            return;
        }

        String url = "jdbc:mysql://localhost:3306/pharmaTrack"; // Adapter à votre configuration
        String user = "root";
        String pass = "";

        // Charger le driver MySQL
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Charger le driver MySQL
        } catch (ClassNotFoundException e) {
            showAlert(AlertType.ERROR, "Erreur", "Pilote MySQL non trouvé.\n" + e.getMessage());
            return; // Arrêter l'exécution si le driver n'est pas trouvé
        }

        String query = "SELECT * FROM utilisateur WHERE nomUtili = ? AND mot_de_passe = ?";

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);  // Vous pouvez ajouter un chiffrement ici

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");

                // Rediriger en fonction du rôle
                switch (role) {
                    case "fabricant":
                        redirectToPage(event, "EnregistrerProduit.fxml", 1188, 730);
                        break;
                    case "distributeur":
                        redirectToPage(event, "Distributeur.fxml", 1188, 730);
                        break;
                    case "pharmacien":
                        redirectToPage(event, "vente.fxml", 1188, 730);
                        break;
                    case "client":
                        redirectToPage(event, "Client.fxml", 1188, 730);
                        break;
                    default:
                        showAlert(AlertType.WARNING, "Rôle inconnu", "Le rôle de l'utilisateur est inconnu.");
                }
            } else {
                showAlert(AlertType.ERROR, "Authentification échouée", "Nom d'utilisateur ou mot de passe incorrect.");
            }

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible de vérifier l'utilisateur.\n" + e.getMessage());
        }
    }

    // Méthode pour rediriger vers une page spécifique
    private void redirectToPage(ActionEvent event, String fxmlPath, int width, int height) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Scene scene = new Scene(loader.load(), 1188, 730);
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger la page.\n" + e.getMessage());
        }
    }

    // Méthode pour rediriger vers la page d'inscription
    @FXML
    private void goToSignupPage(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("Inscription.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger la page d'inscription.\n" + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}