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

        String[] tokenPatterns = {"id","splite","number",""};
        String[] regex = {"(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z)&((0|1|2|3|4|5|6|7|8|9)|(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z)|_)*"
                            ,";",  "(0|1|2|3|4|5|6|7|8|9)&(0|1|2|3|4|5|6|7|8|9)"};

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
        for(String s: tokenPatterns){
            tokenPatternsString = tokenPatternsString + "\"" + s + "\",";
        }
        tokenPatternsString = tokenPatternsString.substring(0, tokenPatternsString.length()-1) + "}";
        Template.setTOKEN_PATTERNS(tokenPatternsString);

        writer.write(Template.GET_ANALYER_CLASS());
        writer.flush();
        writer.close();
    }
}
