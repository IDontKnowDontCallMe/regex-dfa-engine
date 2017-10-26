package first;

/**
 * Created by USER on 2017/10/22.
 */
public class Token {

    private String name;
    private String value;

    public Token(String name, String value){

        this.name = name;
        this.value = value;

    }

    @Override
    public String toString(){

        return "< " + name + " , " + value + " >";
    }

}
