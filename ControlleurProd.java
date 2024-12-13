package com.example.phartracfabmod;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ControlleurProd implements Initializable {

    @FXML
    private Button BtAjouter;

    @FXML
    private Button BtModifier;

    @FXML
    private ImageView imgQrCode;

    @FXML
    private TableColumn<Produit, String> colComposition;

    @FXML
    private TableColumn<Produit, LocalDate> colDateExpiration;

    @FXML
    private TableColumn<Produit, LocalDate> colDateFabrication;

    @FXML
    private TableColumn<Produit, String> colFormePharmaceutique;

    @FXML
    private TableColumn<Produit, String> colNomProd;

    @FXML
    private TableColumn<Produit, String> colNumeroLot;

    @FXML
    private TextArea compositionTextArea;

    @FXML
    private Button BtnStatistiques;

    @FXML
    private DatePicker dateExpirationPicker;

    @FXML
    private DatePicker dateFabricationPicker;

    @FXML
    private ComboBox<?> formePharmaceutiqueComboBox;

    @FXML
    private TableView<Produit> produitTableView;

    @FXML
    private TextField tNomProd;

    @FXML
    private TextField tNumeroLot;

    @FXML
    private TextField tRechercher;

    private ObservableList<Produit> lesProduits = FXCollections.observableArrayList();
    private ProduitDAO produitDAO;

    @FXML
    void onAjouterProduit(ActionEvent event) {

        Produit p = new Produit(tNumeroLot.getText(), tNomProd.getText(), dateFabricationPicker.getValue(), dateExpirationPicker.getValue(), compositionTextArea.getText(), (String) formePharmaceutiqueComboBox.getValue());
        produitDAO.ajouterProduit(p);
        lesProduits.add(p);
    }

    @FXML
    void onGenererQRCode(ActionEvent event) {
        Produit ps = produitTableView.getSelectionModel().getSelectedItem();

        if(ps != null){
            String nP = ps.getNomProd()+"QR-Code.png";
            QRCodeGenerateur qrCode = new QRCodeGenerateur();
            BufferedImage qrCodeImage = qrCode.genererQRCode(ps, nP)
                    ;

            if(qrCodeImage != null){
                WritableImage image = SwingFXUtils.toFXImage(qrCodeImage, null);
                imgQrCode.setImage(image);
            }
        }
    }

    @FXML
    void onModifierProduit(ActionEvent event) {

        Produit ps = produitTableView.getSelectionModel().getSelectedItem();
        if(ps != null){
            ps.setNumeroLot(tNumeroLot.getText());
            ps.setNomProd(tNomProd.getText());
            ps.setDateFab(dateFabricationPicker.getValue());
            ps.setDateExp(dateExpirationPicker.getValue());
            ps.setComposition(compositionTextArea.getText());
            ps.setFormePharma((String) formePharmaceutiqueComboBox.getValue());

            produitDAO.modifierProduit(ps);
            produitTableView.refresh();
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

    @FXML
    private void afficherStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StatFab.fxml"));
            Parent root = loader.load();

            // Ouvrir une nouvelle fenêtre avec le graphique
            Stage stage = new Stage();
            stage.setTitle("Statistiques des Produits");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colNumeroLot.setCellValueFactory(new PropertyValueFactory<>("numeroLot"));
        colNomProd.setCellValueFactory(new PropertyValueFactory<>("nomProd"));
        colDateFabrication.setCellValueFactory(new PropertyValueFactory<>("dateFab"));
        colDateExpiration.setCellValueFactory(new PropertyValueFactory<>("dateExp"));
        colComposition.setCellValueFactory(new PropertyValueFactory<>("Composition"));
        colFormePharmaceutique.setCellValueFactory(new PropertyValueFactory<>("formePharma"));

        produitDAO = new ProduitDAO();
        ObservableList<Produit> produits = FXCollections.observableArrayList(produitDAO.getLesProduits());
        lesProduits.addAll(produits);

        produitTableView.setItems(lesProduits);

        produitTableView.setOnMouseClicked(new EventHandler<MouseEvent >() {
            @Override
            public void handle(javafx.scene.input.MouseEvent mouseEvent) {
                Produit ps = produitTableView.getSelectionModel().getSelectedItem();
                if (ps != null){
                    tNumeroLot.setText(ps.getNumeroLot());
                    tNomProd.setText(ps.getNomProd());
                    dateFabricationPicker.setValue(ps.getDateFab());
                    dateExpirationPicker.setValue(ps.getDateExp());
                    compositionTextArea.setText(ps.getComposition());
                }
            }
        });

    }
}
