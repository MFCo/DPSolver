package com.tpe.dpsolver.client.logic.structures;


public class Literal {
	private boolean value;
	private char name;
	private boolean invalid;

	@Override
	public boolean equals(Object o) {
		Literal literal = (Literal) o;
		if (value != literal.value)
			return false;
		if (name != literal.name)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name;
		int v = 0;
		if (value)
			v = 1;
		result = 31 * result + v;
		return result;
	}

	public void clone(Object another) {
		this.value = ((Literal) another).value;
		this.name = ((Literal) another).name;
		this.invalid = ((Literal) another).invalid;
	}

	public Literal(boolean valor, char nombre) {
		if ((nombre >= 65) && (nombre <= 90)) {
			value = valor;
			name = nombre;
			invalid = false;
		}
	}

	public Literal() {
		invalid = true;
	}

	public boolean invalid() {
		return invalid;
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public char getName() {
		return name;
	}

	public void setName(char name) {
		this.name = name;
	}

	public String imprimir() {
		String lit = "";
		if (value == false)
			lit = ("-" + name);
		else
			lit = ("" + name);
		return lit;
	}
	public String toString(){
		if(this.value)
			return "" + this.name;
		else 
			return "-" + this.name;
		
	}

}
