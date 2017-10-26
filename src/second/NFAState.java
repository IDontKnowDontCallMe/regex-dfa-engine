package second;

import java.util.*;

/**
 * Created by USER on 2017/10/25.
 */
public class NFAState {

    public static int number = 0;

    public static NFAState getNewNFAState(){
        return new NFAState(NFAState.number++);
    }

    //value of this state
    private int value;
    //epsilon edge
    private Set<NFAState> epsilonEdges = new HashSet<>();
    //edge ---char---> states
    private Map<Character, Set<NFAState>> edges = new HashMap<>();
    //优先级, Integer.MAX_VALUE表示这个不是一个终态，数字越小优先级越高
    private int priority = Integer.MAX_VALUE;

    public NFAState(int value){
        this.value = value;
    }

    public void addEdge(Character c, NFAState nfaState){

        if(edges.get(c)==null){
            edges.put(c, new HashSet<NFAState>());
        }

        edges.get(c).add(nfaState);

    }

    public void addEpsilonEdge(NFAState nfaState){
        epsilonEdges.add(nfaState);
    }

    public Set<NFAState> getEpsilonEdges(){
        return this.epsilonEdges;
    }

    public Set<Character> getAllCharsOfEdges(){
        Set<Character> result = new HashSet<>();

        for(Map.Entry<Character, Set<NFAState>> entry: edges.entrySet()){
            result.add(entry.getKey());
        }

        return result;
    }

    public Set<NFAState> getMoveEdges(char move){
        if(edges.get(move)==null){
            return new HashSet<>();
        }
        else {
            return edges.get(move);
        }
    }

    public int getValue(){
        return this.value;
    }

    public void setPriority(int priority){
        this.priority = priority;
    }

    public int getPriority(){
        return this.priority;
    }

    @Override
    public boolean equals(Object o){

        if(o==null) return false;
        if(o==this) return true;

        if(!(o instanceof NFAState)) return false;

        NFAState s = (NFAState)o;

        return this.value == s.value;
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(this.value);
    }

    @Override
    public String toString(){

        String result = "<id, " + this.value + ">";

        for(NFAState nfaState : epsilonEdges){
            result = result + " (epsilon, " + nfaState.getValue() + ")";
        }

        for(Map.Entry<Character, Set<NFAState>> entry: edges.entrySet()){
            for(NFAState nfaState: entry.getValue()){
                result = result + " (" + entry.getKey() + ", " + nfaState.getValue() + ")";
            }
        }

        return result;
    }

}
