package com.googlecode.jamr;

import gnu.io.*;

public class App {

	public static void main(String[] args) throws Exception {

		if( args.length < 1 )
		{
			System.out.println( "Provide port as arg" );

			java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
			while (portEnum.hasMoreElements()) {
				CommPortIdentifier portIdentifier = portEnum.nextElement();
				System.out.println( "\t" + portIdentifier.getName() );
			}

			System.exit( 1 );
		}

		String portName = args[0];
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned())
		{
			System.out.println( "port " + portIdentifier.getName() + " is currently in use");
		}
		else
		{
			CommPort commPort = portIdentifier.open("JAMR", 2000);
			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				java.io.InputStream in = serialPort.getInputStream();

				SerialReader sr = new SerialReader(in);
				serialPort.addEventListener(sr);
				serialPort.notifyOnDataAvailable(true);
			}
		}
	}
}
