package second;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by USER on 2017/10/25.
 */
public class NFAGenerator {

    private List<String> suffixRegexs = new ArrayList<>();
    //存储每个regex对应的NFA
    private List<NFA> nfas = new ArrayList<>();



    public NFA getNFA(String[] infixs){

        for(String infix: infixs){
            //System.out.println(getSuffixFromInfix(infix));
            suffixRegexs.add(getSuffixFromInfix(infix));
        }

        for(String suffix: suffixRegexs){

            NFA nfa = getNFAFromSuffix(suffix);
            nfas.add(nfa);
        }

        //test
        for(NFA nfa: nfas){
            for(Map.Entry<Integer, NFAState> entry: nfa.getAllStates().entrySet()){
                System.out.println(entry.getValue());
            }
        }

        //给每个终态加上对应的优先级
        int priority = 0;
        for(NFA nfa: nfas){
            nfa.getEndState().setPriority(priority++);
        }

        if(nfas.size() == 1){
            return nfas.get(0);
        }
        else {
            NFA nfa = nfas.get(0);
            int index = 1;
            while(index < nfas.size()){
                nfa = getOrNFA(nfa, nfas.get(index));
                index++;
            }
            return nfa;
        }

        //return null;

    }

    //转中缀至后缀
    private String getSuffixFromInfix(String infix){

        StringBuilder result = new StringBuilder("");

        Stack<Character> operatorStack = new Stack<>();

        for(int i=0; i < infix.length(); i++){

            char c = infix.charAt(i);

            if(isOperator(c)){

                //左括号直接压栈
                if(c == '('){
                    operatorStack.push(c);
                }
                //右括号，出栈直到遇到第一个左括号
                else if(c == ')'){
                    while(true){
                        char oper = operatorStack.pop();
                        if(oper != '('){
                            result.append(oper);
                        }
                        else {
                            break;
                        }
                    }
                }
                else {

                    while(true){

                        if(operatorStack.size()==0){
                            operatorStack.push(c);
                            break; //空栈时，直接压栈退出
                        }

                        char topOper = operatorStack.peek();

                        if(getPriorityOfOperator(topOper) >= getPriorityOfOperator(c)){
                            result.append(operatorStack.pop());
                        }
                        else {
                            operatorStack.push(c);
                            break;
                        }

                    }
                }

            }
            else {
                result.append(c);
            }
        }

        while(operatorStack.size() !=0 ){
            result.append(operatorStack.pop());
        }

        return result.toString();
    }

    private boolean isOperator(char c){

        return c=='&' || c=='|' || c=='*' || c=='(' || c==')';
    }

    private int getPriorityOfOperator(char oper){

        switch (oper){
            case '|':
                return 1;
            case '&':
                return 2;
            case '*':
                return 3;
            default:
                return 0;
        }

    }

    private NFA getNFAFromSuffix(String suffix){

        //存扫描了正则后缀前面一些字符时生成的nfa
        Stack<NFA> tempNFA = new Stack<>();

        for(int i=0; i < suffix.length(); i++){

            char c = suffix.charAt(i);

            if(isRegexOperator(c)){
                NFA nfa1 = null;
                NFA nfa2 = null;
                switch (c){
                    case '*':
                        nfa1 = tempNFA.pop();
                        nfa1 = getManyNFA(nfa1);
                        tempNFA.push(nfa1);
                        break;
                    case '&':
                        nfa2 = tempNFA.pop();
                        nfa1 = tempNFA.pop();
                        tempNFA.push(getAndNFA(nfa1, nfa2));
                        break;
                    case '|':
                        nfa2 = tempNFA.pop();
                        nfa1 = tempNFA.pop();
                        tempNFA.push(getOrNFA(nfa1, nfa2));
                        break;
                    default:
                        break;
                }
            }
            else {
                NFA nfa = getCharNFA(c);
                tempNFA.push(nfa);
            }

        }

        return tempNFA.pop();
    }


    private boolean isRegexOperator(char c){
        return c=='*' || c=='&' || c=='|';
    }

    private NFA getEpsilonNFA(){

        NFAState start = NFAState.getNewNFAState();
        NFAState end = NFAState.getNewNFAState();

        start.addEpsilonEdge(end);

        return new NFA(start, end);
    }

    private NFA getCharNFA(char c){
        NFAState start = NFAState.getNewNFAState();
        NFAState end = NFAState.getNewNFAState();

        start.addEdge(c, end);

        return new NFA(start, end);
    }

    private NFA getOrNFA(NFA nfa1, NFA nfa2){

        NFAState start = NFAState.getNewNFAState();
        NFAState end = NFAState.getNewNFAState();

        NFA nfa = new NFA(start, end);

        //新nfa的开始节点有两条epsilon边指向两个or组合的nfa
        nfa.getStartState().addEpsilonEdge(nfa1.getStartState());
        nfa.getStartState().addEpsilonEdge(nfa2.getStartState());
        //两个or组合的nfa的结束节点分别有epsilon边指向新nfa的结束节点
        nfa1.getEndState().addEpsilonEdge(nfa.getEndState());
        nfa2.getEndState().addEpsilonEdge(nfa.getEndState());
        //添加所有的边
        nfa.getAllStates().putAll(nfa1.getAllStates());
        nfa.getAllStates().putAll(nfa2.getAllStates());

        return nfa;
    }

    private NFA getAndNFA(NFA nfa1, NFA nfa2){

        NFA nfa = new NFA(nfa1.getStartState(), nfa2.getEndState());

        nfa1.getEndState().addEpsilonEdge(nfa2.getStartState());

        nfa.getAllStates().putAll(nfa1.getAllStates());
        nfa.getAllStates().putAll(nfa2.getAllStates());

        return nfa;
    }

    private NFA getManyNFA(NFA nfa){

        NFAState start = NFAState.getNewNFAState();
        NFAState end = NFAState.getNewNFAState();

        //开始节点epsilon到原开始节点
        start.addEpsilonEdge(nfa.getStartState());
        //原结束节点epsilon到结束节点
        nfa.getEndState().addEpsilonEdge(end);
        //原结束节点epsilon到原开始节点
        nfa.getEndState().addEpsilonEdge(nfa.getStartState());
        //开始节点epsilon到结束节点
        start.addEpsilonEdge(end);

        //原nfa设置新的开始和结束
        nfa.changeStart(start);
        nfa.changeEnd(end);

        return nfa;
    }



}
