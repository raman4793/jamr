package com.googlecode.jamr;

//import gnu.io.*;
import com.engidea.comm.*;

public class App {

	private SerialPort serport;

	public static void main(String[] args) throws Exception {
		System.out.println( System.getProperty( "os.name" ) );
		App a = new App();
		a.start( args );
	}

	private void start( String[] args ) throws Exception
	{
		CommPortIdentifier portIdentifier = new com.engidea.win32jcom.WinjcomIdentifier(0);

		if( args.length < 1 )
		{
			System.out.println( "Provide port as arg" );

			java.util.List portlist = portIdentifier.getCommPortList();
			for( java.util.Iterator iter=portlist.iterator(); iter.hasNext(); )
			{
				CommPort identifier = (CommPort)iter.next();
				System.out.println( "\t" + identifier.getName() );
			}

			System.exit( 1 );
		}

		String portName = args[0];
		CommPort aPort = portIdentifier.getCommPort(portName);
		aPort.open();

		/*
			The following line throws:
			Exception in thread "main" java.io.IOException: fun(SetSerialPortParams) msg(SetCommState FAIL) err( 31:A device attached to the system is not functioning.)
				at com.engidea.win32jcom.WinjcomPort.nativeSetSerialPortParams(Native Method)
				at com.engidea.win32jcom.WinjcomPort.setSerialPortParams(WinjcomPort.java:105)
				at com.googlecode.jamr.App.start(App.java:37)
				at com.googlecode.jamr.App.main(App.java:13)

			((SerialPort)aPort).setSerialPortParams(9600,8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		*/
		serport = (SerialPort)aPort;

		new Thread(new PortReader()).start();
	}

	private final class PortReader implements Runnable
	  {
	  public void run()
	    {
	    try
	      {
	      // This will timeout if nothing is received in the specified time.
	      byte []buff = new byte[1];
	      while (  serport.read(buff,0,buff.length) > 0 )
		{
		// NOTE: you should be checking the encoding !
		System.out.print(new String(buff));
		}
	      }
	    catch ( Exception exc )
	      {
	      exc.printStackTrace();
	      }
	    }
	  }

}
