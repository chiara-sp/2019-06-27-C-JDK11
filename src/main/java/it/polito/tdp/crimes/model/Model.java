package it.polito.tdp.crimes.model;

import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	EventsDao dao;
	SimpleWeightedGraph<String, DefaultWeightedEdge> grafo;
	List<Adiacenza> adiacenze;
	List<String> soluzione;
	int pesoMassimo;
	
	public Model() {
		dao= new EventsDao();
		adiacenze= new LinkedList<>();
	}
	public List<String> getCategorie(){
		return dao.getCategorie();
	}
	public List<Integer> getGiorni(){
		return dao.getGiorni();
	}
	public void creaGrafo(String categoria, int giorno) {
		this.grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafo, dao.getVertici(categoria, giorno));
		
		adiacenze=dao.getAdiacenze(categoria, giorno);
		for(Adiacenza a: adiacenze) {
			if(grafo.vertexSet().contains(a.getTipo1())&& grafo.vertexSet().contains(a.tipo2)) {
				Graphs.addEdge(grafo, a.getTipo1(), a.getTipo2(), a.getPeso());
			}
		}
	}
	public int numVertici() {
		// TODO Auto-generated method stub
		return this.grafo.vertexSet().size();
	}

	public int numArchi() {
		// TODO Auto-generated method stub
		return this.grafo.edgeSet().size();
	}
	public List<Adiacenza> getArchiPesoInferiore(){
		if(grafo==null) {
			return null;
		}
		List<Adiacenza> lista= new LinkedList<>();
		for(Adiacenza a: adiacenze) {
			if(a.getPeso()<this.pesoMediano()) {
				lista.add(a);
			}
		}
		return lista;
	}
	public double pesoMediano() {
		int minimo=Integer.MAX_VALUE;
		int massimo=Integer.MIN_VALUE;
		for(Adiacenza a: adiacenze) {
			if(a.getPeso()>massimo) {
				massimo=a.getPeso();
			}
			if(a.getPeso()<minimo) {
				minimo=a.getPeso();
			}
		}
		return (massimo+minimo)/2;
	}
	public List<String> ricorsione(String partenza, String arrivo){
		this.soluzione= new LinkedList<>();
		this.pesoMassimo=0;
		List<String> parziale= new LinkedList<>();
		parziale.add(partenza);
		
		cerca(parziale, 1, arrivo, 0);
		
		return soluzione;
	}
	private void cerca(List<String> parziale, int livello, String arrivo, int pesoToT) {
		
		//condizione di terminazione
		String ultimo= parziale.get(parziale.size()-1);
		if(ultimo.equals(arrivo)&& pesoToT>this.pesoMassimo) {
			soluzione = new LinkedList<>(parziale);
			this.pesoMassimo=pesoToT;
		}
		
		for(String s: Graphs.neighborListOf(grafo,ultimo)) {
			if(!parziale.contains(s)) {
				int peso= (int)grafo.getEdgeWeight(grafo.getEdge(s, ultimo));
				parziale.add(s);
				pesoToT+=peso;
				cerca(parziale,livello+1, arrivo,pesoToT);
				parziale.remove(s);
				pesoToT-=peso;
			}
		}
		
	}
	public int getPesoMassimo() {
		return pesoMassimo;
	}
	public boolean grafoCreato() {
		if(grafo==null)
			return false;
		return true;
	}
}
