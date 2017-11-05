package second;


import java.util.*;

/**
 * Created by USER on 2017/10/25.
 */
public class DFAGenerator {

    //记录传入NFA所有的转换边上的char
    private Set<Character> allChars = new HashSet<>();
    //记录已经根据哪些nfa集合生成过dfa状态,防止重复产生
    private List<DFAState> allDFAState = new ArrayList<>();
    //当前需要计算邻接节点的dfa在上面list的index，默认0，即从开始集算起
    private int dfaStateIndex = 0;

    private List<DFAState> minNFAList = null;


    public List<DFAState> getDFA(NFA nfa){

        calculateTheChars(nfa);

        Set<NFAState> startSet = getEpsilonClosureOfState(nfa.getStartState());
        DFAState startDFAState = DFAState.getNewDFAState(startSet);
        allDFAState.add(startDFAState);

        while(dfaStateIndex < allDFAState.size()){

            DFAState presentDFAState = allDFAState.get(dfaStateIndex);
            //每个字符边都对应一个到达的dfa状态，空集除外
            for(char move: allChars){
                Set<NFAState> moveClosure = getEpsilonClosureOfMove(presentDFAState.getCorrNFAStates(), move);
                //不为空，说明该dfa有对应该char边的到达dfa状态
                if(!moveClosure.isEmpty()){
                    int index = getIndexOfDFAStateOfNFASet(moveClosure);
                    //index!=-1, 已经有对应的dfa状态在列表
                    if(index!=-1){
                        presentDFAState.addEdge(move, allDFAState.get(index));
                    }
                    //没有对应的dfa状态，要新建一个
                    else {
                        DFAState newDFAState = DFAState.getNewDFAState(moveClosure);
                        allDFAState.add(newDFAState);
                        presentDFAState.addEdge(move, newDFAState);
                    }
                }
            }

            dfaStateIndex++;
        }

        //test output only
        for(DFAState dfaState: allDFAState){
            System.out.println(dfaState);
        }

        //先最小化
        minifyTheDFA();


        //再消除死状态
//        for(int i=0; i<minNFAList.size(); i++){
//
//            DFAState ds = minNFAList.get(i);
//            boolean shouldRemove = true;
//
//            for(Character c: allChars){
//                if(ds.getEdgeOfMove(c)==null){
//                    shouldRemove=false;
//                    break;
//                }
//                else if(ds.getEdgeOfMove(c).equals(ds)){
//                    continue;
//                }
//                else {
//                    shouldRemove = false;
//                    break;
//                }
//            }
//
//            if(shouldRemove){
//                minNFAList.remove(i);
//                System.out.println("delete a dead state of id:" + i);
//                break;
//            }
//        }

        //test output
        System.out.println("-----------------");
        for(DFAState ds : minNFAList){
            System.out.println(ds);
        }



        return minNFAList;
    }

    private void minifyTheDFA(){

        List<List<DFAState>> presentDivision = new ArrayList<>();

        //先根据是否是终态划分两个dfa状态集合
        List<DFAState> list1 = new ArrayList<>();
        List<DFAState> list2 = new ArrayList<>();
        for(DFAState dfaState: allDFAState){
            if(dfaState.getCorrEndNFAState()==null){
                list1.add(dfaState);
            }
            else {
                list2.add(dfaState);
            }
        }
        if(list1.size()>=1){
            presentDivision.add(list1);
        }
        if(list2.size()>=1){
            presentDivision.add(list2);
        }

        //默认初始有两个集合要划分，index为1
        int lastListToDivide = 1;
        int presentToDivide = 0;
        int preSize = 2; //之前的划分集合数量，两次数量一样时说明不能再划分
        while(true){

            List<List<DFAState>> divideResult = divideEquivalentSet(presentDivision.get(presentToDivide), presentDivision);
            presentDivision.remove(presentToDivide);
            for(List<DFAState> list: divideResult){
                presentDivision.add(list);
            }

            lastListToDivide--;
            //小于0，说明跑完一轮了，检查一下要不要再跑一轮
            if(lastListToDivide<0){
                if(preSize==presentDivision.size()){
                    break;
                }
                else {
                    preSize = presentDivision.size();
                    lastListToDivide = presentDivision.size()-1;
                }
            }

        }

        List<DFAState> minDFAStateList = new ArrayList<>();

        for(int i=0; i<presentDivision.size(); i++){
            minDFAStateList.add(DFAState.getNewDFAState());
        }

        for(int i=0; i<presentDivision.size(); i++){

            DFAState presentMinState = minDFAStateList.get(i);
            List<DFAState> presentList = presentDivision.get(i);
            presentMinState.setValue(i);

            for(int j=0; j<presentList.size(); j++){
                DFAState temp = presentList.get(j);
                //加上新状态对应的NFA状态
                presentMinState.addNFAStates(temp.getCorrNFAStates());
                //加上新状态对应的新边
                for(Map.Entry<Character, DFAState> entry: temp.getEdges().entrySet()){

                    DFAState toFind = entry.getValue();
                    int k = 0;
                    while(true){
                        if(presentDivision.get(k).contains(toFind)){
                            break;
                        }
                        else {
                            k++;
                        }
                    }

                    presentMinState.addEdge(entry.getKey(), minDFAStateList.get(k));
                }
            }

        }



        this.minNFAList = minDFAStateList;

    }

    private List<List<DFAState>> divideEquivalentSet(List<DFAState> dfaStateList, List<List<DFAState>> presentDivision){

        List<List<DFAState>> result = new ArrayList<>();

        if(dfaStateList.size()==1){
            result.add(dfaStateList);
            return result;
        }

        boolean[] hasTest = new boolean[dfaStateList.size()];

        for(int i=0; i<dfaStateList.size(); i++){

            if(!hasTest[i]){
                List<DFAState> tempList = new ArrayList<>();
                tempList.add(dfaStateList.get(i));
                hasTest[i] = true;

                for(int j=i+1; j<dfaStateList.size(); j++){
                    if(!hasTest[j]){
                        if(isEquivalent(dfaStateList.get(i), dfaStateList.get(j), presentDivision)){
                            tempList.add(dfaStateList.get(j));
                            hasTest[j] = true;
                        }
                    }
                }

                result.add(tempList);
            }

        }

        return result;
    }

    private boolean isEquivalent(DFAState dfaState1, DFAState dfaState2, List<List<DFAState>> presentDivision){

        Map<Character, DFAState> state1EdgeMap = dfaState1.getEdges();
        Map<Character, DFAState> state2EdgeMap = dfaState2.getEdges();

        if(state1EdgeMap.size() != state2EdgeMap.size()){
            return false;
        }

        for(Map.Entry<Character, DFAState> entry: state1EdgeMap.entrySet()){
            DFAState temp1 = entry.getValue();
            DFAState temp2 = state2EdgeMap.get(entry.getKey());

            if(temp2==null) return false;

            for(List<DFAState> tempDFASet: presentDivision){
                if(tempDFASet.contains(temp1)&&tempDFASet.contains(temp2)){
                    return true;
                }
                else if(!tempDFASet.contains(temp1)&&tempDFASet.contains(temp2)){
                    return false;
                }
                else if(tempDFASet.contains(temp1)&& !tempDFASet.contains(temp2)){
                    return false;
                }
            }
        }


        return false;
    }

    public void calculateTheChars(NFA nfa){

        Map<Integer, NFAState> stateMap =  nfa.getAllStates();

        for(Map.Entry<Integer, NFAState> entry: stateMap.entrySet()){
            allChars.addAll(entry.getValue().getAllCharsOfEdges());
        }

    }

    //求一个nfa状态集的move状态集的epsilon闭包
    private Set<NFAState> getEpsilonClosureOfMove(Set<NFAState> nfaStateSet, char move){

        Set<NFAState> result = new HashSet<>();

        Set<NFAState> moveSet = new HashSet<>();

        for(NFAState s: nfaStateSet){
            Set<NFAState> temp = s.getMoveEdges(move);
            moveSet.addAll(temp);
        }


        for(NFAState s: moveSet){
            Set<NFAState> closure = getEpsilonClosureOfState(s);
            result.addAll(closure);
        }

        return result;
    }

    //求一个nfa状态的epsilon闭包
    private Set<NFAState> getEpsilonClosureOfState(NFAState nfaState){
        Set<NFAState> result = new HashSet<>();
        result.add(nfaState);
        result.addAll(nfaState.getEpsilonEdges());
        int preSize = result.size();

        while(true){
            Set<NFAState> temp = new HashSet<>();
            for(NFAState s: result){
                temp.addAll(s.getEpsilonEdges());
            }
            result.addAll(temp);

            if(preSize==result.size()){
                break;
            }
            else {
                preSize = result.size();
            }
        }

        return result;
    }

    //返回参数nfa集对应的dfa状态在dfa状态列表的index，若没有，返回-1
    private int getIndexOfDFAStateOfNFASet(Set<NFAState> nfaStateSet){
        int result = -1;

        for(int i=0; i<allDFAState.size(); i++){
            if(allDFAState.get(i).getCorrNFAStates().equals(nfaStateSet)){
                result = i;
                break;
            }
        }

        return result;
    }

}
