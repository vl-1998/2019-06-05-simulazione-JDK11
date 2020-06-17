package it.polito.tdp.crimes.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	private EventsDao dao;
	private Graph <Integer,DefaultWeightedEdge> grafo;
	
	public Model () {
		this.dao = new EventsDao();
	}
	
	public List<Integer> getAnno(){
		List<Integer> anni = new ArrayList<>();
		for (LocalDate ld : dao.getDate()) {
			if (!anni.contains(ld.getYear())) {
				anni.add(ld.getYear());
			}
		}
		Collections.sort(anni);
		return anni;
	}
	
	public List<Integer> getMese(){
		List<Integer> mesi = new ArrayList<>();
		for (LocalDate ld : dao.getDate()) {
			if (!mesi.contains(ld.getMonthValue())) {
				mesi.add(ld.getMonthValue());
			}
		}
		Collections.sort(mesi);
		return mesi;
	}
	
	public List<Integer> getDay(){
		List<Integer> giorni = new ArrayList<>();
		for (LocalDate ld : dao.getDate()) {
			if (!giorni.contains(ld.getDayOfMonth())) {
				giorni.add(ld.getDayOfMonth());
			}
		}
		Collections.sort(giorni);
		return giorni;
	}
	
	public void creaGrafo(Integer anno) {
		this.grafo = new SimpleWeightedGraph <> (DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(this.grafo, dao.getVertex(anno));
		
		
		//giro sui 7 vertici per calcolare il peso 
		for (Integer i : this.grafo.vertexSet()) {
			for (Integer j : this.grafo.vertexSet()) {
				if(!i.equals(j)) {
					if (this.grafo.getEdge(i, j)==null) { //se l'arco non esiste già
						Double lat1 = this.dao.getLat(anno, i);
						Double lat2 = this.dao.getLat(anno, j);
						
						Double lon1 = this.dao.getLon(anno, i);
						Double lon2 = this.dao.getLon(anno, j);
						
						Double distanzaMedia = LatLngTool.distance(new LatLng (lat1,lon1), new LatLng(lat2,lon2), LengthUnit.KILOMETER);
					
						Graphs.addEdgeWithVertices(this.grafo, i, j, distanzaMedia);
					}
					
				}
			}
		}
	}
	
	public int vertexNumber () {
		return this.grafo.vertexSet().size();
	}
	public int edgeNumber () {
		return this.grafo.edgeSet().size();
	}
	public Set<Integer> getVertici(){
		return this.grafo.vertexSet();
	}
	
	public List<Vicini> adiacenti(Integer distretto) {
		List<Vicini> vicini = new ArrayList<>();
		
		List <Integer> viciniId = Graphs.neighborListOf(this.grafo, distretto);
		for (Integer i : viciniId) {
			DefaultWeightedEdge e = new DefaultWeightedEdge();
			e = this.grafo.getEdge(distretto, i);
			Double distanza = this.grafo.getEdgeWeight(e);
			
			Vicini vTemp = new Vicini(distretto, i, distanza);
			vicini.add(vTemp);
		}
		
		Collections.sort(vicini);
		
		return vicini;
		
	}
	
	public int simula(Integer anno,Integer mese,Integer giorno, Integer N) {
		Simulator sim = new Simulator();
		sim.init(N, anno, mese, giorno, grafo);
		
		return sim.run();
	}
	

}
