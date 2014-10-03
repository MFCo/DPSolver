package com.tpe.dpsolver.client.logic.structures;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;


public class Clausula {

	private int ID;
	private boolean valor_de_verdad;
	private Set<Literal> claus;

	public Clausula() {
		ID = 1;
		valor_de_verdad = true;
		claus = new HashSet<Literal>();
	}
	
	private void vaciar() {
		claus.clear();
	}
	private void setValor(boolean valor) {
		valor_de_verdad = valor;
	}
	private Literal buscar(char variable) {
		if (claus.contains(new Literal(true, variable)))
			return new Literal(true, variable);
		else if (claus.contains(new Literal(false, variable)))
			return new Literal(false, variable);
		else
			return new Literal();
	}

	public void eliminar(char variable) {
		Literal aux = new Literal();
		aux.clone(buscar(variable));
		if (!(aux.invalid())) {
			if (aux.getValue())
				ID /= (aux.getName() - 30);
			else
				ID /= (aux.getName() - 60);
			claus.remove(aux);
		}
	}
	public void addPlain(boolean value, char name) {
		Literal nuevo = new Literal(value, name);
		this.add(nuevo);
	}
	public void add(Literal aAgregar) {
		Literal aux = new Literal();
		aux.clone(buscar(aAgregar.getName()));
		if (!(aux.invalid())) {
			if (aux.getValue() != aAgregar.getValue()) {
				claus.add(aAgregar);
				this.setValor(true); // TAUTOLOGIA
				if (aAgregar.getValue())
					ID *= (aAgregar.getName() - 30);
				else
					ID *= (aAgregar.getName() - 60);
			}
			// rama ELSE: si tiene el mismo valor no se agrega porque ya lo
			// posee
		} else {
			if (this.vacia())
				this.setValor(false);
			claus.add(aAgregar);
			if (aAgregar.getValue())
				ID *= (aAgregar.getName() - 30);
			else
				ID *= (aAgregar.getName() - 60);
		}

	}
	public boolean vacia() {
		return claus.isEmpty();
	}
	public int getID() {
		return ID;
	}
	public boolean valorVariable(char var) {
		Literal nuevo1 = new Literal(true, var);
		return claus.contains(nuevo1);
	}
	public boolean tautologia() {
		return valor_de_verdad;
	}
	public String imprimir() {
		String Tau = " La Clausula es Tautologia: ";
		String NoSat = " La Clausula NO es Satisfacible y la Clausula es Vacia ";
		String Vac = " La Clausula es Vacia ";
		if (claus.size() != 0) {
			String clausula = new String();
			if (tautologia())
				clausula += Tau;
			clausula += "( ";
			Iterator<Literal> it = claus.iterator();
			Literal toCompare = it.next();
			clausula += toCompare.imprimir();
			while (it.hasNext()) {
				toCompare = it.next();
				clausula += " v " + toCompare.imprimir();
			}
			clausula += " )";
			return clausula;
		} else {
			if (!tautologia())
				return NoSat;
			return Vac;
		}
	}
	public int cantidad() {
		return claus.size();
	}
	public Vector<Character> getVars() {
		Vector<Character> vars = new Vector<Character>();
		Iterator<Literal> it = claus.iterator();
		while (it.hasNext()) {
			Literal toCompare = it.next();
			vars.add(toCompare.getName());
		}
		return vars;
	}
	public boolean equals(Clausula c) {
		return this.ID == c.getID();
	}
	public String toString(){
		if (claus.size() != 0) {
			String clausula = new String();
			if(this.tautologia())
				clausula += "*( ";
			else
				clausula += "( ";
			Iterator<Literal> it = claus.iterator();
			Literal toCompare = it.next();
			clausula += toCompare.imprimir();
			while (it.hasNext()) {
				toCompare = it.next();
				clausula += " v " + toCompare.imprimir();
			}
			clausula += " )";
			return clausula;
		}
		else{
			return "";
		}
	}
	/*
	 * public HashSet<Literal> getSet(){ return (HashSet<Literal>)this.claus; }
	 */

	public void copySet(Set<Literal> toCopy) {
		Iterator<Literal> it = this.claus.iterator();
		while (it.hasNext()) {
			Literal toCompare = it.next();
			toCopy.add(toCompare);
		}
	}

	public void clone(Object another) {
		this.ID = ((Clausula) another).getID();
		this.valor_de_verdad = ((Clausula) another).tautologia();
		/*
		 * HashSet<Literal> set = ((Clausula)another).getSet(); Iterator
		 * <Literal> it = set.iterator(); while (it.hasNext()) { Literal
		 * toCompare = it.next(); this.claus.add(toCompare); }
		 */
		((Clausula) another).copySet(this.claus);
	}

	public Clausula resolver(char variable, Clausula in) {
		Clausula devolver = new Clausula();
		Clausula otraClausula = new Clausula();
		otraClausula.clone((Clausula) in);
		if (this.valorVariable(variable) != otraClausula.valorVariable(variable)) {
			otraClausula.eliminar(variable);
			if (otraClausula.vacia()) {
				if (claus.size() == 1) { // P v -P case
					devolver.setValor(false);
					devolver.vaciar();
				} else {
					devolver.clone(this);
					devolver.eliminar(variable);
				}
			} else {
				devolver.clone(otraClausula);
				Iterator<Literal> it = claus.iterator();
				while (it.hasNext()) {
					Literal toCompare = it.next();
					devolver.add(toCompare);
				}
				devolver.eliminar(variable);
			}
		}else {
			devolver.vaciar();
			devolver.setValor(true);
		}
		return devolver;
	}
}
