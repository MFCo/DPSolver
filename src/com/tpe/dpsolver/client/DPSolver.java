package com.tpe.dpsolver.client;


import com.tpe.dpsolver.client.parser.*;
import com.tpe.dpsolver.client.logic.structures.Clausula;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Vector;
import java.util.Vector;

import com.tpe.dpsolver.client.logic.structures.ClauSet;
import com.tpe.dpsolver.client.parser.DCParser;
import com.tpe.dpsolver.client.parser.ParseException;

public class DPSolver implements EntryPoint {
	private HorizontalPanel solvePanel=new HorizontalPanel();
	private HorizontalPanel logicPanel=new HorizontalPanel();
	private static TextBox inputForm = new TextBox();
	private Button autoSolve=new Button("Automatico");
	private Button manualSolve=new Button("Paso a Paso");
	private Button and=new Button("&and;");
	private Button or=new Button("&or;");
	private Button cond=new Button("&rarr;");
	private Button conSem=new Button("&#8872;");
	private Button not=new Button("&not;");
	
	public void onModuleLoad() { 
		final int  MAX_LOOPS = 1000000000;
		final int WORK_LOOP_COUNT = 500000000;
		final int WORK_CHUNK = 1000000000;
		logicPanel.add(inputForm);
		solvePanel.add(autoSolve);
		solvePanel.add(manualSolve);
		logicPanel.add(and);
		logicPanel.add(or);
		logicPanel.add(cond);
		logicPanel.add(conSem);
		logicPanel.add(not);
		logicPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		solvePanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		logicPanel.setStyleName("center");
		solvePanel.setStyleName("center");
		RootPanel.get("dptable").add(logicPanel);
		RootPanel.get("dptable").add(solvePanel);
		inputForm.setFocus(true);
//ACCIONES DE SOLVEPANEL		
		autoSolve.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event){
					try {
						solveInput(null);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        }
	      });
		manualSolve.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event){
        		new GetVarOrderDialog().show();
	        }
	      });
//ACCIONES DE LOGICPANEL
	    inputForm.addKeyDownHandler(new KeyDownHandler() {
	        public void onKeyDown(KeyDownEvent event) {
	        	if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
						try {
							solveInput(null);
						} catch (ParseException e) {
					    	Window.alert("Error de sintaxis.");
					        inputForm.selectAll();
					        e.printStackTrace();
					        return;							
						}
	          }
	        }
	      });
		and.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event){
	    		final String actual = inputForm.getText();
	        	inputForm.setText(actual+ "∧");
	    		inputForm.setFocus(true);
	        }
	      });
		or.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event){
	    		final String actual = inputForm.getText();
	        	inputForm.setText(actual+ "∨");
	    		inputForm.setFocus(true);
	        }
	      });
		not.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event){
	    		final String actual = inputForm.getText();
	        	inputForm.setText(actual+ "¬");
	        	inputForm.setFocus(true);
	        }
	      });
		cond.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event){
	    		final String actual = inputForm.getText();
	        	inputForm.setText(actual+ "→");
	        	inputForm.setFocus(true);
	        }
	      });
		conSem.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event){
	    		final String actual = inputForm.getText();
	        	inputForm.setText(actual+ "⊨");
	        	inputForm.setFocus(true);
	        }
	      });
	}
	private static void solveInput(Vector<Character> order) throws  ParseException {
		try{
		String formula = inputForm.getText().toUpperCase();
		formula=preParser(formula);
	    if (formula.length()==0) //PARA PROBAR EL CORTE
	    {
	    	Window.alert("Error de sintaxis.");
	        inputForm.selectAll();
	        return;
	    }
		inputForm.setFocus(true);
		DCParser dc= new DCParser(formula);
		Vector<Clausula> aResolver = new Vector<Clausula>();
		aResolver = dc.Parsear(dc);
		ClauSet omega = new ClauSet();
		omega.addVector(aResolver);
		Vector<Character> variables;
		if (order!=null){
			boolean falta=false, sobra=false;
			Vector <Character> faltan= new Vector <Character>();
			Vector <Character> sobran= new Vector <Character>();
			verificaOrder(order, faltan, sobran, falta, sobra, omega.getVars());
			variables = order;
		}
		else
			variables=ordenHeur(omega);
		Vector< Vector<String>> DPdata = new Vector< Vector<String>>();
		DPdata = omega.DP(variables);
	    inputForm.setText("");
        new SolvedDialog(DPdata).show();
		}
		catch (ParseException e) {
	    	Window.alert("Error de sintaxis.");
	        inputForm.selectAll();
	        e.printStackTrace();
	        return;					
		}
}
        

	
	private static void verificaOrder(Vector<Character> order,
			Vector<Character> faltan, Vector<Character> sobran, boolean falta,
			boolean sobra, Vector <Character> vars) {

			faltantes(vars, order, falta, faltan);
			faltantes(order, vars, sobra, sobran);
	}
	private static void faltantes(Vector<Character> vars,
			Vector<Character> order, boolean falta, Vector<Character> faltan) {
		for (int i=0; i<vars.size();i++)
			for(int j=0; j<order.size();j++)
				if(!vars.contains(order.get(j))){
					falta=true;
					faltan.add(order.get(j));
				}		
	}
	private static Vector<Character> ordenHeur(ClauSet omega) {
		Vector <Character> aux=new Vector <Character>();
		aux=omega.getVars();
		return aux;
	}
	private static String preParser(String formula) {
		String Aux=new String();
		boolean semant=false;
		for(int i=0;i<formula.length();i++){
			if (formula.charAt(i)=='∧' || formula.charAt(i)=='*' )
				Aux=Aux+'^';
			else if (formula.charAt(i)=='∨' || formula.charAt(i)=='+')
				Aux=Aux+'v';
			else if (formula.charAt(i)=='¬')
				Aux=Aux+'-';
			else if (formula.charAt(i)=='→' || formula.charAt(i)=='>')
				Aux=Aux+"=>";
			else if (formula.charAt(i)=='⊨' || formula.charAt(i)=='='){
				Aux=Aux+"^-";
				semant=true;
			}
			else Aux=Aux+formula.charAt(i);			
		}
		return Aux;
	}

	private static class GetVarOrderDialog extends DialogBox {

	    public GetVarOrderDialog() {
	     setText("Insertar orden en que se resolveran las variables separadas por comas");
	     setAnimationEnabled(true);
	     setGlassEnabled(true);
		    center();
	     final TextBox order=new TextBox();
	     Button ready = new Button("Listo");
		 order.addKeyDownHandler(new KeyDownHandler() {
		        public void onKeyDown(KeyDownEvent event) {
		        	if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
		       	     	Vector<Character> orderVec;
			        	orderVec=loadOrderArr(order.getText().toUpperCase().trim());
			        	if (!orderVec.equals(null)&&order.getText().length()!=0)
							try {
								solveInput(orderVec);
							    order.setText("");
							} catch (ParseException e) {
								Window.alert("Error de sintaxis.");
				    	        order.selectAll();
				    	        e.printStackTrace();
				    	        order.setFocus(true);
				    	        return;								
							}
						else
			    	    {
			    	    	Window.alert("Error de sintaxis.");
			    	        order.selectAll();
			    	        order.setFocus(true);
			    	        return;
			    	    }
			        		
		        	}
		        }
		      });
	     ready.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	        	Vector<Character> orderVec;	
	        	orderVec=loadOrderArr(order.getText().toUpperCase().trim());
	        	if (orderVec!=null && order.getText().length()!=0)
					try {
						solveInput(orderVec);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
	        	{
	        		Window.alert("Error de sintaxis.");
	        		order.selectAll();
	    	        order.setFocus(true);
	        		return;
	        	}
	        }
		
	      });
	   Button close = new Button("Cerrar");
	   close.addClickHandler(new ClickHandler() {
	   public void onClick(ClickEvent event) {
		   GetVarOrderDialog.this.hide();
	      }
	    });
	   HorizontalPanel table=new HorizontalPanel();
	    table.add(order);
	    table.add(ready);
	    table.add(close);
   	  	setWidget(table);
	    }
	  }
	

	private static Vector<Character> loadOrderArr(String trim) {
		Vector<Character> order=new Vector <Character>();
		int i=0;
		while (i<trim.length()){
			if ((trim.charAt(i) >= 65) && (trim.charAt(i) <= 90)) {
				order.add(trim.charAt(i));
			}
			else return null;
			i++;
			if(i < trim.length())
			if((trim.charAt(i)==',')){
				i++;
			}else return null;
				
		}
		return order;
	}
	private static class SolvedDialog extends DialogBox {

	     public SolvedDialog(Vector<Vector<String>> DPdata) { //TO SHOW ES ESTRUCTURA QUE TRAE TODAS LAS RESOLUCIONES ETC
	      
	    	 
	      setText("Paso a paso de Davis Putnam");
	      setAnimationEnabled(true);
	      setGlassEnabled(true);
		  center();
	      Button close = new Button("Cerrar");
	      close.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	        	SolvedDialog.this.hide();
	        }
	      });
	      int it=0;
	      int sub=0;
	      FlexTable table=new FlexTable();
	      while(sub<DPdata.size()-1)
	      {
		      table.setWidget(it,0,new HTML("<b><u>Resolucion sobre: </u></b>"));
		      String aux= ((String) DPdata.elementAt(sub).elementAt(0));
		      table.setText(it, 1, aux);
		      it++;
	          table.setWidget(it, 1, new HTML("<b>S:</b>"));
		      aux= ((String) DPdata.elementAt(sub).elementAt(1));
	          table.setText(it, 2, "{"+ aux+ "}");
	          it++;
	          table.setWidget(it, 1, new HTML("<b>R:</b>"));
		      aux= ((String) DPdata.elementAt(sub).elementAt(2));
	          table.setText(it, 2, "{"+ aux+ "}");
	          it++;
	          table.setWidget(it, 1, new HTML("<b>T:</b>"));
	          table.setText(it, 2, "{"+ DPdata.elementAt(sub).elementAt(3)+ "}");
	          it++;
	          sub++;
	      }
	      table.setText(it,1,DPdata.elementAt(DPdata.size()-1).elementAt(0));
	      it++;
	      table.setWidget(it, 1, close);
	      setWidget(table);
	    }
	  }
}
