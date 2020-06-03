package it.polito.tdp.crimes.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.crimes.db.EventsDao;
import it.polito.tdp.crimes.model.Evento.TipoEvento;

public class Simulatore {
	//TIPI DI EVENTO 
	
	//1. Evento criminoso
	// 1.1 La centrale seleziona l'agente libero più vicino
	// 1.2 Se non ci sono disponibilità -> crimine mal gestito
	// 1.3 Se c'è un agente libero -> setto l'agente a occupato
	
	//2. L'agente selezionato ARRIVA sul posto
	// 2.1 Definisco quanto durerà l'intervento
	// 2.2 Controllo se il crimine è mal gestito (ritardo dell'agente)
	
	//3. Crimine TERMINATO
	// 3.1 "Libero" l'agente, che torna a essere disponibile
	
	//Strutture dati che ci servono
	// Input utente
	private Integer N;
	private Integer anno;
	private Integer mese;
	private Integer giorno;
	// Stato del sistema
	private Graph<Integer,DefaultWeightedEdge> grafo;
	private Map<Integer,Integer> agenti; //mappa distretto-># agenti liberi
	// Coda degli eventi
	private PriorityQueue<Evento> queue;
	// Output
	private Integer malGestiti;
	
	
	public void init(Integer N, Integer anno, Integer mese, Integer giorno,
			Graph<Integer,DefaultWeightedEdge> grafo) {
		this.N = N;
		this.anno = anno;
		this.mese = mese;
		this.giorno = giorno;
		this.grafo = grafo;
		
		this.malGestiti = 0;
		this.agenti = new HashMap<Integer, Integer>();
		for(Integer d : this.grafo.vertexSet()) {
			this.agenti.put(d,0);
		}
		
		//devo sceglie dov'è la centrale (e mettere N agenti in quel distretto)
		EventsDao dao = new EventsDao();
		Integer minD = dao.getDistrettoMin(anno);
		this.agenti.put(minD, N);
		
		//creo e inizializzo la coda
		this.queue = new PriorityQueue<Evento>();
		
		for(Event e : dao.listAllEventsByDate(anno, mese, giorno)) {
			queue.add(new Evento(TipoEvento.CRIMINE, e.getReported_date(),e));
		}
	}
	
	public int run() {
		Evento e;
		while((e = queue.poll()) != null) {
			switch (e.getTipo()) {
				case CRIMINE:
					System.out.println("NUOVO CRIMINE! " + e.getCrimine().getIncident_id());
					//cerco l'agente libero più vicino
					Integer partenza = null;
					partenza = cercaAgente(e.getCrimine().getDistrict_id());
					if(partenza != null) {
						//c'è un agente libero in partenza
						//setto l'agente come occupato
						this.agenti.put(partenza, this.agenti.get(partenza) - 1);
						//cerco di capire quanto ci metterà l'agente libero ad arrivare sul posto
						Double distanza;
						if(partenza.equals(e.getCrimine().getDistrict_id()))
							distanza = 0.0;
						else
							distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(partenza, e.getCrimine().getDistrict_id()));
						
						Long seconds = (long) ((distanza * 1000)/(60/3.6));
						this.queue.add(new Evento(TipoEvento.ARRIVA_AGENTE, e.getData().plusSeconds(seconds), e.getCrimine()));
						
					} else {
						//non c'è nessun agente libero al momento -> crimine mal gestito
						System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " MAL GESTITO!");
						this.malGestiti ++;
					}
					break;
				case ARRIVA_AGENTE:
					System.out.println("ARRIVA AGENTE PER CRIMINE! " + e.getCrimine().getIncident_id());
					Long duration = getDurata(e.getCrimine().getOffense_category_id());
					this.queue.add(new Evento(TipoEvento.GESTITO,e.getData().plusSeconds(duration), e.getCrimine()));
					//controllare se il crimine è mal gestito
					if(e.getData().isAfter(e.getCrimine().getReported_date().plusMinutes(15))) {
						System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " MAL GESTITO!");
						this.malGestiti ++;
					}
					break;
				case GESTITO:
					System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " GESTITO");
					this.agenti.put(e.getCrimine().getDistrict_id(), this.agenti.get(e.getCrimine().getDistrict_id())+1);
					break;
			}
		}
		
		return this.malGestiti;
	}

	private Long getDurata(String offense_category_id) {
		if(offense_category_id.equals("all_other_crimes")) {
			Random r = new Random();
			if(r.nextDouble() > 0.5)
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
		
		for(Integer d : this.agenti.keySet()) {
			if(this.agenti.get(d) > 0) {
				if(district_id.equals(d)) {
					distanza = 0.0;
					distretto = d; 
				} else if(this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d)) < distanza) {
					distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d));
					distretto = d;
				}
			}
		}
		return distretto;
	}
	
}
