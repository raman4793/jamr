package com.googlecode.jamr;

import gnu.io.*;

public class SerialReader implements SerialPortEventListener {

	private java.io.InputStream in;
	private byte[] buffer = new byte[1024];

	public SerialReader(java.io.InputStream in) {
		this.in = in;
	}

	public void serialEvent(SerialPortEvent arg0) {
		int data;

		 try {
                        int len = 0;
                        while ((data = in.read()) > -1) {
                                if (data == '\n') {
                                        break;
                                }
                                buffer[len++] = (byte) data;
                        }
                        String line = new String(buffer, 0, len);
                        System.out.println( " - " + line + " - " );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}	
