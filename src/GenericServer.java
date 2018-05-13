import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by Julien on 28/10/2016.
 */
public abstract class GenericServer {

    public ServerSocket serverSocket;
    private int port;
    Vector tabClientsout;
    Scanner sc;

    //constructeur
    public GenericServer(int port) throws IOException {
        System.out.println("Initialising connexion at port "+port);
        this.port = port;
        this.sc=new Scanner(System.in);
        this.tabClientsout=new Vector();
        this.serverSocket=new ServerSocket(port);

    }

}
