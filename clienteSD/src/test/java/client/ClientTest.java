package client;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import static org.junit.Assert.assertTrue;


public class ClientTest {

    private static Socket socket;

    private InteractiveClient interactiveClient;

    private ReceptorClient receptorClient;


    @Before
    public void setUp()
        throws IOException {

        // Should start server before executing any tests
        // TODO make it automated

        Properties properties = Client.getProperties();

        String hostname = properties.getProperty( "server.hostname" );
        int port = Integer.parseInt( properties.getProperty( "server.port" ) );

        InetAddress address = InetAddress.getByName( hostname );
        socket = new Socket( address, port );

        interactiveClient = new InteractiveClient( socket );
        receptorClient = new ReceptorClient( socket );
    }


    @After
    public void closeUp()
        throws IOException {

        socket.close();
    }


    @Test
    public void createTest()
        throws IOException {

        BufferedReader reader = receptorClient.getReaderFromServer();

        prepareServerForTestOneCenario( reader );

        String createCommand = "1;1;primeiro";
        sendCommandGetServerResponseAndAssert( reader, createCommand, "Success" );

        sendCommandGetServerResponseAndAssert( reader, createCommand, "Fail" );

        readCommand( reader, "primeiro" );

    }

    // Read test has been already tested on every other tests


    @Test
    public void updateTest()
        throws IOException {

        BufferedReader reader = receptorClient.getReaderFromServer();

        prepareServerForTestOneCenario( reader );

        String updateCommand = "3;1;primeiro";
        sendCommandGetServerResponseAndAssert( reader, updateCommand, "Fail" );

        String createCommand = "1;1;Kinsela";
        sendCommandGetServerResponseAndAssert( reader, createCommand, "Success" );

        readCommand( reader, "Kinsela" );

        sendCommandGetServerResponseAndAssert( reader, updateCommand, "Success" );

        readCommand( reader, "primeiro" );

    }


    @Test
    public void deleteTest()
        throws IOException {

        BufferedReader reader = receptorClient.getReaderFromServer();

        prepareServerForTestOneCenario( reader );

        String deleteCommand = "4;1";
        sendCommandGetServerResponseAndAssert( reader, deleteCommand, "Fail" );

        String createCommand = "1;1;Kinsela";
        sendCommandGetServerResponseAndAssert( reader, createCommand, "Success" );

        sendCommandGetServerResponseAndAssert( reader, deleteCommand, "Success" );

        String readCommand = "2;1";
        sendCommandGetServerResponseAndAssert( reader, readCommand, "Fail" );
    }


    private void sendCommandGetServerResponseAndAssert( BufferedReader reader, String updateCommand, String returnCode )
        throws IOException {

        interactiveClient.sendRequestToServer( updateCommand );
        String response = reader.readLine();

        assertTrue( response.startsWith( returnCode ) );
    }


    private void prepareServerForTestOneCenario( BufferedReader reader )
        throws IOException {

        interactiveClient.sendRequestToServer( "4;1" );
        reader.readLine();
    }


    private void readCommand( BufferedReader reader, String value )
        throws IOException {

        String response;
        String readCommand = "2;1";

        interactiveClient.sendRequestToServer( readCommand );
        response = reader.readLine();

        assertTrue( response.startsWith( "Success" ) );
        assertTrue( response.contains( value ) );
    }
}
