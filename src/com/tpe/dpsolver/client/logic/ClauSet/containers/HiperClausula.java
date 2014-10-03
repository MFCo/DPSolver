package com.tpe.dpsolver.client.logic.ClauSet.containers;

import java.util.*;

import com.tpe.dpsolver.client.logic.structures.*;

public class HiperClausula {
	private Clausula clause;
	private List<Bolsa> vars;

	public HiperClausula() {
		clause = null;
		vars = new ArrayList<Bolsa>();
	}

	public HiperClausula(Clausula c) {
		clause = c;
		vars = new ArrayList<Bolsa>();
	}

	public Clausula getClause() {
		return clause;
	}

	public void setClause(Clausula clause) {
		this.clause = clause;
	}

	public void addBag(Bolsa agregar) {
		vars.add(agregar);
	}

	public void unLink() {
		for (int i = 0; i < vars.size(); i++) {
			vars.get(i).removeClause(this);
		}
		vars.clear();
	}

	public String imprimir() {
		return this.clause.imprimir();
	}

	@Override
	public boolean equals(Object hc) {
		return this.clause.equals(((HiperClausula) hc).getClause());
	}
}