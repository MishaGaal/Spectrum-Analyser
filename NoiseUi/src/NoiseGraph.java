import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;







import com.fazecast.jSerialComm.SerialPort;

import static spectr.PSD.*;

public class NoiseGraph {

	final static JFrame window = new JFrame();
	
	int N =1;
	int bufferSize = 512;//65536;
	double t = 0.35 ;
	static SerialPort chosenPort;
	static int time = 0;
	static String[] pgaOptions = { "1", "2", "4", "8", "10", "20", "40", "80",
			"100" };
	static String[] filterGains = { "1", "2", "3", "4", "8" };
	static String[] filterFreq = { "MUTE", "10 KHz", "20 KHz", "30 KHz",
			"100 Khz" };
	static String[] loadOptions = { "1 KOhm", "3 KOhm", "10 KOhm", "100 KOhm" };
	static String[] adcOptions = {"20 MHz", "40 Hz"};
	static PrintWriter output;

	List<Double> data ;
	Double [] PSDAvr; 
	Double[] dataTest;
	public Double[] PSDAvrTest;

	JPanel bottomPanel = new JPanel();
	JCheckBox chekPga = new JCheckBox("PGA 2  ", true);
	JRadioButton chekGatePol = new JRadioButton("Vd pos ", true);
	JRadioButton chekDrainPol = new JRadioButton("Vg pos ", true);
	JRadioButton chekLnaGround = new JRadioButton("LNA out", true);
	
	JComboBox<String> portList = new JComboBox<String>();
	JButton connectButton = new JButton("Start");
	SerialPort[] portNames = SerialPort.getCommPorts();
	
	JPanel leftPanel = new JPanel();
	JPanel toolsPanel = new JPanel();
	
	JPanel pgaPanel = new JPanel();
	JComboBox<String> pgaList = new JComboBox<String>();
	JLabel pgalabel = new JLabel("PGA");
	
	JPanel adcPanel = new JPanel();
	JComboBox<String> adcList = new JComboBox<String>();
	JLabel adclabel = new JLabel("ADC speed");
	
	
	JPanel filterPanel = new JPanel();
	JComboBox<String> filterList = new JComboBox<String>();
	JLabel filterLabel = new JLabel("Filter frequency");
	
	
	JPanel fgainPanel = new JPanel();
	JComboBox<String> fgainList = new JComboBox<String>();
	JLabel fgainlabel = new JLabel("Filter gain");
	
	JPanel loadPanel = new JPanel();
	JComboBox<String> loadList = new JComboBox<String>();
	JLabel loadlabel = new JLabel("R load");
	
	JLabel AvrLabel = new JLabel("Averageing");
	JTextField Avr = new JTextField();
	
	JPanel groundPanel = new JPanel();
	JTextField vGround = new JTextField();
	JLabel groundLabel = new JLabel("Set Vg");
	JPanel drainPanel = new JPanel();
	JTextField vDrain = new JTextField();
	JLabel drainLabel = new JLabel("Set Vd");
	
	XYSeries OnePSD = new XYSeries("PSD");
	XYSeries signal = new XYSeries("Adc Readings");
	XYSeries AvrPSD = new XYSeries("PSD averg");

	
	XYSeriesCollection dataset = new XYSeriesCollection(signal);
	JFreeChart signalChart = ChartFactory.createXYLineChart("Signal Reading",
		"Time (sec)", "Voltage (volt)", dataset);
	JFreeChart avrChart = ChartFactory.createXYLineChart("Average PSD",
			"Frequency (Hz)", "Amplitude (V^2/Hz)", dataset);
	
	XYPlot plot = avrChart.getXYPlot(); 
	LogAxis logAxis = new LogAxis ("Frequency (Hz)");
	LogAxis logAxis2 = new LogAxis ("Power Spectarl Density (V\u00B2/Hz)");
	
	
	ChartPanel chartPanel = new ChartPanel(signalChart);
	ChartPanel chartPanel1 = new ChartPanel(avrChart);
	Image img = Toolkit.getDefaultToolkit().getImage(getClass().getResource("hiclipart.png"));

	

	
	void showDataTest(ArrayList<Double> dataTest,int N){
		this.dataTest = new Double[dataTest.size()];
		window.remove(chartPanel);
		window.add(chartPanel1, BorderLayout.CENTER);
		long t  = System.currentTimeMillis();
		for(int i=0; i<dataTest.size(); i++){
			this.dataTest[i] =  dataTest.get(i);
		}

			PSDAvrTest = new Double[dataTest.size()];
			for(int i=0 ; i<N ; i++){
						if(i==0){
							avrChart.setTitle("PSD comp");
							Double[] psd = computePsd(this.dataTest);
							System.out.println("computed PSD");
							for(int j = 0; j<dataTest.size();j++){
							     PSDAvrTest[j] = psd[j];
							}
						}else if (i!=0){
							System.out.println("PSD avr computing: " +i);
							averageTestPsd(this.dataTest, i);
					}
					
			}
		System.out.println(System.currentTimeMillis() -  t);
		showTestPsd();
	}
	
	void showSeqTest(ArrayList<Double> dataTest,int N){
		window.remove(chartPanel);
		window.add(chartPanel1, BorderLayout.CENTER);
		System.out.println(dataTest.size());
		avrChart.setTitle("PSD comp");
		this.dataTest = new Double[2048];
			PSDAvrTest = new Double[2048];
			for(int i=0 ; i<N ; i++){
							for(int j=2048*i; j<this.dataTest.length+2048*i; j++){
								this.dataTest[j-2048*i] =  dataTest.get(j);
							}
			
						averageTestPsd(this.dataTest, i);
					}
			
		System.out.println(System.currentTimeMillis() -  t);
		showTestPsd();
	}
	
	
	void averageTestPsd(Object[] x, int N ){
	/*	Thread thread = new Thread(){
			@Override
			public void run() {	*/
		Double[] psd = computePsd(x);
		
		if(N==0){
			  System.out.println("computing PSD " +N);
			for(int j = 0; j<psd.length;j++){
			     PSDAvrTest[j] = psd[j];
			}
		}else if(N==1){
			System.out.println("computing average PSD : " +N);
					for(int i=0; i < PSDAvrTest.length; i++){
						PSDAvrTest[i] = (PSDAvrTest[i] + psd[i])/2;
					}
		}else if(N!=1 && N!=0){
			System.out.println("computing average PSD : " +N);
				for(int i=0; i < PSDAvrTest.length; i++){
					//PSDAvrTest[i] = (PSDAvrTest[i]/N  + (psd[i])/(N*(N-1)));
					PSDAvrTest[i] = (PSDAvrTest[i]*(N-1)/N  + (psd[i])/N);
				}
			}
		 }	  

	void showTestPsd(){
		avrChart.setTitle("Average PSD");
		AvrPSD.clear();
		Double[] avr = new Double[PSDAvrTest.length/2];
		for(int i = 0; i <avr.length; i++){
			avr[i] = 2*PSDAvrTest[i];
		}	
		AvrPSD.setNotify(false);
		for(int j = 1; j<avr.length; j++){
				AvrPSD.add(j, avr[j]);
		}
		AvrPSD.setNotify(true);
}
	
	NoiseGraph() {
			
			window.setTitle("Portable noise analyser");
			window.setIconImage(img);
			window.setSize(800, 600);
			window.setLayout(new BorderLayout());
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			setGraphics();
			
			
			connectButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(connectButton.getText().equals("Start")){
						chosenPort = SerialPort.getCommPort(portList
								.getSelectedItem().toString());
						chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER,
								0, 0);
						output = new PrintWriter(chosenPort.getOutputStream());
						N = Integer.parseInt(Avr.getText().trim());
						if(chosenPort.openPort()){
							setAdc();
							setDisabled();
							checkGroundAndPollar();
							setPga();
							setFilter();
							setFiltrGain();
							setLoad();
							setVoltages();
							startMeasurement();
						 }
							System.out.println("N = " + N);
							showSignalProgress(N);
					}else{
						chosenPort.closePort();
						showAvrPSD();
					}
				}
				
			});
			
			
			window.add(chartPanel, BorderLayout.CENTER);
			window.add(leftPanel, BorderLayout.EAST);
			window.setVisible(true);
			
		}
	
	void setAdc(){
		int adc = pgaList.getSelectedIndex();
		switch (adc) {
		case 0: {
			output.print("ltc:0;");
			output.flush();
			break;
		}
		case 1: {
			output.print("ltc:1;");
			output.flush();
			break;
		 }
		}
	}
	
	void setDisabled() {
		connectButton.setText("ShowPSD");
		portList.setEnabled(false);
		// AvrPSD.clear();
		pgaList.setEnabled(false);
		filterList.setEnabled(false);
		fgainList.setEnabled(false);
		loadList.setEnabled(false);
		vGround.setEditable(false);
		vDrain.setEditable(false);
		Avr.setEditable(false);
	}

	void checkGroundAndPollar() {
		if (chekLnaGround.isSelected()) {
			output.print("lnaout;");
			output.flush();
		} else {
			output.print("lnagrnd;");
			output.flush();
		}

		if (chekDrainPol.isSelected()) {
			output.print("vmpos;");
			output.flush();
		} else {
			output.print("vmneg;");
			output.flush();
		}

		if (chekGatePol.isSelected()) {
			output.print("vgpos;");
			output.flush();

		} else {
			output.print("vgneg;");
			output.flush();
		}

		output.print("messOn;");
		output.flush();

	}

	void setPga() {
		int pga = pgaList.getSelectedIndex();
		switch (pga) {
		case 0: {
			output.print("vv:1;");
			output.flush();
			break;
		}
		case 1: {
			output.print("vv:2;");
			output.flush();
			break;
		}
		case 2: {
			output.print("vv:3;");
			output.flush();
			break;
		}
		case 3: {
			output.print("vv:4;");
			output.flush();
			break;
		}
		case 4: {
			output.print("vv:5;");
			output.flush();
			break;
		}
		case 5: {
			output.print("vv:6;");
			output.flush();
			break;
		}
		case 6: {
			output.print("vv:7;");
			output.flush();
			break;
		}
		case 7: {
			output.print("vv:8;");
			output.flush();
			break;
		}
		case 8: {
			output.print("vv:9;");
			output.flush();
			break;
		}

		}
	}

	void setFilter() {
			int frq = filterList.getSelectedIndex();
			switch (frq) {
			case 0: {
				output.print("fi:1;");
				output.flush();
				break;
			}
			case 1: {
				output.print("fi:2;");
				output.flush();
				break;
			}
			case 2: {
				output.print("fi:3;");
				output.flush();
				break;
			}
			case 3: {
				output.print("fi:4;");
				output.flush();
				break;
			}
			}
		}
	
	void setFiltrGain(){
		int gain = fgainList.getSelectedIndex();
		switch (gain) {
		case 0: {
			output.print("nv:1;");
			output.flush();
			break;
		}
		case 1: {
			output.print("nv:2;");
			output.flush();
			break;
		}
		case 2: {
			output.print("nv:3;");
			output.flush();
			break;
		}
		case 3: {
			output.print("nv:4;");
			output.flush();
			break;
		}
		}
	}
	
	void setLoad(){
		int load = fgainList.getSelectedIndex();
		switch (load) {
		case 0: {
			output.print("load:1;");
			output.flush();
			break;
		}
		case 1: {
			output.print("load:2;");
			output.flush();
			break;
		}
		case 2: {
			output.print("load:3;");
			output.flush();
			break;
		}
		case 3: {
			output.print("load:4;");
			output.flush();
			break;
		}
		}
	}
	
	void setVoltages(){
		double vg = Integer.parseInt(vGround.getText().trim());
		output.print("sg:" + vg + ";");
		output.flush();
		int vd = Integer.parseInt(vDrain.getText().trim());
		output.print("sd:" + vd + ";");
		output.flush();
	}
	
	void startMeasurement(){
		output.print("noise;");
		output.flush();
	}
	
	void showSignalProgress(int N){

		Thread thread = new Thread() {
			@Override
			public void run() {
				data = new ArrayList<Double>();
				Scanner scanner = new Scanner(chosenPort.getInputStream());
				AvrPSD.clear();
				PSDAvr =  new Double[bufferSize];
				//while (scanner.hasNextLine()) {
				for(int i=0 ; i <N ; i++){
					connectButton.setEnabled(false);
					while (data.size() < bufferSize) {
						try {
							double voltage = 1023 - Double.parseDouble(scanner
									.nextLine());
							data.add(voltage);
							signal.add(time++, 1023 - voltage);
							window.repaint();
						} catch (Exception e) {

						}
					}
						System.out.println(i);
						averagePsd(data.toArray(), i);
						data.clear();
						signal.clear();
						//time = 0;
			}
				scanner.close();
				connectButton.setEnabled(true);
			}
			
		};	
		thread.start();
	}

	
	void showOnePSD(Object[] data){
		signal.clear();
		//chart.setTitle("PSD 1 sec");
				for(int i =1 ; i < data.length; i++){
					OnePSD.add(i, computePsd(data)[i]);
				}
	}
	void showAvrPSD(){
		window.remove(chartPanel);
		window.add(chartPanel1, BorderLayout.CENTER);
		Double[] avr = new Double[PSDAvr.length/2];
		for(int i = 0; i <avr.length; i++){
			avr[i] = 2*PSDAvr[i];
		}
		AvrPSD.setNotify(false);
		for(int i =1 ; i <avr.length ; i++){
			AvrPSD.add(i*t, avr[i]);
		}
		AvrPSD.setNotify(true);

		setEnabled();
	}
	
	void averagePsd(Object[] x, int N ){
		Thread thread = new Thread() {
			@Override
			public void run() {
				Double[] psd = computePsd(x);
				if (N == 0) {
					for (int i = 0; i < PSDAvr.length; i++) {
						PSDAvr[i] = psd[i];
					}
				} else {
					for (int i = 0; i < PSDAvr.length; i++) {
						PSDAvr[i] = (PSDAvr[i] * (N - 1) / N + (psd[i]) / N);
						System.out.println("computing average PSD " + i
								+ " from Thread " + this.getName());
					}
				}
			}

		};
		thread.setName("" + N);
		thread.start();
	}
	
	
	void setEnabled(){
		output.print("stop");
		output.flush();
		time=0;
		portList.setEnabled(true);
		pgaList.setEnabled(true);
		filterList.setEnabled(true);
		fgainList.setEnabled(true);
		loadList.setEnabled(true);
		connectButton.setText("Start");
		vGround.setEditable(true);
		vDrain.setEditable(true);
		Avr.setEditable(true);
	}
	void setGraphics(){
		chekPga.setBackground(Color.WHITE);
		chekPga.setAlignmentX(Component.RIGHT_ALIGNMENT);
		chekGatePol.setBackground(Color.WHITE);
		chekGatePol.setAlignmentX(Component.RIGHT_ALIGNMENT);
		chekDrainPol.setBackground(Color.WHITE);
		chekDrainPol.setAlignmentX(Component.RIGHT_ALIGNMENT);
		chekLnaGround.setBackground(Color.WHITE);
		chekLnaGround.setAlignmentX(Component.RIGHT_ALIGNMENT);
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.add(portList);
		bottomPanel.add(connectButton);
	
		for (int i = 0; i < portNames.length; i++) {
			portList.addItem(portNames[i].getSystemPortName());
		}
		
		toolsPanel.setBackground(Color.WHITE);
		toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.Y_AXIS));
		
		adcPanel.setBackground(Color.WHITE);
		adcPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		adcPanel.setLayout(new BoxLayout(adcPanel, BoxLayout.Y_AXIS));
		
		adcList.setAlignmentX(Component.CENTER_ALIGNMENT);
		for (int i = 0; i < adcOptions.length; i++) {
			adcList.addItem(adcOptions[i]);
		}
		adclabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		adcPanel.add(adclabel);
		adcPanel.add(adcList);

		
		
		pgaPanel.setBackground(Color.WHITE);
		pgaPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		pgaPanel.setLayout(new BoxLayout(pgaPanel, BoxLayout.Y_AXIS));
		
		pgaList.setAlignmentX(Component.CENTER_ALIGNMENT);
		for (int i = 0; i < pgaOptions.length; i++) {
			pgaList.addItem(pgaOptions[i]);
		}
		pgalabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		pgaPanel.add(pgalabel);
		pgaPanel.add(pgaList);

		filterPanel.setBackground(Color.WHITE);
		filterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
		filterList.setAlignmentX(Component.CENTER_ALIGNMENT);
		for (int i = 0; i < filterFreq.length; i++) {
			filterList.addItem(filterFreq[i]);
		}
		filterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		filterPanel.add(filterLabel);
		filterPanel.add(filterList);
		
		
		fgainPanel.setBackground(Color.WHITE);
		fgainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		fgainPanel.setLayout(new BoxLayout(fgainPanel, BoxLayout.Y_AXIS));
		fgainList.setAlignmentX(Component.CENTER_ALIGNMENT);
		for (int i = 0; i < filterGains.length; i++) {
			fgainList.addItem(filterGains[i]);
		}
		fgainlabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		fgainPanel.add(fgainlabel);
		fgainPanel.add(fgainList);
		
		
		
		loadPanel.setBackground(Color.WHITE);
		loadPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		loadPanel.setLayout(new BoxLayout(loadPanel, BoxLayout.Y_AXIS));
		loadList.setAlignmentX(Component.CENTER_ALIGNMENT);
		for (int i = 0; i < loadOptions.length; i++) {
			loadList.addItem(loadOptions[i]);
		}
		loadlabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		loadPanel.add(loadlabel);
		loadPanel.add(loadList);

		
		
	
		groundPanel.setBackground(Color.WHITE);
		groundPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		groundPanel.setLayout(new FlowLayout());
		vGround.setText("0     ");
		groundPanel.add(groundLabel);
		groundPanel.add(vGround, BorderLayout.NORTH);
		
		
		drainPanel.setBackground(Color.WHITE);
		drainPanel.setLayout(new FlowLayout());
		drainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		vDrain.setText("0     ");
		drainPanel.add(drainLabel);
		drainPanel.add(vDrain, BorderLayout.NORTH);
		
		AvrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		Avr.setText("1");
		
		toolsPanel.add(adcPanel);
		toolsPanel.add(pgaPanel);
		toolsPanel.add(filterPanel);
		toolsPanel.add(fgainPanel);
		toolsPanel.add(loadPanel);
		toolsPanel.add(groundPanel);
		toolsPanel.add(drainPanel);
		toolsPanel.add(chekPga);
		toolsPanel.add(chekLnaGround);
		toolsPanel.add(chekGatePol);
		toolsPanel.add(chekDrainPol);
		toolsPanel.add(AvrLabel);
		toolsPanel.add(Avr);
		toolsPanel.add(bottomPanel);
		
		leftPanel.setBackground(Color.WHITE);
		leftPanel.add(toolsPanel);
		
		dataset.addSeries(AvrPSD);
		plot.setDomainAxis(logAxis);
		plot.setRangeAxis(logAxis2);
		logAxis2.setAutoTickUnitSelection(false);
		//logAxis2.setMinorTickCount(9);  // changing the integer argument has no effect on chart
		logAxis2.setBase(10);
		logAxis.setAutoTickUnitSelection(false);
		//logAxis.setMinorTickCount(9);  // changing the integer argument has no effect on chart
		logAxis.setBase(10);
	}
	
	
	
	public static void main(String[] args) {
		//	
		NoiseGraph np = new NoiseGraph();
	
	}

}
