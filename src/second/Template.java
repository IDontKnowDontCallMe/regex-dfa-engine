package second;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by USER on 2017/10/26.
 */
public class Template {

    public static String TOKEN_PATTERNS = "";
    public static void setTOKEN_PATTERNS(String string){
        Template.TOKEN_PATTERNS = string;
    }

    public static String AUTOMATE_METHOD = "";
    public  static void setAutomate_Method(String string){
        Template.AUTOMATE_METHOD = string;
    }

    public static  void ADD_DFA_STATE(DFAState dfaState){

        List<String> ifSet = new ArrayList<>();
        ifSet.add("case "+ dfaState.getValue()  +":\n");


        boolean useIf = true;
        for(Map.Entry<Character, DFAState> entry: dfaState.getEdges().entrySet()){
            String temp = (useIf?"if":"else if") + "(action=='" + entry.getKey() + "'){\n"+
                                "result = "+ entry.getValue().getValue() +";\n";

            if(entry.getValue().getCorrEndNFAState()!=null){
                temp += "hasMatched = true;\n" +
                        "preOK = preEnd;\n"+
                        "patternIndex="+entry.getValue().getCorrEndNFAState().getPriority()+";\n";
            }

            temp += "}\n";

            ifSet.add(temp);

            if(useIf){
                useIf=false;
            }
        }

        ifSet.add("return result;\n");

        for(String s: ifSet){
            Template.AUTOMATE_METHOD += s;
        }

    }

    public static String GET_ANALYER_CLASS(){

        return
                "import java.io.BufferedReader;\n" +
                        "import java.io.File;\n"+
                        "import java.io.FileReader;\n"+
                        "import java.io.IOException;\n"+

                        "public class LexialAnalyer {\n"+

                        "private StringBuilder buffer = new StringBuilder();\n"+
                        "private int preStart = 0;\n"+
                        "private int preEnd = 0;\n"+

                        "private int preOK = 0;\n"+
                        "private int patternIndex = 0;\n"+
                        "boolean hasMatched = false;\n"+

                        "private String[] tokenPatterns = " + TOKEN_PATTERNS + ";\n"+


                        "public void analyze(File file){\n"+
                        "try {\n"+
                        "readSourceToBuffer(file);\n"+
                        "System.out.println(\"------------source file content:------------\");\n"+
                        "System.out.println(buffer);\n"+
                        "System.out.println(\"------------source file content end------------\");\n"+
                        "System.out.println();\n"+
                        "System.out.println(\"------------pattern list------------\");\n"+
                        "for(String s: tokenPatterns){\n"+
                        "System.out.print(s+\",\");\n"+
                        "}\n"+
                        "System.out.println();\n"+
                        "System.out.println(\"------------pattern list------------\");\n"+
                        "} catch (IOException e) {\n"+
                        "System.out.println(\"I/O Exception!\");\n"+
                        "return;\n"+
                        "}\n"+

                        "hasMatched = false;\n"+
                        "int nextState = 0;\n"+
                        "while (true) {\n"+
                        "if(preEnd>=buffer.length()){\n"+
                        "if(hasMatched){\n"+
                        "String word = buffer.substring(preStart, preOK+1);\n"+
                        "System.out.println(\"<\"+tokenPatterns[patternIndex]+\",\"+word+\">\");\n"+
                        "System.out.println(\"completed!!!\");\n"+
                        "}\n"+
                        "else {"+
                        "System.out.println(\"Can't match, program stop at \" + preEnd);\n"+
                        "return;\n"+
                        "}\n"+
                        "return;\n"+
                        "}\n"+

                        "nextState = automate(nextState, buffer.charAt(preEnd));\n"+

                        "if(nextState == -1){\n"+
                        "if(hasMatched){\n"+
                        "hasMatched = false;\n"+
                        "nextState = 0;\n"+
                        "String word = buffer.substring(preStart, preOK+1);\n"+
                        "preStart = preEnd = preOK+1;\n"+
                        "System.out.println(\"<\"+tokenPatterns[patternIndex]+\",\"+word+\">\");\n"+
                        "}\n"+
                        "else {"+
                        "System.out.println(\"can't match, program stop at \" + preEnd);\n"+
                        "return;\n"+
                        "}\n"+
                        "}\n"+
                        "else {\n"+
                        "preEnd++;\n"+
                        "}\n"+

                        "}\n"+

                        "}\n"+


                        "private int automate(int present, char action){\n"+
                        "int result=-1;\n"+
                        "switch (present){\n"+


                        Template.AUTOMATE_METHOD +

                        "default: return-1;\n"+
                        "}\n"+

                        "}\n"+

                        "private void readSourceToBuffer(File source) throws IOException {\n"+

                        "BufferedReader br = new BufferedReader(new FileReader(source));\n"+

                        "String s = \"\";\n"+
                        "while((s = br.readLine())!=null){\n"+
                        "buffer.append(s);\n"+
                        "}\n"+

                        "br.close();\n"+
                        "}\n"+


                        "public static void main(String[] args){\n"+

                        "LexialAnalyer lexialAnalyer = new LexialAnalyer();\n"+

                        "lexialAnalyer.analyze(new File(args[0]));\n"+



                        "}\n"+

                        "}\n"


                ;

    }


}
