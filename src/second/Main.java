package second;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Created by USER on 2017/10/25.
 */
public class Main {

    public static void main(String[] args) throws IOException{

        String[] tokenPatterns = {"firstPattern","secondPattern"};
        String[] regex = {"d&s|b&c*", "c&d&s"};

        NFAGenerator nfaGenerator = new NFAGenerator();
        NFA nfa =  nfaGenerator.getNFA(regex);

        DFAGenerator dfaGenerator = new DFAGenerator();
        List<DFAState> dfa = dfaGenerator.getDFA(nfa);

        for(DFAState dfaState: dfa){

            Template.ADD_DFA_STATE(dfaState);

        }


        FileOutputStream writerStream = new FileOutputStream("LexialAnalyer.java");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));

        String tokenPatternsString = "{";
        for(String s: regex){
            tokenPatternsString = tokenPatternsString + "\"" + s + "\",";
        }
        tokenPatternsString = tokenPatternsString.substring(0, tokenPatternsString.length()-1) + "}";
        Template.setTOKEN_PATTERNS(tokenPatternsString);

        writer.write(Template.GET_ANALYER_CLASS());
        writer.flush();
        writer.close();
    }
}
