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
		NRSerialPort serial = new NRSerialPort( portName, 115200 );                          
		serial.connect();                                 
		java.io.DataInputStream ins = new java.io.DataInputStream( serial.getInputStream() );                         

		int data;
		byte[] buffer = new byte[1024];
		try
		{
			int len = 0;
			while ((data = ins.read()) > -1)	
			{
				if (data == '\n')
				{
					break;
				}
				buffer[len++] = (byte) data;
			}
			String line = new String(buffer, 0, len);
			System.out.println( " - " + line + " - " );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		/*
		DataOutputStream outs = new DataOutputStream(serial.getOutputStream());
		byte b = ins.read();
		outs.write(b); 

		serial.disconnect();
		*/
	}

}
