package com.tpe.dpsolver.client.logic.ClauSet.containers;

import java.util.ArrayList;
import java.util.List;


public class Bolsa {
	private char variable;
	private List<HiperClausula> clausulas;

	public Bolsa(char v) {
		variable = v;
		clausulas = new ArrayList<HiperClausula>();
	}

	public char getVariable() {
		return variable;
	}

	public void setVariable(char variable) {
		this.variable = variable;
	}

	public void addClause(HiperClausula agregar) {
		clausulas.add(agregar);
	}

	public void removeClause(HiperClausula eliminar) {
		for (int i = 0; i < clausulas.size(); i++) {
			if (clausulas.get(i).equals(eliminar)) {
				clausulas.remove(i);
			}
		}
	}

	public boolean vacia() {
		return clausulas.size() == 0;
	}

	public String imprimir() {
		String s = new String("");
		for (int i = 0; i < clausulas.size(); i++) {
			s += clausulas.get(i).getClause().toString() + " ";
		}
		return s;
	}

	public List<HiperClausula> getClauses() {
		return clausulas;
	}

	@Override
	public boolean equals(Object comparar) {
		return ((Bolsa) comparar).getVariable() == this.variable;
	}
}
