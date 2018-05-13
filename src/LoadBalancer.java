import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by Julien on 27/10/2016.
 * @author Julien Oury--Nogues
 * Transmets les requêtes aux serveurs
 */
public class LoadBalancer extends GenericServer {
    Vector tabServerssout;
    Vector tabServerssin;
    private PrintWriter out;
    private int current_server =0;

    //constructeur, on le construit comme un serveur !
    public LoadBalancer(int port) throws IOException {
        super(port);
        tabServerssout = new Vector();
        tabServerssin = new Vector();
        //On se connecte aux serveurs
        addServers(8010,8013);
    }

    // cree un client et lance un thread d'ecoute pour lui propre
    public void addClient() throws IOException {
        BufferedReader in;
        Socket clientSocket; //pour chaque nouveau client
        System.out.println("New Client connected.");
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        tabClientsout.addElement(out);
        Runnable r = new recevoir(out, in);
        // genere le Thread pour le nouveau client
        new Thread(r).start();
    }


    public void addServers(int portmin,int portmax){
        //On a une liste deja connue des serveurs
        BufferedReader inloc;
        PrintWriter outloc;
        Socket SSocket;
        for (int portcourant = portmin; portcourant < portmax; portcourant++) {
        try {
                System.out.print("Attempt to connect to server at port ");
                System.out.println(portcourant);
                SSocket = new Socket("127.0.0.1", portcourant);
                System.out.print("Done...");
                //flux pour envoyer
                outloc = new PrintWriter(SSocket.getOutputStream());
                tabServerssout.add(outloc);
                //flux pour recevoir
                inloc = new BufferedReader(new InputStreamReader(SSocket.getInputStream()));
                tabServerssin.add(inloc);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    //Methode roundrobin pour trouver le prochain serveur

    public int round_robin()
    {
        current_server++;
        current_server%=tabServerssout.size();
        return current_server;
    }





    // le thread recevoir pour chaque client

    public class recevoir extends ListenClient implements Runnable {

        String msg;

        public recevoir(PrintWriter out,BufferedReader in) throws IOException {
            super(out, in);
        }

        public void run() {
            boolean deco = false;
            int index;
            BufferedReader inserver;
            PrintWriter outserver;
            while (!deco) {
                try {
                    msg = inperso.readLine();
                    if (msg.equals("quit()")) {
                        deco = true;
                        break;
                    }
                index = round_robin();
                outserver =(PrintWriter) tabServerssout.elementAt(index);
                outserver.println(msg);
                outserver.flush();
                inserver =(BufferedReader) tabServerssin.elementAt(index);
                //il vaut mieux lancer un Thread sur lui plutot que de garder l'aspect bloquant
                outperso.println(inserver.readLine());
                outperso.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //je ferme le out si deconnexion
            System.out.println("Client disconnected.");
            outperso.close();
            try {
                inperso.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        LoadBalancer LB;
        try {
            // les clients se connectent sur le port 8000
            LB = new LoadBalancer(8000);
        } catch (IOException e) {
            LB = null; // tant pis si ca plante
            e.printStackTrace();
        }
        final boolean[] deco = {false};


        // un thread listen qui ne fait qu'accepter les connexions, et lance le nouveau thread associé

        final LoadBalancer finalLB = LB;
        Thread Listen = new Thread(new Runnable() {
            @Override
            public void run() {
                // thread qui ne fait que rajouter des connexions
                try {
                    while (true) {
                        finalLB.addClient();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        Listen.start();


        Thread decopropre = new Thread(new Runnable() {
            String msg;
            @Override
            public void run() {
                while (!deco[0]) {
                    msg = finalLB.sc.nextLine();
                    if (msg.equals("quit()")) {
                        deco[0] = true;
                    }
                }
                //si on arrive la, c'est qu'on deco !
                PrintWriter out;
                for (int i = 0; i < finalLB.tabClientsout.size(); i++) {
                    out = (PrintWriter) finalLB.tabClientsout.elementAt(i);
                    if (out != null) {
                        out.close();
                    }
                }
                Listen.stop(); //je kill le listen
                try {
                    finalLB.serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        decopropre.start();
    }
}
