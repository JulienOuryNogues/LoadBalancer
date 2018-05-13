import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Julien on 28/10/2016.
 */
public class Scrabble {

    Vector<String> lexique=new Vector<>();

    public Scrabble()
    {
        loadLexique();
    }


    private Vector str_to_chr(String tirage)
    {
        Vector res = new Vector<>();
        for (int i = 0, n = tirage.length(); i < n; i++) {
            char c = tirage.charAt(i);
            res.addElement(c);
        }
        return res;
    }

    private void loadLexique()
    {
        try (BufferedReader br = new BufferedReader(new FileReader("lexiqueFR.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                lexique.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Vector<String> TousMots(String tirage, Vector<String> lexique)
    {
        Vector<String> res = new Vector<>();
        Vector tirage2 = str_to_chr(tirage.toUpperCase());
        for (int i=0; i<lexique.size(); i++)
        {
            if (peut_ecrire(lexique.elementAt(i),tirage2))
            {
                res.add(lexique.elementAt(i));
            }
        }

        return res;
    }

    private void print_vect(Vector tab)
    {
        Iterator i = tab.iterator();
        while (i.hasNext()) {
            System.out.println(i.next());
        }
    }

    private Vector deep_copy(Vector initial)
    {
        Vector res = new Vector();
        Iterator i = initial.iterator();
        while (i.hasNext()) {
            res.addElement(i.next());
        }
        return res;
    }


    private boolean peut_ecrire(String mot, Vector tirage)
    {
        Vector tiragecpy = deep_copy(tirage);
        for (int i = 0, n = mot.length(); i < n; i++) {
            char c = mot.charAt(i);
            if (tiragecpy.contains(c))
            {
                tiragecpy.remove(tiragecpy.indexOf(c));
            }else{
                return false;
            }
        }
        return true;
    }

    public String join(Vector<String> tab)
    {
        String res="";
        Iterator i = tab.iterator();
        while (i.hasNext()) {
            res+=(String) i.next()+",";
            //System.out.println(i.next());
        }
        return res;
    }


    // Quelques tests
    /*
    public static void main(String[] args) {
        Scrabble s = new Scrabble();

        //print_vect(lexique);

        String res="";
        Vector tirage= s.str_to_chr("oiseauseffsdfsd");
        System.out.println(s.peut_ecrire("oiseau",tirage));
        res=s.join(s.TousMots("oiseauj",s.lexique));
        System.out.println(res); // OK !


    }*/


}
