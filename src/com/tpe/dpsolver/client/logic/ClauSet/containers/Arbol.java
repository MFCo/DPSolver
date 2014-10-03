package com.tpe.dpsolver.client.logic.ClauSet.containers;

public class Arbol {
	private int numero;
	private Arbol izq;
	private Arbol der;

	public Arbol() {
		izq = null;
		der = null;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public Arbol getIzq() {
		return izq;
	}

	public void setIzq(Arbol izq) {
		this.izq = izq;
	}

	public Arbol getDer() {
		return der;
	}

	public void setDer(Arbol der) {
		this.der = der;
	}

}
