package second;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by USER on 2017/10/25.
 */
public class NFA {

    private NFAState startState = null;
    private NFAState endState = null;

    private Map<Integer, NFAState> allStates = new HashMap<>();

    public NFA(NFAState startState, NFAState endState){

        this.startState = startState;
        this.endState = endState;

        allStates.put(startState.getValue(), startState);
        allStates.put(endState.getValue(), endState);

    }


    public void addNFAState(NFAState nfaState){

        allStates.put(nfaState.getValue(), nfaState);

    }

    public void changeStart(NFAState nfaState){
        this.startState = nfaState;
        this.getAllStates().put(nfaState.getValue(), nfaState);
    }

    public void changeEnd(NFAState nfaState){
        this.endState = nfaState;
        this.getAllStates().put(nfaState.getValue(), nfaState);
    }

    public Map<Integer, NFAState> getAllStates(){
        return allStates;
    }

    public NFAState getStartState(){
        return this.startState;
    }

    public NFAState getEndState(){
        return this.endState;
    }
}
