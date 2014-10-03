package com.tpe.dpsolver.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Changer {
	
	public InputStream change(String formula){
		InputStream stream = new ByteArrayInputStream(formula.getBytes());
		return stream;
		}
}
