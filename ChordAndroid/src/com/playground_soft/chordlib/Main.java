package com.playground_soft.chordlib;

import java.io.*;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String input = "";
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("test.txt")));
			StringBuilder builder = new StringBuilder();
			
			String line;
			while((line = reader.readLine())!=null){
				builder.append(line);
				builder.append("\n");
			}
			input = builder.toString();
			
			reader.close();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Document d = Document.create(input);
		for(int i = 0; i<d.count();i++){
			Element e = d.getElement(i);
			if(e.type == Element.Type.Text)
				System.out.printf("%s", e.data);
			else
				System.out.printf("[%s : %s]" , e.type, e.data);
		}

	}

}
