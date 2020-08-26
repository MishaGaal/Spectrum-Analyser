package textfiles;


import java.io.*;
import java.util.*;

public class TextFile extends ArrayList<String>{
	public static String read(String filepath) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(filepath).getAbsoluteFile()));
				String s;
				try {
				while((s=in.readLine())!=null) {
					sb.append(s);
					sb.append("\n");
				}
			}finally {
				in.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static void write(String filepath, String text) {
			try {
				PrintWriter out = new PrintWriter(new File(filepath).getAbsoluteFile());
				try {
					out.print(text);
				}finally {
					out.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	
	public TextFile(String filepath, String splitter) {
		super(Arrays.asList(read(filepath).split(splitter)));
		if(get(0).equals("")) remove(0);
	}
	public TextFile(String filepath) {
		this(filepath,"\n");
	}
	
	public void write(String filepath) {
		try {
			PrintWriter out = new PrintWriter(new File(filepath).getAbsoluteFile());
			try {
				for(String item : this)
					out.println(item);
			}finally {
				out.close();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}

