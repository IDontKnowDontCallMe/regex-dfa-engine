package second;

import java.util.*;

/**
 * Created by USER on 2017/10/25.
 */
public class DFAState {

    public static int number = 0;

    public static DFAState getNewDFAState(){
        return new DFAState(DFAState.number++);
    }

    public static DFAState getNewDFAState(Set<NFAState> nfaStateSet){
        DFAState dfa = new DFAState(DFAState.number++);

        dfa.corrNFAStates.addAll(nfaStateSet);

        for(NFAState nfaState: nfaStateSet){
            if(nfaState.getPriority()!=Integer.MAX_VALUE){
                if(dfa.corrEndNFAState==null){
                    dfa.corrEndNFAState = nfaState;
                }
                else {
                    if(dfa.corrEndNFAState.getPriority() > nfaState.getPriority()){
                        dfa.corrEndNFAState = nfaState;
                    }
                }
            }
        }

        return dfa;
    }


    private int value;

    private Map<Character, DFAState> edges = new HashMap<>();
    //对应的优先级最高的NFAState,用来找到对应的匹配regex, null表示这个DFAState不是终态
    private NFAState corrEndNFAState = null;
    //对应的全部NFA状态,用来判断两个DFA状态是否相同
    public Set<NFAState> corrNFAStates = new HashSet<NFAState>();

    public DFAState(int value){
        this.value = value;
    }



    public void addNFAState(NFAState nfaState){
        corrNFAStates.add(nfaState);

        if(nfaState.getPriority()!=Integer.MAX_VALUE){
            if(this.corrEndNFAState==null){
                this.corrEndNFAState = nfaState;
            }
            else if(this.corrEndNFAState.getPriority() < nfaState.getPriority()){
                this.corrEndNFAState = nfaState;
            }
        }
    }

    public void addNFAStates(Set<NFAState> nfaStateSet){

        for(NFAState nfaState: nfaStateSet){
            this.addNFAState(nfaState);
        }

    }

    public void addEdge(char move, DFAState dfaState){
        edges.put(move, dfaState);
    }

    public NFAState getCorrEndNFAState(){
        return this.corrEndNFAState;
    }

    public void setCorrEndNFAState(NFAState nfaState){
        this.corrEndNFAState = nfaState;
    }

    public Set<NFAState> getCorrNFAStates(){
        return this.corrNFAStates;
    }

    public int getValue(){
        return this.value;
    }

    public void setValue(int value){
        this.value = value;
    }

    public Map<Character, DFAState> getEdges(){
        return this.edges;
    }

    public DFAState getEdgeOfMove(char move){
        return edges.get(move);
    }

    @Override
    public boolean equals(Object o){
        if(o==null) return false;
        if(o==this) return true;
        if(!(o instanceof DFAState)) return false;

        DFAState ds = (DFAState)o;

        //只要对应的nfa集合相等就相等
        return this.corrNFAStates.equals(ds.getCorrNFAStates());
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(this.value);
    }

    @Override
    public String toString(){

        String result = "";
        String endStateString = this.corrEndNFAState==null? "not end state": "is end state of "+this.getCorrEndNFAState().getPriority();
        result = "<" + this.value + ", " + endStateString + "> [";

        for(NFAState nfaState: this.corrNFAStates){
            result = result + nfaState.getValue() + ",";
        }
        result = result + "] ";

        for(Map.Entry<Character, DFAState> entry: edges.entrySet()){

            result += "(" + entry.getKey() + ", " + entry.getValue().getValue() + ")";

        }

        return result;
    }

}
