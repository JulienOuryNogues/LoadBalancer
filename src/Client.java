import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Julien on 27/10/2016.
 * @author Julien Oury--Nogues
 * Client simple avec des methodes pour generer des requetes aleatoires
 *
 */
public class Client {
    public static int rand_int(int min, int max)
    {
        return min +(int) (Math.random() * (max - min));
    }

    private static char rand_char()
    {
        String sac="aaaaaaaaabbccdddeeeeeeeeeeeeeeeffgghhiiiiiiiijklllllmmmnnnnnnooooooppqrrrrrrssssssttttttuuuuuuvvwxyz".toUpperCase();
        return sac.charAt(rand_int(0,sac.length()));
    }
    private static String tirage_alea()
    {
        int n = rand_int(3,9);
        String res="";
        for (int i=0; i<n;i++)
        {
            res+=rand_char();
        }
        return res;
    }

    public static void main(String arg[]) {
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc = new Scanner(System.in);//pour lire � partir du clavier

        try {
         /*
         * les informations du serveur ( port et adresse IP ou nom d'hote )
         * 127.0.0.1 est l'adresse local de la machine
         */
            System.out.println("Connecting to the load balancer...");
            clientSocket = new Socket("127.0.0.1", 8000);
            System.out.println("Done ! Initializing threads...");
            //flux pour envoyer
            out = new PrintWriter(clientSocket.getOutputStream());
            //flux pour recevoir
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //thread pour envoyer les donnees
            Thread envoyer = new Thread(new Runnable() {
                String msg;
                @Override
                public void run() {
                    while(true){
                        msg = sc.nextLine(); //des qu'il recoit une ligne
                        out.println(msg);
                        out.flush();
                    }
                }
            });
            envoyer.start();
            //thread pour recevoir les messages
            Thread recevoir = new Thread(new Runnable() {
                String msg;
                @Override
                public void run() {
                    try {
                        msg = in.readLine(); //toujours une ligne
                        while(msg!=null){
                            System.out.println(msg);
                            msg = in.readLine();
                        }
                        System.out.println("Disconnected. Press Enter to close.");
                        clientSocket.close();
                        envoyer.stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            recevoir.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}