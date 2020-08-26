package spectr;

public class PSD {
	static double time = 0.002;
	
	 public static void printMass( Object [] mas){
	    	for(int i =0 ; i < mas.length; i++){
	    		System.out.println(mas[i]);
	    	}
	    }
	
	//DFT
	public static Complex[] computeDft(Complex[] x) {
		Complex[] y = new Complex[x.length]; 
		int n = x.length;
		for (int k = 0; k < n; k++) {  // For each output element
			double sumreal = 0;
			double sumimag = 0;
			for (int t = 0; t < n; t++) {  // For each input element
				double angle = 2 * Math.PI * t*k / n;
				sumreal +=  x[t].re() * Math.cos(angle) + x[t].im() * Math.sin(angle);
				sumimag += -x[t].re() * Math.sin(angle) + x[t].im() * Math.cos(angle);
				
			}
			y[k] = new Complex(sumreal,sumimag);
		}
		return y;
	}
	//FFT
	 public static Complex[] computeFft(Complex[] x) {
	        int n = x.length;
	        if (n == 1) return new Complex[] { x[0] };
	        if (n % 2 != 0) {
	            throw new IllegalArgumentException("n is not a power of 2");
	        }
	        Complex[] even = new Complex[n/2];
	        for (int k = 0; k < n/2; k++) {
	            even[k] = x[2*k];
	        }
	        Complex[] evenFFT = computeFft(even);
	        Complex[] odd  = even;  // reuse the array (to avoid n log n space)
	        for (int k = 0; k < n/2; k++) {
	            odd[k] = x[2*k + 1];
	        }
	        Complex[] oddFFT = computeFft(odd);

	        Complex[] y = new Complex[n];
	        for (int k = 0; k < n/2; k++) {
	            double kth = -2 * k * Math.PI / n;
	            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
	            y[k]       = evenFFT[k].plus (wk.times(oddFFT[k]));
	            y[k + n/2] = evenFFT[k].minus(wk.times(oddFFT[k]));
	        }
	        return y;
	    }
	    
			
	 //PSD
	  public static Double[] computePsd(Complex[] z){
		  	Complex [] x = computeFft(z);
			Double[] y = new Double[x.length];
			for(int i = 0; i < x.length; i++){
				y[i] = (x[i].re()*x[i].re() + x[i].im()*x[i].im())*(time/x.length);
			}
			return y;
		}
	   
	   public static Double[] computePsd(Object[] x){
	    	Complex[] y = new Complex[x.length];
	    	for(int i =0 ; i<x.length; i++){
	    		y[i] = new Complex(Double.parseDouble(x[i].toString()), 0);
	    	}
	    	return computePsd(y);
	    }

	   
	   
}
