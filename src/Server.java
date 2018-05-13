import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Julien on 27/10/2016.
 * @author Julien Oury--Nogues
 */
public class Server extends GenericServer {
    Scrabble scr=new Scrabble();

    //constructeur
    public Server(int port) throws IOException {
        super(port);
    }


    // cree un client et lance un thread d'ecoute pour lui propre
    // Ici, ce sont les sockets du Load Balancer
    public void addClient() throws IOException{
        BufferedReader in;
        PrintWriter out;
        Socket clientSocket; //pour chaque nouveau client
        System.out.println("Load Balancer detected");
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream());
        in = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));
        tabClientsout.addElement(out);
        Runnable r = new recevoir(out, in);
        new Thread(r).start();
    }

    public class recevoir extends ListenClient implements Runnable  {

        String msg;

        public recevoir(PrintWriter out ,BufferedReader in) throws IOException {
            super(out, in);
        }

        public void run() {
            boolean deco = false;
            String res;
            while (!deco) {
                try {
                    System.out.println("Waiting for infos...");
                    msg = inperso.readLine();
                    System.out.println(msg);
                    if (msg.equals("quit()")) {
                        deco = true;
                        continue;
                    }
                    //traitement de l'information
                    res=scr.join(scr.TousMots(msg,scr.lexique));
                    outperso.println("Mots faisables via votre tirage : "+res);
                    outperso.flush();

                } catch (IOException e) {
                    deco=true;
                    e.printStackTrace();
                }
            }
            outperso.close();
            try {
                inperso.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }



    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(8010) ;
        } catch (IOException e) {
            server=null; // tant pis si ca plante
            e.printStackTrace();
        }
        final boolean[] deco = {false};


        // un thread listen qui ne fait qu'accepter les connexions, et lance le nouveau thread

        final Server finalServer = server;
        Thread AddClient = new Thread(new Runnable (){

            public void run() {
                // thread qui ne fait que rajouter des connexions
                try {
                    while(true){
                        finalServer.addClient();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        AddClient.start();



        Thread decopropre= new Thread(new Runnable() {
            String msg;
            @Override
            public void run() {
                while(!deco[0]){
                    msg = finalServer.sc.nextLine();
                    if (msg.equals("quit()")){
                        deco[0] =true;
                    }
                }
                //si on arrive là, c'est que l'on déco
                PrintWriter out;
                //on ferme tout
                for (int i=0;i<finalServer.tabClientsout.size();i++){
                    out = (PrintWriter) finalServer.tabClientsout.elementAt(i);
                    if (out!=null){
                        out.close();
                    }
                }
                AddClient.stop(); //je kill le listen
                try {
                    finalServer.serverSocket.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        });
        decopropre.start();
    }
}
