import java.util.ArrayList;
public class MC { //Magic Converter
    public static void main(String[] args) {
        toMarkdown test = new toMarkdown("MagicConverter.txt");
        ArrayList<Integer> updateIndexList = test.processIndex();
        test.invert(updateIndexList);
        test.write("MagicConverter.txt");
    }
}
