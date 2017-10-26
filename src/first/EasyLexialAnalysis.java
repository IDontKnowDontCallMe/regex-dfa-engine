package first;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2017/10/22.
 */
public class EasyLexialAnalysis {

    private StringBuilder buffer = new StringBuilder();
    private int preStart = 0;
    private int preEnd = 0;
    private int lineNum = 1;

    private String[] keyWords = {"int", "float", "string"};

    public List<Token> run(File source) throws Exception{

        readSourceToBuffer(source);

        List<Token> result = new ArrayList<Token>();

        while(preEnd < buffer.length()){
            Token t = getNext();
            if(t!=null){
                result.add(t);
                //System.out.println(t);
            }
        }

        return result;

    }

    private void readSourceToBuffer(File source) throws IOException{

        BufferedReader br = new BufferedReader(new FileReader(source));

        String s = "";
        while((s = br.readLine())!=null){
            buffer.append(s+System.lineSeparator());
        }

        br.close();

    }

    private Token getNext() throws Exception{

        if(isBlank(buffer.charAt(preEnd))){
            while(isBlank(buffer.charAt(preEnd))){
                if(buffer.charAt(preEnd)=='\n') lineNum++;
                preEnd++;
                if(preEnd >= buffer.length()){
                    preStart = preEnd;
                    break;
                }
            }
            preStart = preEnd;
            return null;
        }


        if(isLetter(buffer.charAt(preEnd))){

            while(isLetter(buffer.charAt(preEnd)) || isNumber(buffer.charAt(preEnd)) || buffer.charAt(preEnd)=='_'){
                preEnd++;
                if(preEnd >= buffer.length()){
                    break;
                }
            }

            String s = buffer.substring(preStart, preEnd);

            Token t = getKeyWordToken(s);

            if(t!=null) return t;

            preStart = preEnd;

            return new Token("id", s);
        }
        else if(isNumber(buffer.charAt(preEnd))){

            int potNum = 0;

            while( isNumber(buffer.charAt(preEnd)) && potNum < 2 ){
                if(buffer.charAt(preEnd)=='.'){
                    potNum++;
                    continue;
                }
                preEnd++;
            }

            String s = buffer.substring(preStart, preEnd);

            preStart = preEnd;

            return new Token("number", s);

        }
        else if(buffer.charAt(0)=='"'){

            preEnd = preEnd + 1;

            while(buffer.charAt(preEnd)!='"'){
                preEnd++;
                if(preEnd>=buffer.length())
                    throw new Exception("line: " + lineNum + ", '" + buffer.charAt(preStart) + "' can't be identified");
            }

            preEnd++;

            String s = buffer.substring(preStart, preEnd);

            preStart = preEnd;

            return new Token("string", s);

        }
        else if(isOperator(buffer.charAt(preEnd))){
            String s = buffer.charAt(preEnd) + "";
            preEnd++;
            preStart = preEnd;
            return new Token("operator", s);
        }
        else {
            throw new Exception("line: " + lineNum + ", '" + buffer.charAt(preStart) + "' can't be identified");
        }


    }

    private boolean isBlank(char c){
        return c=='\n' || c=='\t' || c==' ' || c=='\r';
    }

    private boolean isLetter(char c){
        return (c>='a'&&c<='z') || (c>='A'&&c<='Z');
    }

    private boolean isNumber(char c){
        return c>='0' && c<='9';
    }

    private boolean isOperator(char c){
        return c=='+' || c=='-' || c=='*' || c=='/' || c=='=' ;
    }


    private Token getKeyWordToken(String word){

        for(int i=0; i<keyWords.length; i++){
            if(keyWords[i].equals(word)){
                return new Token("keyWord", keyWords[i]);
            }
        }

        return null;

    }

}
