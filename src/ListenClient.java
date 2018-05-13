import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Julien on 28/10/2016.
 */
public abstract class ListenClient {

    BufferedReader inperso;
    PrintWriter outperso;

    public ListenClient (PrintWriter out ,BufferedReader in) throws IOException {
        this.inperso = in;
        this.outperso = out;

    }
}
