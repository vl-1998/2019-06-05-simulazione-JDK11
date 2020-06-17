/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.crimes;

import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.crimes.model.Model;
import it.polito.tdp.crimes.model.Vicini;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxAnno"
    private ComboBox<Integer> boxAnno; // Value injected by FXMLLoader

    @FXML // fx:id="boxMese"
    private ComboBox<Integer> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="boxGiorno"
    private ComboBox<Integer> boxGiorno; // Value injected by FXMLLoader

    @FXML // fx:id="btnCreaReteCittadina"
    private Button btnCreaReteCittadina; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimula"
    private Button btnSimula; // Value injected by FXMLLoader

    @FXML // fx:id="txtN"
    private TextField txtN; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaReteCittadina(ActionEvent event) {
    	txtResult.clear();
    	
    	try {
    		Integer anno = boxAnno.getValue();
    		
    		this.model.creaGrafo(anno);
    		txtResult.appendText("Grafo creato! #vertici: "+this.model.vertexNumber()+" #archi: "+this.model.edgeNumber()+"\n");
    		
    		
    		
    		for (Integer i: this.model.getVertici()) {
    			List <Vicini> vicini = this.model.adiacenti(i);
    			txtResult.appendText("\n\nVICINI DEL DISTRETTO: "+i + "\n");
    			for (Vicini v: vicini) {
    				txtResult.appendText(v.getD1() +" -> "+ v.getD2()+ ". Distanza: "+v.getPeso()+"\n");
    			}
    		}
    		
    		
    	} catch (IllegalArgumentException e) {
    		txtResult.appendText("Scegliere un anno!");
    		return;
    	}

    }

    @FXML
    void doSimula(ActionEvent event) {
    	txtResult.clear();
    	String enne = txtN.getText();
    	try {
    		int N = Integer.parseInt(enne);
    		Integer anno = boxAnno.getValue();
    		Integer mese = boxMese.getValue();
    		Integer giorno = boxGiorno.getValue();
    		
    		if (anno == null || mese == null || giorno == null) {
    			txtResult.appendText("Selezionare tutti i campi!");
    			return;
    		}
    		
    		try {
    			LocalDate.of(anno, mese, giorno);
    		} catch(DateTimeException e) {
    			txtResult.appendText("Data non corretta");
    		}
    		
    		txtResult.appendText("Simula con "+N+ " agenti.");
    		txtResult.appendText("\nCrimini mal gestiti: "+this.model.simula(anno, mese, giorno, N));
    		
    		
    	} catch (IllegalArgumentException e) {
    		txtResult.appendText("Inserire valore valido!");
    		return;
    	}

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxGiorno != null : "fx:id=\"boxGiorno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaReteCittadina != null : "fx:id=\"btnCreaReteCittadina\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.boxAnno.getItems().setAll(this.model.getAnno());
    	this.boxMese.getItems().setAll(this.model.getMese());
    	this.boxGiorno.getItems().setAll(this.model.getDay());
    }
}
