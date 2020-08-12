package AutoFuzzMain;

/****************************************************************/
/*                      AutoFuzzguiMain                         */
/*                                                              */
/****************************************************************/
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.FormatterClosedException;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import FuzzingEngine.FuzzerMain;
import FuzzingFunctions.FuzzingFunctionInterface;
import ProtocolLearner.AppDataParser;
import ProtocolLearner.FSAutomaton;
import ProtocolLearner.MessageTrace;
import ProtocolLearner.PassiveLearner;
import ProxyServer.ComAndControlSocket;
import ProxyServer.ProxyDataManipulator;
import ProxyServer.ProxyServerMain;

import AbstractionFuncs.*;
/**
 * Summary description for AutoFuzzguiMain
 *
 */
public class AutoFuzzguiMain extends JFrame
{

	/*
	 * Static class with configuration variables
	 */
	private static ConfigVariables systemConfig ;
	/**
	 * Abstraction function names
	 */
	private ArrayList<String> absFunctions = new ArrayList<String> () ;

	// Variables declaration
	private JTabbedPane tabbedPanelMain;
	private JPanel contentPane;
	//-----
	private JLabel labelTrafficView;
	private JLabel labelSetPort;
	private JLabel labelControlPort ;

	private JTextField textFieldSetPort;
	private JTextArea textAreaTrafficView;
	private JTextArea textAreaFuzzingStatus ; 
	private JScrollPane jScrollPane1;
	private JButton buttonStartRecording;
	private JButton buttonStopRecording;
	private JButton buttonStartProxy;
	private JButton buttonReverseInOutDirection ; 
	private JButton buttonClearTraffic ; 
	private JButton buttonBuildFSM ;
	private JButton buttonLoadTraffic ; 
	private JButton buttonExportTraffic ;
	private JButton buttonMimimizeFSM ;
	private JButton buttonStartFuzzing ;
	private JButton buttonStopFuzzing ;
	private JButton buttonInitFuzzingEngine ;
	private JButton buttonCompletionStatus ;
	private JButton buttonAcceptControlClient ;

	private JTextField fieldControlPort ;
	private JTextField fieldCompletionStatus ;

	// These are identical to the start/stopFuzzing buttons and perform
	// the same action, but are located on different panels. 
	private JButton buttonStartFuzzing1 ;
	private JButton buttonStopFuzzing1 ;
	private JComboBox boxChooseAbstraction ;
	private JSeparator separatorProxy ;
	private JTable tableGenericStrings ; 
	private JScrollPane paneGenericStrings ;
	private JScrollPane paneFuzzingStatus ;
	private JLabel labelFuzzingStatus ;

	private boolean proxyStarted ;
	private boolean recStarted ; 
	private boolean fuzzingStarted ; 

	private JSeparator separatorWorkingPanel;  	
	private JSeparator separatorFuzzWorkingPanel ;
	DefaultTableModel tableModelGenericStrings = null ;

	private ProxyDataManipulator dataManipulator ;
	private JPanel panelProxy;
	private JPanel panelOriginalFSM ;
	private JPanel panelMinimizedFSM ;
	private JPanel panelFuzzingWindow ;

	private ProxyServerMain proxy ;
	AbstractionFunct iabs = null ;
	AbstractionFunct oabs = null ;

	// This is the main automaton on which the fuzzing will be performed
	private FSAutomaton automaton = null ;
	private FSAutomaton minimizedAutomaton = null ;
	private ArrayList<MessageTrace> traces = null ;
	private PassiveLearner learner = null ;
	private AppDataParser dataParser = null ;
	private String trafficString = null ; 
	private GuiFASBuilder fsaBuilder = null ;
	private FuzzerMain fuzzer = null ;
	private ComAndControlSocket cacSocket = null ;
	//-----
	// End of variables declaration
	
	private LoggingFacility loggingFacility = null ;
	private ConfigVariables variables = null ;
	
	private Object[][] dataVector  ;
	private Object[] columnVector  ;

	// This is the number of columns that will be in the table on the fuzzing panel
	// Currently the table will have stateID, inputSymbol, Generic Message columns
	private static final int numOfColumns = 3 ;

	public AutoFuzzguiMain()
	{
		super();
		systemConfig = new ConfigVariables() ;
		this.checkSystemConfiguration() ;
		initializeComponent();

		//
		// TODO: Add any constructor code after initializeComponent call
		//
		dataManipulator = new ProxyDataManipulator( textAreaTrafficView, textAreaFuzzingStatus ) ;
		proxy = new ProxyServerMain() ;
		this.setVisible(true);
		proxyStarted = false ;
		recStarted = false ;
		fuzzingStarted = false ;
		dataParser = new AppDataParser() ;
		learner = new PassiveLearner() ;
		fsaBuilder = new GuiFASBuilder() ;
		loggingFacility = new LoggingFacility() ;
		variables = new ConfigVariables() ;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always regenerated
	 * by the Windows Form Designer. Otherwise, retrieving design might not work properly.
	 * Tip: If you must revise this method, please backup this GUI file for JFrameBuilder
	 * to retrieve your design properly in future, before revising this method.
	 */
	private void initializeComponent()
	{
		checkSystemConfiguration() ;
		int workingPanelStartX = 10 ;
		int workingPanelStartY = 590 ;
		int sidePanelStartX = 20 ;
		int sidePanelStartY = 30 ;

		tabbedPanelMain = new JTabbedPane();
		contentPane = (JPanel)this.getContentPane();
		//-----
		labelTrafficView = new JLabel();
		labelSetPort = new JLabel();
		labelControlPort = new JLabel() ;
		textFieldSetPort = new JTextField();
		textAreaTrafficView = new JTextArea();
		textAreaFuzzingStatus = new JTextArea() ;
		jScrollPane1 = new JScrollPane();
		paneFuzzingStatus = new JScrollPane() ;
		buttonStartRecording = new JButton();
		buttonStopRecording = new JButton();
		buttonClearTraffic = new JButton() ;
		buttonStartProxy = new JButton() ;
		buttonReverseInOutDirection = new JButton() ;
		buttonBuildFSM = new JButton() ;
		buttonLoadTraffic = new JButton() ;
		buttonExportTraffic = new JButton() ;
		buttonMimimizeFSM = new JButton() ;
		buttonStartFuzzing = new JButton() ;
		buttonStopFuzzing = new JButton() ;
		buttonStartFuzzing1 = new JButton() ;
		buttonStopFuzzing1 = new JButton() ;
		buttonInitFuzzingEngine = new JButton() ;
		labelFuzzingStatus = new JLabel() ;

		buttonAcceptControlClient = new JButton() ;
		fieldControlPort = new JTextField() ;
		fieldControlPort.setText( "22223" ) ;

		buttonCompletionStatus = new JButton() ;
		fieldCompletionStatus = new JTextField() ;
		fieldCompletionStatus.setText( "NULL" ) ;
		fieldCompletionStatus.setEditable( false ) ;

		boxChooseAbstraction  = new JComboBox() ;

		separatorWorkingPanel = new JSeparator() ;
		separatorFuzzWorkingPanel = new JSeparator() ;
		separatorProxy = new JSeparator() ;

		tableGenericStrings = new JTable() ;
		paneGenericStrings = new JScrollPane() ;
		this.initGenericStringsTable() ;

		panelProxy = new JPanel();
		panelOriginalFSM = new JPanel() ;
		panelMinimizedFSM = new JPanel() ;
		panelFuzzingWindow = new JPanel() ;

		//-----

		//
		// tabbedPanelMain
		//
		tabbedPanelMain.addTab("Proxy Server", panelProxy);
		tabbedPanelMain.addTab("Original FSM", new JScrollPane( panelOriginalFSM) ) ;
		tabbedPanelMain.addTab("Minimized FMS",  new JScrollPane( panelMinimizedFSM ) ) ;
		tabbedPanelMain.addTab("Fuzzing Engine", panelFuzzingWindow ) ;
		tabbedPanelMain.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				tabbedPanelMain_stateChanged(e);
			}

		});
		//
		// contentPane
		//
		contentPane.setLayout(null);
		addComponent(contentPane, tabbedPanelMain, 0,0, 1020, 676 );
		//
		// labelTrafficView
		//
		labelTrafficView.setText("Application Protocol Traffic");
		//
		// labelSetPort
		//
		labelSetPort.setText("Set Proxy Port");
		//
		// textFieldSetPort
		//
		textFieldSetPort.setText("22222");
		textFieldSetPort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				textFieldSetPort_actionPerformed(e);
			}

		});
		//
		// textAreaTrafficView
		//
		//
		// jScrollPane1
		//
		jScrollPane1.setViewportView(textAreaTrafficView);

		paneFuzzingStatus.setViewportView( textAreaFuzzingStatus ) ;
		//
		// buttonStartRecording
		//
		buttonStartRecording.setText("Start Recording Traffic");
		buttonStartRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				buttonStartRecording_actionPerformed(e);
			}

		});
		//
		// buttonStopRecording
		//
		buttonStopRecording.setText("Stop Recording Traffic");
		buttonStopRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				buttonStopRecording_actionPerformed(e);
			}

		});
		//
		// buttonStartProxy
		//
		buttonStartProxy.setText("Start Proxy"); 
		buttonStartProxy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				buttonStartProxy_actionPerformed(e);
			}

		});
		buttonReverseInOutDirection.setText( "Reverse Input/Output Traffic Direction" ) ;
		buttonReverseInOutDirection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				buttonReverseInOutDirection_actionPerformed(e);
			}

		});
		buttonClearTraffic.setText("Clear Screen");
		buttonClearTraffic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				buttonClearTraffic_actionPerformed(e);
			}

		});
		buttonBuildFSM.setText( "Construct FSM" ) ;
		buttonBuildFSM.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				buttonBuildFSM_actionPerformed(e);
			}
		});
		buttonLoadTraffic.setText( "Load App. Traffic" ) ;
		buttonLoadTraffic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				buttonLoadTraffic_actionPerformed(e);
			}
		});
		buttonExportTraffic.setText( "Export App. Traffic" ) ;
		buttonExportTraffic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				buttonExportTraffic_actionPerformed(e);
			}
		});
		buttonMimimizeFSM.setText( "Minimize FSM" ) ;
		buttonMimimizeFSM.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonMinimizeFSM_actionPerformed( e ) ;
			}
		}) ;
		buttonStartFuzzing.setText( "Start Fuzzing" ) ;
		buttonStartFuzzing.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonStartFuzzing_actionPerformed( e ) ;
			}
		}) ;
		buttonStartFuzzing1.setText( "Start Fuzzing" ) ;
		buttonStartFuzzing1.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonStartFuzzing_actionPerformed( e ) ;
			}
		}) ;
		buttonStopFuzzing.setText( "Stop Fuzzing" ) ;
		buttonStopFuzzing.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonStopFuzzing_actionPerformed( e ) ;
			}
		}) ;
		buttonStopFuzzing1.setText( "Stop Fuzzing" ) ;
		buttonStopFuzzing1.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonStopFuzzing_actionPerformed( e ) ;
			}
		}) ;
		buttonInitFuzzingEngine.setText( "Init. Fuzzing Engine" ) ;
		buttonInitFuzzingEngine.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonInitFuzzingEngine_actionPerformed( e ) ;
			}
		}) ;
		buttonCompletionStatus.setText( "Update Completion %" ) ;
		buttonCompletionStatus.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonCompletionStatus_actionPerformed( e ) ;
			}
		}) ;
		buttonAcceptControlClient.setText( "Accept control client" ) ;
		buttonAcceptControlClient.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonAcceptControlClient_actionPerformed( e ) ;
			}
		}) ;
		labelControlPort.setText( "Port:" ) ;

		/***
		 * Load abstraction function names 
		 */
		boxChooseAbstraction.addItem( "Choose protocol abstraction function") ;
		this.loadAbstractionFuncs() ;
		for ( String s: absFunctions )
			boxChooseAbstraction.addItem( s ) ;
		//
		// Proxy
		//
		panelProxy.setLayout(null);
		panelProxy.setBackground(new Color(223, 209, 182));
		panelProxy.setOpaque(false);
		addComponent(panelProxy, labelTrafficView, 335,4,140,22);
		addComponent(panelProxy, jScrollPane1, 333,26,672,550);
		// -- Proxy Area
		addComponent(panelProxy, labelSetPort, sidePanelStartX,sidePanelStartY,80,20);
		addComponent(panelProxy, textFieldSetPort, sidePanelStartX + 200,sidePanelStartY,100,25);
		addComponent(panelProxy, buttonStartProxy, sidePanelStartX + 200, sidePanelStartY + 30, 100,25);


		// -- Other side panel Area
		addComponent(panelProxy, separatorProxy, sidePanelStartX, sidePanelStartY+65, 300, 10 ) ;
		int otherSidePanelX = sidePanelStartX ;
		int otherSidePanelY = sidePanelStartY + 75 ;
		addComponent(panelProxy, boxChooseAbstraction, otherSidePanelX, otherSidePanelY, 300, 25) ;
		addComponent(panelProxy, buttonReverseInOutDirection, otherSidePanelX, otherSidePanelY+30, 300, 25 ) ;
		addComponent(panelProxy, separatorWorkingPanel, 10, 585, 995, 10 ) ;
		addComponent(panelProxy, buttonStartRecording, workingPanelStartX, workingPanelStartY,155, 20);
		addComponent(panelProxy, buttonClearTraffic, workingPanelStartX + 875,workingPanelStartY, 120, 45 ) ;
		addComponent(panelProxy, buttonStopRecording, workingPanelStartX, workingPanelStartY+25,155,20);
		addComponent(panelProxy, buttonBuildFSM, workingPanelStartX + 160, workingPanelStartY, 110, 45 ) ;
		addComponent(panelProxy, buttonMimimizeFSM, workingPanelStartX + 275, workingPanelStartY, 110, 45 ) ;
		addComponent(panelProxy, buttonInitFuzzingEngine, workingPanelStartX + 390, workingPanelStartY, 160, 45 ) ;
		addComponent(panelProxy, buttonStartFuzzing, workingPanelStartX + 555, workingPanelStartY, 155, 20 ) ;
		addComponent(panelProxy, buttonStopFuzzing, workingPanelStartX + 555, workingPanelStartY + 25, 155, 20 ) ;
		addComponent(panelProxy, buttonLoadTraffic, workingPanelStartX + 715, workingPanelStartY, 155, 20 ) ;
		addComponent(panelProxy, buttonExportTraffic, workingPanelStartX + 715, workingPanelStartY + 25, 155, 20 ) ;

		// Fuzzer working panel components
		panelFuzzingWindow.setLayout(null);
		panelFuzzingWindow.setBackground(new Color(223, 209, 182));
		panelFuzzingWindow.setOpaque(false);
		addComponent( panelFuzzingWindow, separatorFuzzWorkingPanel, 10, 585, 995, 10 ) ;
		addComponent( panelFuzzingWindow, paneGenericStrings, 10, 10, 685, 560 ) ; 
		addComponent( panelFuzzingWindow, buttonStartFuzzing1, workingPanelStartX, workingPanelStartY, 155, 20 ) ;
		addComponent( panelFuzzingWindow, buttonStopFuzzing1, workingPanelStartX, workingPanelStartY + 25, 155, 20 ) ;
		addComponent( panelFuzzingWindow, buttonCompletionStatus, workingPanelStartX + 160, workingPanelStartY, 155, 20 ) ;
		addComponent( panelFuzzingWindow, fieldCompletionStatus, workingPanelStartX + 160, workingPanelStartY + 25, 155, 20 ) ;

		addComponent( panelFuzzingWindow, buttonAcceptControlClient, workingPanelStartX + 320, workingPanelStartY, 155, 20 ) ;
		addComponent( panelFuzzingWindow, fieldControlPort, workingPanelStartX + 400, workingPanelStartY+25, 75, 20 ) ;
		addComponent( panelFuzzingWindow, labelControlPort, workingPanelStartX + 325, workingPanelStartY+25, 75, 20 ) ;

		labelFuzzingStatus.setText( "Fuzzing Progress:" ) ;
		addComponent( panelFuzzingWindow, labelFuzzingStatus, 700, 10, 300, 10 ) ;
		addComponent( panelFuzzingWindow, paneFuzzingStatus, 700, 20, 300, 550 ) ;
		//
		// AutoFuzzguiMain
		//
		this.setTitle("AutoFuzz") ;
		this.setLocation(new Point(3, 1) ) ;
		this.setSize(new Dimension( 1024, 700 ) ) ;
		this.setResizable( true ) ;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
	}

	/** Add Component Without a Layout Manager (Absolute Positioning) */
	private void addComponent(Container container,Component c,int x,int y,int width,int height)
	{
		c.setBounds(x,y,width,height);
		container.add(c);
	}

	private void tabbedPanelMain_stateChanged(ChangeEvent e)
	{
		//	System.out.println("\ntabbedPanelMain_stateChanged(ChangeEvent e) called.");
		// TODO: Add any handling code here

	}

	private void textFieldSetPort_actionPerformed(ActionEvent e)
	{
		//System.out.println("\ntextFieldSetPort_actionPerformed(ActionEvent e) called.");
		// TODO: Add any handling code here

	}

	private void buttonStartRecording_actionPerformed(ActionEvent e)
	{
		if ( proxyStarted == false )
			JOptionPane.showMessageDialog(null, "The Proxy Server is not started" ) ;
		else if ( recStarted == true )
			JOptionPane.showMessageDialog( null, "Proxy is already recording the traffic" ) ;
		else
		{
			textAreaTrafficView.append("<Trace>") ;
			proxy.startRec() ;
			recStarted = true ;
		}
	}

	private void buttonStopRecording_actionPerformed(ActionEvent e)
	{
		if ( recStarted == true )
		{
			textAreaTrafficView.append("</Trace>\n") ;
			proxy.stopRec() ;
			recStarted = false ;
		}
		else
			JOptionPane.showMessageDialog( null, "Recording was never started" ) ;
	}

	private void buttonClearTraffic_actionPerformed( ActionEvent e )
	{
		textAreaTrafficView.setText("") ;
	}

	// Currently, the FSM gets constructed from the traffic in the traffic text area
	// this allows the user to modify it if needed. 
	private void buttonBuildFSM_actionPerformed( ActionEvent e )
	{
		if ( boxChooseAbstraction.getSelectedIndex() == 0 )
		{
			JOptionPane.showMessageDialog(null, "Please choose an abstraction function" ) ;
			return ;
		}
		trafficString = textAreaTrafficView.getText() ;
		trafficString = "<ApplicationData>" + trafficString + "</ApplicationData>" ;


		traces = dataParser.getMsgTraces( trafficString ) ;
		try { 
			String selectedStr = (String) boxChooseAbstraction.getSelectedItem() ;
			BufferedReader in = new BufferedReader(new FileReader( systemConfig.getSystemConfigPath() + "\\abstractionSource.txt") ) ;
			String str; 
			while ((str = in.readLine()) != null) 
			{ 
				if ( str.startsWith("#"))
					continue ;
				else
				{
					String[] strs = str.split( ":::" ) ;
					if ( strs.length == 3 && strs[0].equalsIgnoreCase( selectedStr ) )
					{
						try {
							iabs = this.instantiateAbsFunc( strs[1] ) ;
							oabs = this.instantiateAbsFunc( strs[2] ) ;
							if ( iabs == null || oabs == null )
								return ;
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(null, "Could not find class " + strs[1] + " or " + strs[2] ) ;
							return ;
						}							
					}
				}
			} 
			in.close(); 
		} catch (IOException ex) 
		{
			JOptionPane.showMessageDialog(null, "The source with abstraction functions was not found. Please check AutoFuzz/\\abstractionSource.txt" ) ;
		}
		if ( traces.size() != 0 && iabs != null && oabs != null )
			automaton = learner.constructAutomaton( traces, iabs, oabs ) ;
		if ( automaton != null )
		{	
			panelOriginalFSM.removeAll() ;
			fsaBuilder.drawFSA( automaton, panelOriginalFSM ) ;
		}			
		else
		{
			JOptionPane.showMessageDialog(null, "Could not construct FSA from the given traffic. Please check your abstraction function." ) ;
			return ;
		}
	}


	private void buttonReverseInOutDirection_actionPerformed( ActionEvent e )
	{
		variables.reverseTrafficOrder() ;
	}
	private void buttonStartProxy_actionPerformed (ActionEvent e) 
	{ 
		try { 
			if ( proxyStarted == false )
			{
				int port = Integer.parseInt( textFieldSetPort.getText() ) ;
				proxy.startProxyServer( port ) ; 
				buttonStartProxy.setText( "Stop Proxy" ) ;
				this.repaint() ;				
				proxyStarted = true ;
			}
			else
			{
				if ( recStarted == true ){
					JOptionPane.showMessageDialog( null , "Recording has to be stopped before the server" ) ;
					return ;
				}
				proxy.stopProxyServer() ;
				buttonStartProxy.setText( "Start Proxy" ) ;
				this.repaint() ;
				proxyStarted = false ;
			}
		}
		catch ( NumberFormatException ex )
		{
			JOptionPane.showMessageDialog( null, "Could not start proxy on port " ) ;
		}
	}
	private void buttonLoadTraffic_actionPerformed (ActionEvent e) 
	{ 
		File file = null ;
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		try{
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				BufferedReader in = new BufferedReader( new FileReader( file ) ) ;
				String line = null ;
				String traffic = "" ;
				textAreaTrafficView.setText("") ;
				while ( ( line = in.readLine() ) != null )
				{
					traffic = traffic + line + "\n" ;
				}
				in.close() ;
				if ( this.loadTrafficIntoTextArea( traffic ) == false )
					throw new FormatterClosedException() ;
			}
		}
		catch (Exception r)
		{
			JOptionPane.showMessageDialog(null, "Error reading from the file " + file.getName() ) ;
		}
	}

	private void buttonExportTraffic_actionPerformed (ActionEvent e) 
	{ 
		File file = null ;
		final JFileChooser fc = new JFileChooser();
		try
		{
			int returnVal = fc.showSaveDialog( this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				BufferedWriter out = new BufferedWriter( new FileWriter( file )) ;
				out.write( "<?xml version=\"1.0\"?>" ) ;
				out.write( "<ApplicationData>" + textAreaTrafficView.getText() + "</ApplicationData>" ) ;
				out.close() ;
			}
		}
		catch (Exception ex )
		{
			JOptionPane.showMessageDialog( null, "Error writing to the file " + file.getName() ) ;
		}
	} 

	/**
	 * Minimizes the current FSM and draws the minimized one in a separate tab. 
	 * @param e
	 */
	private void buttonMinimizeFSM_actionPerformed (ActionEvent e) 
	{ 
		if ( automaton != null )
		{
			minimizedAutomaton = learner.minimizeAutomaton( automaton, traces, iabs, oabs ) ;
			if ( minimizedAutomaton != null )
			{
				panelMinimizedFSM.removeAll() ;
				fsaBuilder.drawFSA( minimizedAutomaton, panelMinimizedFSM ) ;
			}
		}			
	} 

	private void buttonAcceptControlClient_actionPerformed (ActionEvent e) 
	{ 
		int port = -1 ;
		if ( cacSocket == null )
		{
			JOptionPane.showMessageDialog( null, "Fuzzer must be initialized prior to opening the socket" ) ;
			return ;
		}
		try
		{
			port = Integer.parseInt( fieldControlPort.getText() ) ;
			if ( port < 1 || port > 65535 )
				throw new NumberFormatException() ;
		}
		catch ( Exception ex ) {
			JOptionPane.showMessageDialog( null, "Invalid command and control port specified." ) ;
			return ;
		}
		if ( port != -1 )
		{
			cacSocket.openSocket( port ) ;
		}
	} 

	private void buttonStartFuzzing_actionPerformed (ActionEvent e) 
	{ 
		if ( proxyStarted == false )
		{
			JOptionPane.showMessageDialog( null,  "Please start the proxy server.") ;
			return ;
		}
		else if ( minimizedAutomaton == null )
		{
			JOptionPane.showMessageDialog( null,  "Please minimize the FSA first.") ;
			return ;
		}
		else if ( ! dataManipulator.isFuzzerInitialized() )
		{
			JOptionPane.showMessageDialog( null,  "Please initialize the fuzzer first" ) ;
			return ;
		}
		try {
			
			File newLogFile = loggingFacility.instantiateNewFuzzingLogFile() ;
			variables.setNewFuzzingLogFile( newLogFile ) ;
			proxy.startFuzzer( ) ;
		}
		catch (Exception ex ) 
		{
			JOptionPane.showMessageDialog(null, "Unable to start the proxy server." ) ;
		}
	}


	private void buttonStopFuzzing_actionPerformed (ActionEvent e) 
	{ 
		proxy.stopFuzzer() ;
	}

	/**
	 * This function updates updates the number of fuzzing functions
	 * performed on the specified generic messages over the total number
	 * of available fuzzing functions
	 * @param e
	 */
	private void buttonCompletionStatus_actionPerformed (ActionEvent e) 
	{ 
		fieldCompletionStatus.setText( dataManipulator.getCompletionStatus() ) ;
	}

	private void buttonInitFuzzingEngine_actionPerformed (ActionEvent e)
	{ 
		if ( minimizedAutomaton == null )
		{
			JOptionPane.showMessageDialog( null,  "Please minimize the FSA first.") ;
			return ;
		}
		int returnVal = JOptionPane.showConfirmDialog( null, "This operation might take a few minutes. Continue?", "Confirm Action", JOptionPane.YES_NO_OPTION ) ;
		if ( JOptionPane.NO_OPTION ==  returnVal ) 
			return ;

		try { 
			String selectedStr = (String) boxChooseAbstraction.getSelectedItem() ;
			BufferedReader in = new BufferedReader(new FileReader( systemConfig.getSystemConfigPath() + "\\abstractionSource.txt") ) ;
			String str; 
			while ((str = in.readLine()) != null) 
			{ 
				if ( str.startsWith("#") )
					continue ;
				else
				{
					String[] strs = str.split( ":::" ) ;
					if ( strs.length == 3 && strs[0].equalsIgnoreCase( selectedStr ) )
					{
						try {
							Class c = Class.forName( "AbstractionFuncs." + strs[1] );
							dataManipulator.setTraces( traces ) ;
							dataManipulator.initializerFuzzer( minimizedAutomaton,( AbstractionFunct) c.newInstance() ) ;
							tableModelGenericStrings.setDataVector( dataManipulator.getFuzzingData(), columnVector ) ;
							this.fixFuzzingTableColumnWidth() ;
							// We also initialize the cotrol socket object
							cacSocket = new ComAndControlSocket( dataManipulator.getFuzzer() ) ;
							break ;
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(null, "Could not find class " + strs[1] + "/" + strs[2] + " or instantiate the fuzzing engine" ) ;
							return ;
						}							
					}
				}
			} 
			in.close(); 
		} catch (IOException ex ) 
		{
			JOptionPane.showMessageDialog(null, "The source with abstraction functions was not found. Please check AutoFuzz/\\abstractionSource.txt" ) ;
		}
	}


	/**
	 * This function simply adds all the abstraction function names. 
	 * Add your abstraction function in here
	 */
	private void loadAbstractionFuncs()
	{
		try { 
			System.out.println(systemConfig.getSystemConfigPath());
			BufferedReader in = new BufferedReader(new FileReader( systemConfig.getSystemConfigPath() + "\\abstractionSource.txt") ) ;
			String str; 
			while ((str = in.readLine()) != null) 
			{ 
				if ( str.startsWith("#"))
					continue ;
				else
				{
					String[] strs = str.split( ":::" ) ;
					if ( strs.length == 3 )
						absFunctions.add( strs[0] ) ;
					else
					{
						JOptionPane.showMessageDialog(null, "Error parsing abstractionSource.txt. Please check the file syntax.") ;
						System.exit( 0 ) ;
					}
				}
			} 
			in.close(); 
		} catch (IOException ex) 
		{
			JOptionPane.showMessageDialog(null, "The source with abstraction functions was not found. Please check AutoFuzz/abstractionSource.txt" ) ;
		}
	}


	/** 
	 * Parses the XML file into message traces, formats and outputs them to the traffic text area
	 * 
	 * @param traffic The string with the traffic
	 * 
	 * @return True if successfully parsed and loaded in the text area, false otherwise. 
	 */
	private boolean loadTrafficIntoTextArea( String traffic ) 
	{
		ArrayList<MessageTrace> traces = null ;
		try {
			traces = dataParser.getMsgTraces( traffic ) ;
		}
		catch (Exception ex )
		{
			JOptionPane.showMessageDialog( null,  "Could not parse the file. Please check the syntax" ) ;
			return false ;
		}
		if ( traces != null)
		{
			for ( MessageTrace trace : traces )
			{
				textAreaTrafficView.append( "<Trace>" ) ;
				for ( Map<String, ArrayList<String>> msg : trace )
				{				
					Iterator it = msg.entrySet().iterator();
					while ( it.hasNext() ) {
						// IOpaier: key - input string
						//          value - array of output strings
						Map.Entry IOpairs = (Map.Entry)it.next();
						textAreaTrafficView.append( "<Input>") ;
						String msgBody = (String) IOpairs.getKey() ;
						/*
						msgBody = msgBody.replaceAll("&", "&amp;" ) ;
						msgBody = msgBody.replaceAll(">", "&gt;" ) ;
						msgBody = msgBody.replaceAll("<", "&lt;" ) ;		
						msgBody = msgBody.replaceAll("\"", "&quot;" ) ;
						msgBody = msgBody.replaceAll("'", "&apos;" ) ;
						 */
						textAreaTrafficView.append( msgBody ) ;
						textAreaTrafficView.append( "</Input>\n\n") ;
						for ( String s : (ArrayList<String>) IOpairs.getValue() )
						{
							textAreaTrafficView.append( "<Output>") ;
							msgBody = s ;
							/*
							msgBody = msgBody.replaceAll("&", "&amp;" ) ;
							msgBody = msgBody.replaceAll(">", "&gt;" ) ;
							msgBody = msgBody.replaceAll("<", "&lt;" ) ;		
							msgBody = msgBody.replaceAll("\"", "&quot;" ) ;
							msgBody = msgBody.replaceAll("'", "&apos;" ) ;
							 */
							textAreaTrafficView.append( msgBody ) ;
							textAreaTrafficView.append( "</Output>\n\n" ) ;
						}
					}
				}
				textAreaTrafficView.append( "</Trace>\n" ) ;
			}		
			return true ;
		}
		else
			return false ;
	}


	public static void main(String[] args)
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception ex)
		{
			System.out.println("Failed loading L&F: ");
			System.out.println(ex);
		}
		new AutoFuzzguiMain();
	}

	/**
	 * This functions instantiate a table used to output results of the sequence alignment.
	 * It contains information about each transition that will be fuzzed such as its input 
	 * symbol, generic sequence and the percentage of fuzzing functions performed. 
	 */
	private void initGenericStringsTable()
	{
		tableModelGenericStrings = new DefaultTableModel( ) ;
		tableGenericStrings.setModel( tableModelGenericStrings ) ;

		Object[][] data = {
				{" ", " ", " "},
				{" ", " ", " "},
				{" ", " ", " "},
				{" ", " ", " "},
				{" ", " ", " "}	} ;
		dataVector = data ;

		Object[] columns = {"State ID", "Input Sym.", "Generic Message" } ;
		columnVector = columns ;
		tableModelGenericStrings.setDataVector( dataVector, columnVector ) ;
		paneGenericStrings.setViewportView( tableGenericStrings ) ;
		this.fixFuzzingTableColumnWidth() ;
	}
	private void fixFuzzingTableColumnWidth( )
	{
		// Set the column widths
		for ( int i = 0; i < numOfColumns ; i++ ) 
		{
			TableColumn column = tableGenericStrings.getColumnModel().getColumn( i ) ;
			if ( i==0 ) column.setMaxWidth( 100 ) ;
			if ( i==1 ) column.setMaxWidth( 100 ) ;
			if ( i==2 ) column.setMaxWidth( 795 ) ;
		}
	}

	/**
	 * This function loads the environment variables, find the location of Program Files (on windows platform only)
	 * It then checks if the folder with AutoFuzz configuration files is present and it everything
	 * is sucessfull it stores the location of it in the sytemConfigPath variable
	 */
	private void checkSystemConfiguration()
	{
		Map<String, String> env = System.getenv();
		String programFiles = (String) env.get( "ProgramFiles(x86)" ) ;
		if ( programFiles == null )
			programFiles = (String) env.get( "ProgramFiles" ) ;
		
		if ( programFiles == null )
		{
			JOptionPane.showMessageDialog( null, "Unable to read the location of windows \"program files\" from env. variables. Exiting..." ) ;
			System.exit( ERROR ) ; 
		}
		File folder = new File( programFiles ) ;
		File[] listOfFiles = folder.listFiles();

		// Check if AutoFuzz directory is present 
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if ( listOfFiles[i].isDirectory() && listOfFiles[i].getName().equalsIgnoreCase( "AutoFuzz" ) ) 
			{
				systemConfig.setSystemConfigPath( listOfFiles[i].getAbsolutePath() ) ;
				return ;
			}
		}
	}

	/**
	 * @param name The name of the abstraction function class 
	 * 
	 * @return An object of the class name specified in the parameter from abstraction functions source
	 * null if no such object exists
	 * 
	 */
	private AbstractionFunct instantiateAbsFunc( String name )
	{
		Map<String, String> env = System.getenv();
		String programFiles = (String) env.get( "ProgramFiles(x86)" ) ;
		if ( programFiles == null )
			programFiles = (String) env.get( "ProgramFiles" ) ;
		
		if ( programFiles == null )
		{
			JOptionPane.showMessageDialog( null, "Unable to read the location of windows \"program files\" from env. variables. Exiting..." ) ;
			System.exit( ERROR ) ; 
		}

		File file = new File( programFiles + "\\AutoFuzz\\" ) ;
		try { 
			// Convert File to a URL 
			URL url = file.toURL() ; 
			URL[] urls = new URL[]{url} ; 
			// Create a new class loader with the directory 
			ClassLoader cl = new URLClassLoader(urls) ;
			Class cls = cl.loadClass( "AbstractionFuncs." + name ) ; 
			try {
				
				AbstractionFunct f = ( AbstractionFunct ) cls.newInstance() ;
				return f ;
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog( null, "Unable to instantiate abstraction function " + name ) ;
				e.printStackTrace() ;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog( null, "Unable to instantiate abstraction function " + name ) ;

				e.printStackTrace();
			}

		} 
		catch (MalformedURLException e) { } catch (ClassNotFoundException e) 
		{
			JOptionPane.showMessageDialog( null, "Unable to instantiate abstraction function " + name ) ;
		} 
		return null ;
	}
}

