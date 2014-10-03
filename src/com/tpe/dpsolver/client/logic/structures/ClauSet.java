package com.tpe.dpsolver.client.logic.structures;

import com.tpe.dpsolver.client.logic.ClauSet.containers.*;

import java.util.*;

public class ClauSet {
	private Arbol idTree;
	private int cantVariables;
	private List<Bolsa> variables;
	private List<HiperClausula> formula;

	public ClauSet() {
		idTree = null;
		formula = new LinkedList<HiperClausula>();
		variables = new LinkedList<Bolsa>();
		cantVariables = 0;
	}
	
	public Vector <Character> getVars(){
		Vector<Character> aux=new Vector<Character>();
		for(int i=0; i<variables.size(); i++)
			aux.add(variables.get(i).getVariable());
		return aux;	
	}

	public void addVector(Vector<Clausula> listaClausulasInput) {
		for (int i = 0; i < listaClausulasInput.size(); i++)
			add(listaClausulasInput.elementAt(i));
	}

	public void add(Clausula clausulaInput) {
		if (!clausulaInput.tautologia())
			if (!cRepetida(this.formula, clausulaInput.getID())) {
				HiperClausula hc = new HiperClausula(clausulaInput);
				this.formula.add(hc);
				Vector<Character> temporal = clausulaInput.getVars();
				for (int j = 0; j < temporal.size(); j++) {
					Bolsa agregar = new Bolsa(temporal.elementAt(j));
					if (!this.variables.contains(agregar)) {
						cantVariables++;
						this.variables.add(agregar);
						agregar.addClause(hc);
						hc.addBag(agregar);
					} else {
						agregar = buscarVariable(temporal.elementAt(j),
								this.variables);
						agregar.addClause(hc);
						hc.addBag(agregar);
					}
				}
			}
	}

	public void resolver(char variable, Vector<Clausula> resueltas) {
		Bolsa bag = buscarVariable(variable, this.variables);
		if (bag != null) {
			if (bag.getClauses().size() > 1) {
				Clausula aResolver;
				for (int i = 0; i < bag.getClauses().size() - 1; i++) {
					for (int j = i + 1; j < bag.getClauses().size(); j++) {
						aResolver = bag
								.getClauses()
								.get(i)
								.getClause()
								.resolver(bag.getVariable(),bag.getClauses().get(j).getClause());
						System.out.println("Resuelta... " + aResolver.imprimir());
						if (!repetido(idTree, aResolver.getID()))
							resueltas.add(aResolver);
					}
				}
			}
		}
		limpiarArbol(idTree);
	}

	public Vector< Vector<String>> DP(Vector<Character> variables) {
		Vector<Clausula> temporales = new Vector<Clausula>();
		Vector< Vector<String>> devolver = new Vector< Vector<String>>();
		Vector<String> columna = new Vector<String>();
		char var;
		int i = 0;
		while ((this.getCantidadVariables() > 0) || (i < variables.size())) {
			temporales.clear();
			var = variables.elementAt(i);
			columna = new Vector<String>();
			columna.add(Character.toString(var));
			if(this.imprimir().length() > 0){
				columna.add(this.imprimir());
			}
			else{
				columna.add("Formula Vacia");
			}	
			if (match(var, this.variables)) {
				columna.add(this.imprimirVar(var));
				System.out.println("Con " + var +": { " + this.imprimirVar(var) + "}");
				this.resolver(var, temporales);
				this.eliminarVariable(var);
				this.addVector(temporales);
				String resueltas = new String("");
				for(int j = 0; j < temporales.size(); j++){
					if(!temporales.get(j).vacia())
						resueltas += temporales.get(j).toString() + " ";
				}
				if(resueltas.length() > 0){
					columna.add(resueltas);
				}
				else{
					if(temporales.size() > 0){
						if(!temporales.get(0).tautologia()){
							columna.add("Resolucion: Clausula Insatisfacible");
						}
						else{
							columna.add("Resolucion Vacia");
						}
							
					}
					else{
						columna.add("Resolucion Vacia");
					}
				}
				devolver.add(columna);
			} else {
				columna.add("Variable sin Clausulas");
				columna.add("Resolucion Vacia");
				devolver.add(columna);
				System.out.println("Variable no matcheada");
			}
			i++;
		}
		if (temporales.isEmpty()) {
			columna = new Vector<String>();
			columna.add("Satisfacible");
			devolver.add(columna);
			System.out.println("Satisfacible");
		} else {
			if (temporales.elementAt(0).tautologia()){
				columna = new Vector<String>();
				columna.add("Satisfacible");
				devolver.add(columna);
				System.out.println("Satisfacible");
			}
				
			else{
				columna = new Vector<String>();
				columna.add("Insatisfacible");
				devolver.add(columna);
				System.out.println("Insatisfacible");
			}
				
		}
		return devolver;
	}

	public int getCantidadVariables() {
		return cantVariables;
	}

	public String imprimir() {
		String s = new String("");
		for (int i = 0; i < this.formula.size(); i++) {
			s += this.formula.get(i).imprimir() + " ";
		}
		return s;
	}

	public String imprimirVar(char var) {
		String bolsa = new String("");
		for (int i = 0; i < this.variables.size(); i++) {
			if (this.variables.get(i).getVariable() == var) {
				bolsa += this.variables.get(i).imprimir();
				return bolsa;
			}
		}
		return "No match!";
	}

	private Bolsa buscarVariable(char variable, List<Bolsa> b) {
		for (int i = 0; i < b.size(); i++) {
			if (b.get(i).getVariable() == variable)
				return b.get(i);
		}
		return null;
	}

	private boolean repetido(Arbol root, int cID) {
		if (root == null) {
			root = new Arbol();
			root.setNumero(cID);
			root.setIzq(null);
			root.setDer(null);
			return false;
		} else if ((root.getNumero()) < cID)
			return repetido(root.getDer(), cID);
		else if ((root.getNumero()) > cID)
			return repetido(root.getIzq(), cID);
		else
			return true;
	}

	private boolean cRepetida(List<HiperClausula> listaHc, int numero) {
		for (int i = 0; i < listaHc.size(); i++) {
			if (listaHc.get(i).getClause().getID() == numero)
				return true;
		}
		return false;
	}

	private void limpiarArbol(Arbol root) {
		if (root != null) {
			limpiarArbol(root.getIzq());
			limpiarArbol(root.getDer());
			root = null;
		}
	}

	private void limpiarBolsasVacias(List<Bolsa> b) {
		for (int i = 0; i < b.size(); i++) {
			if (b.get(i).vacia()) {
				eliminarVariable(b.get(i).getVariable());
			}
		}
	}

	private void eliminarVariable(char var) {
		Bolsa bag = buscarVariable(var, this.variables);
		if (bag != null) {
			desvincularClausulas(bag.getClauses()); // Para todas las clausulas
													// apuntadas por esta bolsa,
													// se las borra de las demas
													// bolsas
			this.variables.remove(bag);
			this.cantVariables--;
			bag = null;
			limpiarBolsasVacias(this.variables);
		}
	}

	private void desvincularClausulas(List<HiperClausula> lhc) {
		while (lhc.size() > 0) {
			HiperClausula hc = lhc.get(0);
			hc.unLink();
			this.formula.remove(hc);
			hc = null;
		}
		lhc.clear();
		lhc = null;
	}

	private boolean match(char var, List<Bolsa> b) {
		if ((var >= 65) && (var <= 90)) {
			if (b.size() > 0) {
				return (buscarVariable(var, b) != null);
			} else
				return false;
		} else {
			System.out.println("La Variable debe estar en MAYUSCULA ");
			return false;
		}

	}
}