package first;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        EasyLexialAnalysis analysis = new EasyLexialAnalysis();

        try{
            List<Token> list = analysis.run(new File("source.txt"));

            for (Token t : list){
                System.out.println(t);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
