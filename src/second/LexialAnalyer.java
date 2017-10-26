package second;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by USER on 2017/10/26.
 */
public class LexialAnalyer {

    private StringBuilder buffer = new StringBuilder();
    private int preStart = 0;
    private int preEnd = 0;

    private int preOK = 0;
    private int patternIndex = 0;
    private boolean hasMatched = false;

    private String[] tokenPatterns = {};


    public void analyze(File file){
        try {
            readSourceToBuffer(file);
            System.out.println(buffer);
            for(String s: tokenPatterns){
                System.out.print(s+",");
            }
        } catch (IOException e) {
            System.out.println("I\\O Exception!");
            return;
        }

        hasMatched = false;
        int nextState = 0;
        while (true) {
            if(preEnd>=buffer.length()){
                if(hasMatched){
                    String word = buffer.substring(preStart, preOK+1);
                    System.out.println("<"+tokenPatterns[patternIndex]+"，"+word+">");
                }
                else {
                    System.out.println("Can't match, program stop!!!");
                    return;
                }
                return;
            }

            nextState = automate(nextState, buffer.charAt(preEnd));

            if(nextState == -1){
                if(hasMatched){
                    hasMatched = false;
                    nextState = 0;
                    String word = buffer.substring(preStart, preOK+1);
                    preStart = preEnd = preOK+1;
                    System.out.println("<"+tokenPatterns[patternIndex]+"，"+word+">");
                }
                else {
                    System.out.println("can't match, program stop!!!");
                    return;
                }
            }
            else {
                preEnd++;
            }

        }

    }


    private int automate(int present, char action){
        int result=-1;
        switch (present){



            default: return result;
        }

    }

    private void readSourceToBuffer(File source) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(source));

        String s = "";
        while((s = br.readLine())!=null){
            buffer.append(s);
        }

        br.close();
    }

    public static void main(String[] args){

        LexialAnalyer lexialAnalyer = new LexialAnalyer();

        lexialAnalyer.analyze(new File(args[0]));

    }


}
