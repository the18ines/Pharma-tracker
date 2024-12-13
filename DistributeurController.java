package com.example.phartracfabmod;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DistributeurController {

  	@FXML
	private TextField txtNumeroLot, txtNomProduit, txtNumeroLotRecherche, txtFormePharmaceutique,
	txtQuantiteReception, txtLieuReception, txtHeureReception;
	@FXML
	private TextArea txtComposition;
	@FXML
	private DatePicker dateFabrication, dateExpiration, dateReception;

	@FXML
	private TextField tidDistrib;

	@FXML
	private Button btnRechercher;

	@FXML
	private Button btnEnregistrer;

	@FXML
	private void onRechercher() {
		String numeroLot = txtNumeroLotRecherche.getText();
		if (numeroLot.isEmpty()) {
			showAlert("Erreur", "Veuillez entrer un numéro de lot.", Alert.AlertType.ERROR);
			return;
		}

		try (Connection conn = DBConnection.getConnection()) {
			String query = "SELECT * FROM produit WHERE numeroLot = ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, numeroLot);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				txtNomProduit.setText(rs.getString("nomProd"));
				dateFabrication.setValue(rs.getDate("dateFab").toLocalDate());
				dateExpiration.setValue(rs.getDate("dateExp").toLocalDate());
				txtComposition.setText(rs.getString("composition"));
				txtFormePharmaceutique.setText(rs.getString("formePharma"));

			} else {
				showAlert("Information", "Aucun produit trouvé pour ce numéro de lot.", Alert.AlertType.INFORMATION);
			}
		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Erreur", "Une erreur est survenue lors de la recherche.", Alert.AlertType.ERROR);
		}
	}

	@FXML
	private void onEnregistrer() {
		String numeroLot = txtNumeroLotRecherche.getText();
		String lieuReception = txtLieuReception.getText();
		String heureReception = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		String quantiteReception = txtQuantiteReception.getText();
		LocalDate dateReceptionValue = LocalDate.now();
		int idDistrib = Integer.parseInt(tidDistrib.getText());

		if (lieuReception.isEmpty() || heureReception.isEmpty() || quantiteReception.isEmpty() || dateReceptionValue == null) {
			showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
			return;
		}

		try (Connection conn = DBConnection.getConnection()) {
			String query = "INSERT INTO reception (numero_lot, date_reception, heure_reception, lieu_reception, quantite_recue, idDistrib) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, numeroLot);
			stmt.setDate(2, java.sql.Date.valueOf(dateReceptionValue));
			stmt.setString(3, heureReception);
			stmt.setString(4, lieuReception);
			stmt.setString(5, quantiteReception);
			stmt.setInt(6, idDistrib);

			stmt.executeUpdate();
			showAlert("Succès", "Réception enregistrée avec succès.", Alert.AlertType.INFORMATION);
		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Erreur", "Une erreur est survenue lors de l'enregistrement.", Alert.AlertType.ERROR);
		}
	}

	@FXML
	public void onDeconnexion(ActionEvent event){
		redirectToPage(event, "Authentification.fxml", 470, 498);
	}

	private void redirectToPage(ActionEvent event, String fxmlPath, int width, int height) {
		try {
			javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
			javafx.scene.Scene scene = new Scene(loader.load(), 1188, 730);
			javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
			stage.setScene(scene);
		} catch (Exception e) {
			showAlert( "Erreur", "Impossible de charger la page.\n" + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private void showAlert(String title, String content, Alert.AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setContentText(content);
		alert.showAndWait();
	}
}

