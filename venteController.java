package com.example.phartracfabmod;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class venteController {

    @FXML
    private StackPane DistributeurController;

    @FXML
    private TextField txtNomProduit;

    @FXML
    private TextField txtNumeroLotRecherche;

    @FXML
    private TextArea txtComposition;

    @FXML
    private TextField txtFormePharmaceutique;

    @FXML
    private Button btnRechercher;

    @FXML
    private DatePicker dateFabrication;

    @FXML
    private DatePicker dateExpiration;

    @FXML
    private Button btnEnregistrer;

    @FXML
    private TextField txtHeurevente;

    @FXML
    private TextField txtLieuVente;

    @FXML
    private TextField txtQuantiteVendue;

    @FXML
    private DatePicker txtDateVente;

    @FXML
    private TextField txttelephone;

    @FXML
    private TextField txtadresse;

    @FXML
    private TextField txtprenom;

    @FXML
    private TextField txtnom;


	String url = "jdbc:mysql://localhost:3306/pharmatrack";
	String utilisateur = "root";
	String mdp = "";

	@FXML
	private void onRechercher() {
		String numeroLot = txtNumeroLotRecherche.getText();
		if (numeroLot.isEmpty()) {
			showAlert("Erreur", "Veuillez entrer un numéro de lot.", Alert.AlertType.ERROR);
			return;
		}

		try {
			Connection con = DriverManager.getConnection(url, utilisateur, mdp);
			String query = "SELECT * FROM produit WHERE numeroLot = ?";
			PreparedStatement stmt = con.prepareStatement(query);
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
			//e.printStackTrace();
			//showAlert("Erreur", "Une erreur est survenue lors de la recherche.", Alert.AlertType.ERROR);
		}
	}
	    @FXML
	    private void handleEnregistrer() {
	        String numeroLot = txtNumeroLotRecherche.getText();
	        String lieuVente = txtLieuVente.getText();
	        String heureVente = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	        String quantiteVendue = txtQuantiteVendue.getText();
	        LocalDate dateVenteValue = LocalDate.now();
	        String nom = txtnom.getText();
	        String prenom = txtprenom.getText();
	        String adresse = txtadresse.getText();
	        String telephone = txttelephone.getText();

	        if (nom.isEmpty() || prenom.isEmpty() || adresse.isEmpty() || lieuVente.isEmpty() || heureVente.isEmpty() || quantiteVendue.isEmpty() || dateVenteValue == null) {
	            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
	            return;
	        }

	        try{
				Connection con = DriverManager.getConnection(url, utilisateur, mdp);
	            String query = "INSERT INTO vente (numeroLot, dateVente, heureVente, quantite, idClient) VALUES (?, ?, ?, ?, ?)";
	            PreparedStatement stmt = con.prepareStatement(query);
				System.out.println("requete executee");

				Random random = new Random();
				int idClient = random.nextInt(Integer.MAX_VALUE); // Génère un entier aléatoire entre 0 et Integer.MAX_VALUE-1

	            stmt.setString(1, numeroLot);
	            stmt.setDate(2, java.sql.Date.valueOf(dateVenteValue));
	            stmt.setString(3, heureVente);
	            stmt.setString(4, quantiteVendue);
				stmt.setString(5, String.valueOf(idClient));

				String query2 = "INSERT INTO client (nom, prenom, adresse, telephone) VALUES (?, ?, ?, ?)";
				PreparedStatement stmt2 = con.prepareStatement(query2);

				stmt2.setString(1, nom);
				stmt2.setString(2, prenom);
				stmt2.setString(3, adresse);
				stmt2.setString(4, telephone);

				stmt2.executeUpdate();
	          

	            stmt.executeUpdate();
	            showAlert("Succès", "Vente enregistrée avec succès.", Alert.AlertType.INFORMATION);
	        } catch (Exception e) {
	            e.printStackTrace();
	            showAlert("Erreur", "Une erreur est survenue lors de l'enregistrement."+e.getMessage(), Alert.AlertType.ERROR);
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

