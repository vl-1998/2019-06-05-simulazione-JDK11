package it.polito.tdp.crimes.model;

import java.time.LocalDateTime;

public class Eventi implements Comparable<Eventi> {
	//TIPI DI EVENTO
	//1. evento criminoso
	//1.1 La centrale seleziona l'agente libero più vicino
	//1.2 (se non c'è nessuno è mal gestito)
	//1.3 se c'è un agente libero ->setto agente occupato, sta servendo quel crimine
	
	//2. agente selezionato arriva sul posto
	//2.1 capire quanto dura l'intervento, simulando una probailità
	//2.2 Controllo se il crimine è mal gestito, ritardo dell'agente
	//2.3 Scateno evento di terminazione del crimine
	
	//3. Crimine TERMINATO
	//3.1 l'agente deve essere settato come non più occupato
	

	public enum EventType{
		CRIMINE,
		ARRIVA_AGENTE,
		GESTITO,
	}

	
	private EventType type;
	private LocalDateTime data;
	private Event event;
	/**
	 * @param type
	 * @param data
	 * @param event
	 */
	public Eventi(EventType type, LocalDateTime data, Event event) {
		super();
		this.type = type;
		this.data = data;
		this.event = event;
	}
	public EventType getType() {
		return type;
	}
	public void setType(EventType type) {
		this.type = type;
	}
	public LocalDateTime getData() {
		return data;
	}
	public void setData(LocalDateTime data) {
		this.data = data;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	@Override
	public int compareTo(Eventi o) {
		return this.data.compareTo(o.getData());
	}
	
	
	
	
	
}
