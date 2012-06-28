#include <stdio.h>
#include <windows.h>

int main( int argc, char **argv )
{
	if( argc > 1 )
	{
		HANDLE hCommPort = CreateFile(argv[1], GENERIC_READ|GENERIC_WRITE, 0,NULL, OPEN_EXISTING,FILE_FLAG_OVERLAPPED,NULL);

		if( hCommPort == INVALID_HANDLE_VALUE )
		{
			printf( "Failed opeing port\n" );
		}
		else
		{
			printf( "I think the port is open\n" );

			COMMTIMEOUTS cto = { 2, 1, 1, 0, 0 };
			if( ! SetCommTimeouts( hCommPort, &cto ) )
			{
				printf( "Failed SetCommTimeouts\n" );
			}
			else
			{
				printf( "Success SetCommTimeouts\n" );
			}

			DCB dcb;
			memset(&dcb,0,sizeof(dcb));
			dcb.BaudRate = 19200;
			dcb.ByteSize = 8;
			dcb.Parity = NOPARITY;
			dcb.StopBits = ONESTOPBIT;

			if( SetCommState( hCommPort, &dcb ) )
			{
				printf( "SetCommState returned true\n" );
			}
			else
			{
				printf( "SetCommState returned false\n" );
			}	
		}
	}
	else
	{
		printf( "Pass the comm port in as a parameter. i.e. SetCommState COM3\n" );
	}
}
