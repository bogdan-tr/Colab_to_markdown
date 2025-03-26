import java.util.ArrayList;
public class toMd { 
    public static void main(String[] args) {
        if(args.length == 0){
            toMarkdown test = new toMarkdown("toMarkdown.txt");
            ArrayList<Integer> updateIndexList = test.processIndex();
            test.invert(updateIndexList);
            test.write("toMarkdown.txt");
        }else if(args.length == 1){
            String language = args[0];
            toMarkdown test = new toMarkdown("toMarkdown.txt", language);
            ArrayList<Integer> updateIndexList = test.processIndex();
            test.invert(updateIndexList);
            test.write("toMarkdown.txt");
        }else if(args.length == 2){
            String language = args[0];
            toMarkdown test = new toMarkdown("toMarkdown.txt", language);
            ArrayList<Integer> updateIndexList = test.processIndex();
            test.invert(updateIndexList);
            test.write("NewtoMarkdown.txt");
        }
    }
}
