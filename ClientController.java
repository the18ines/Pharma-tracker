package com.example.phartracfabmod;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientController {

    @FXML
    private Button btnAfficher;

    @FXML
    private Button btnRechercher;

    @FXML
    private DatePicker dateExpiration;

    @FXML
    private TextField tDateVente;

    @FXML
    private TextField tHeureVente;

    @FXML
    private TextField tQtite;

    @FXML
    private DatePicker dateFabrication;

    @FXML
    private TextArea txtComposition;

    @FXML
    private TextField txtFormePharmaceutique;

    @FXML
    private TextField txtNomProduit;

    @FXML
    private TextField txtNumeroLotRecherche;

    @FXML
    private TextField tAdresseClient;

    @FXML
    private TextField tContactClient;

    @FXML
    private TextField tDateRecpDist;

    @FXML
    private TextField tDateRecpPhar;

    @FXML
    private TextField tLieuRecpDist;

    @FXML
    private TextField tLieuRecpPhar;

    @FXML
    private TextField tNomClient;

    @FXML
    private TextField tNomDist;

    @FXML
    private TextField tNomPhar;

    @FXML
    private TextField tNumeroLot;

    @FXML
    private TextField tPrenomClient;

    @FXML
    private TextField tContactDistrib;

    @FXML
    private TextField tContactPharma;

    @FXML
    private void onAffichageDetails(ActionEvent event) {
        redirectToPage(event, "DetailsProduit.fxml", 1188, 730);
        //tNumeroLot.setText(txtNumeroLotRecherche.getText());
    }

    @FXML
    private void onAfficher(ActionEvent event){
        try(Connection conn = DBConnection.getConnection()){
            String query1 = "SELECT c.nom, c.prenom, c.telephone, c.adresse, v.dateVente, v.heureVente, v.quantite, p.numeroLot FROM vente v " +
                    "JOIN client c on c.idClient = v.idClient " +
                    "JOIN produit p on p.numeroLot = v.numeroLot ";
            String query2 = "SELECT r.date_reception, r.lieu_reception, d.nomDistrib, d.telephone " +
                    "FROM reception r " +
                    "JOIN distributeur d on d.idDistrib = r.idDistrib " +
                    "JOIN produit p on p.numeroLot = r.numero_lot " +
                    "WHERE r.numero_lot = ?";
            String query3 = "SELECT h.date_reception, h.lieu_reception, p.nomPharma, p.telephone " +
                    "FROM historiquePharma h " +
                    "JOIN pharmacien p on p.idPharma = h.idPharma";

            PreparedStatement st1 = conn.prepareStatement(query1);
            PreparedStatement st2 = conn.prepareStatement(query2);
            st2.setString(1, tNumeroLot.getText());
            PreparedStatement st3 = conn.prepareStatement(query3);

            ResultSet rs1 = st1.executeQuery();
            ResultSet rs2 = st2.executeQuery();
            ResultSet rs3 = st3.executeQuery();

            if(rs1.next() && rs2.next() && rs3.next()){
                tNumeroLot.setText(rs1.getString("numeroLot"));
                tDateRecpDist.setText(rs2.getString("date_reception"));
                tLieuRecpDist.setText(rs2.getString("lieu_reception"));
                tNomClient.setText(rs1.getString("nom"));
                tPrenomClient.setText(rs1.getString("prenom"));
                tContactClient.setText(rs1.getString("telephone"));
                tAdresseClient.setText(rs1.getString("adresse"));
                tDateVente.setText(rs1.getString("dateVente"));
                tHeureVente.setText(rs1.getString("heureVente"));
                tQtite.setText(rs1.getString("quantite"));
                tNomDist.setText(rs2.getString("nomDistrib"));
                tContactDistrib.setText(rs2.getString("telephone"));
                tDateRecpPhar.setText(rs3.getString("date_reception"));
                tLieuRecpPhar.setText(rs3.getString("lieu_reception"));
                tNomPhar.setText(rs3.getString("nomPharma"));
                tContactPharma.setText(rs3.getString("telephone"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onDeconnexion(ActionEvent event){
        redirectToPage(event, "Authentification.fxml", 470, 498);
    }

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

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
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

}