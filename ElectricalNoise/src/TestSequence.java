

import java.util.*;

import textfiles.TextFile;


public class TestSequence {
		public static void main(String[] args){
			
			ArrayList<String> str =
					new TextFile("C:\\Users\\m.halytskyi\\Desktop\\sequence1_1kHz.txt", ",");
			ArrayList<String> str2 =
					new TextFile("C:\\Users\\m.halytskyi\\Desktop\\sequence1_2kHz.txt", ",");
			ArrayList<String> str3 =
					new TextFile("C:\\Users\\m.halytskyi\\Desktop\\sequence3_2kHz_100s.txt", ",");
			ArrayList<Double> seq1 = new ArrayList<Double>();
			ArrayList<Double> seq2 = new ArrayList<Double>();
			ArrayList<Double> seq3 = new ArrayList<Double>();
			for(int i=0; i < str.size(); i++){
				seq1.add(Double.parseDouble(str.get(i).trim()));
				//System.out.println(seq1.get(i)+"  " + seq1.size());
			}
			
			for(int i=0; i<str2.size(); i++){
				seq2.add(Double.parseDouble(str2.get(i).trim()));
				//System.out.println(seq3.get(i));
			}
			//System.out.println(seq2.size());
		
			/*for(int i=0; i<str3.size(); i++){
				if(i>100000 && i <193216){
					i+=2;
					seq3.add(Double.parseDouble(str3.get(i).trim()));
				}else if(i<100000){
					seq3.add(Double.parseDouble(str3.get(i).trim()));
				}
			}*/
			
	
			for(int i=0; i<str3.size(); i++){
				seq3.add(Double.parseDouble(str3.get(i).trim()));
			}
			System.out.println(seq3.size());
			
			NoiseGraph np = new NoiseGraph();
			np.showSeqTest(seq3,97);
		
			
		}
}
