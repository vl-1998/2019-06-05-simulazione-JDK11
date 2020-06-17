package it.polito.tdp.crimes.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.crimes.db.EventsDao;
import it.polito.tdp.crimes.model.Eventi.EventType;

public class Simulator {
	
	//MODELLO DEL MONDO
	private Graph <Integer, DefaultWeightedEdge> grafo;
	private Model model;
	//ho bisogno di una mappa per tener in conto la posizione degli agenti 
	//e se sono liberi o meno
	//nella mappa chiave=distretto, value=#agenti
	private Map <Integer,Integer> agenti; //#agenti liberi
	
	//PARAMETRI SIMULAZIONE
	private Integer N=5; //numero di agenti che posso settare da FXML
	private Integer anno;
	private Integer mese;
	private Integer giorno;
	
	//RISULTATI CALCOLATI
	private Integer malRiusciti;
	
	//CODA DEGLI EVENTI
	private PriorityQueue <Eventi> queue;
	

	public void init(Integer N, Integer anno, Integer mese, Integer giorno, Graph<Integer, DefaultWeightedEdge> grafo) {
		this.N=N;
		this.anno=anno;
		this.mese=mese;
		this.giorno=giorno;
		this.grafo=grafo;
		this.malRiusciti=0;
		
		this.agenti= new HashMap<>();
		//impostiamo 0 agenti nei distretti e poi diventeranno N
		
		//mi faccio dare tutti i distretti dal grafo e inizialmente in ogni
		//distretto metto 0 agenti
		for (Integer d : this.grafo.vertexSet()) {
			this.agenti.put(d, 0);
		}
		
		//devo scegliere dov'è la centrale e mettere N agenti in quel distretto
		EventsDao dao = new EventsDao();
		Integer distrettoMin= dao.getDistrettoMin(anno);
		this.agenti.put(distrettoMin, N);
		
		this.queue = new PriorityQueue <>();
		//aggiungo gli eventi del giorno selezionato
		for (Event e : dao.listAllEventsByDate(anno, mese, giorno)) {
			Eventi eTemp = new Eventi(EventType.CRIMINE, e.getReported_date(), e);
			queue.add(eTemp);
		}
	}
	
	//ritorna direttamente il numero di casi mal gestiti
	public int run() {
		Eventi e;
		while ((e=queue.poll()) != null) {
			switch (e.getType()) {
			case CRIMINE:
				System.out.println("Nuovo crimine! " + e.getEvent().getIncident_id());
				//cerco l'agente libero più vicino
				Integer partenza = null;
				//se l'agente è libero parte da qui
				//dammi il distretto più vicino in cui c'è l'agente libero
				partenza = cercaAgente(e.getEvent().getDistrict_id());
				if (partenza != null) {
					//c'è un agente libero in partenza
					//setto l'agente come occupato
					this.agenti.put(partenza, this.agenti.get(partenza-1));
					//capire quanto ci metterà l'agente libero ad arrivare sul posto
					Double distanza;
					if (partenza.equals(e.getEvent().getDistrict_id())) {
						distanza = 0.0;
					} else {
						distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(partenza, e.getEvent().getDistrict_id()));
					}
					
					//data la distanza posso calcolare quanto tempo
					Long seconds =(long)((distanza *1000)/(60/3.6));
					
					//scheduliamo l'evento di arrivo agente
					this.queue.add(new Eventi(EventType.ARRIVA_AGENTE, e.getData().plusSeconds(seconds), e.getEvent()));
					
				} else {
					//non c'è nessun agente libero -> crimine mal gestito
					this.malRiusciti++;
				}
				
				break;
				
			case ARRIVA_AGENTE:
				System.out.println("Arriva agente per crimine! " + e.getEvent().getIncident_id());
				Long duration = getDurata(e.getEvent().getOffense_category_id());
				//schedulo l'evento di fine crimine
				this.queue.add(new Eventi (EventType.GESTITO, e.getData().plusSeconds(duration), e.getEvent()));
				//controllare se il crimine è mal gestito
				if (e.getData().isAfter(e.getEvent().getReported_date().plusMinutes(15))) {
					//crimine mal gestito
					this.malRiusciti++;
				}
				break;
				
			case GESTITO:
				//devo liberare l'agente che rimane in quel distretto, ma torna ad essere disponibile
				this.agenti.put(e.getEvent().getDistrict_id(), this.agenti.get(e.getEvent().getDistrict_id())+1);
				break;
			}
		}
		
		
		
		return this.malRiusciti;
	}
	
	
	
	private Long getDurata(String offense_category_id) {
		if (offense_category_id.equals("all_other_crimes")) {
			Random r = new Random();
			if (r.nextDouble()>0.5)
				return Long.valueOf(2*60*60);
			else
				return Long.valueOf(1*60*60);
		} else {
			return Long.valueOf(2*60*60);
		}
	}

	private Integer cercaAgente(Integer district_id) {
		Double distanza = Double.MAX_VALUE;
		Integer distretto = null;
		//scorro i distretti, vedo se ci sono agenti liberi e controllo la distanza
		
		for (Integer d:this.agenti.keySet()) {
			//potrebbero non esserci agenti disponibili, per cui non sovrascrivo
			if (this.agenti.get(d)>0) {
				//se i distinct id sonon uguali devo impostare il peso
				if (district_id.equals(d)) { //caso fortunato in cui il crimine è avvenuto
											// nel distretto in cui è presente l'agente
					distanza = 0.0;
					distretto = d;
				} else if (this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d))<distanza){
					//sovrascrivo 
					distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d));
					distretto = d;
				}
			}	
		}
		
		return distretto;
	}

	public Integer getN() {
		return N;
	}
	public void setN(Integer n) {
		N = n;
	}
	
	

}
