import java.util.ArrayList;
public class toMd { 
    public static void main(String[] args) {
        toMarkdown test = new toMarkdown("toMarkdown.txt");
        ArrayList<Integer> updateIndexList = test.processIndex();
        test.invert(updateIndexList);
        test.write("toMarkdown.txt");
    }
}
