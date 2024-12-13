package com.example.phartracfabmod;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StatistiquesController {

	
	 @FXML private BarChart<String, Number> barChart;
	    @FXML private CategoryAxis xAxis;
	    @FXML private NumberAxis yAxis;

	    public void initialize() {
	        // Initialisation de l'axe X et Y
	        xAxis.setLabel("Nom du produit");
	        yAxis.setLabel("Nombre de Fabrications");
	        
	        // Appel de la méthode pour charger les données
	        chargerStatistiques();
	    }

	    private void chargerStatistiques() {
	        ObservableList<XYChart.Series<String, Number>> data = FXCollections.observableArrayList();
	        XYChart.Series<String, Number> series = new XYChart.Series<>();
	        series.setName("Fabrications");

	        // Connexion à la base de données et récupération des statistiques
	        try (Connection conn = DBConnection.getConnection()) {
	            String query = "SELECT nomProd, COUNT(*) AS nombre_fabrications FROM produit GROUP BY nomProd ORDER BY nombre_fabrications DESC";
	            PreparedStatement stmt = conn.prepareStatement(query);
	            ResultSet rs = stmt.executeQuery();

	            while (rs.next()) {
	                String nom = rs.getString("nomProd");
					//String compo = rs.getString("composition");
					//String forme = rs.getString("formePharma");
	                int nombreFabrications = rs.getInt("nombre_fabrications");
	                // Ajout des données au graphique
	                series.getData().add(new XYChart.Data<>(nom, nombreFabrications));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        // Ajout de la série au graphique
	        data.add(series);
	        barChart.setData(data);
	        }

}
