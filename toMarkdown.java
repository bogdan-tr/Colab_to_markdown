//Author: Bogdan Trigubov
//Date: March 2025
//Description: Class that converts a python file (pasted into toMarkdown.txt) into properly formatted markdown (result replaces what was pasted in toMarkdown.txt). Instance of this class is created in toMd.java and is necessary to run the converter. 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class toMarkdown {
    private ArrayList<String> linesList;
    private ArrayList<String> outputList;
    private String language;

    //Default constructor
    public toMarkdown(String fileName){
        this.linesList = readFile(fileName);
        this.language = "python";
    }
    
    //Constructor for custom language
    public toMarkdown(String fileName, String language){
        this.linesList = readFile(fileName);
        this.language = language;
    }

    public void invert(ArrayList<Integer> updateIndexList){
        int indexOf_updateIndexList = 0;
        this.outputList = new ArrayList<>();
        for(int i=0;i<linesList.size();i++){
            String string;
            if(i==0){
                string = (linesList.get(i)).replace("\"\"\"", "# " );
                String watermark = "\nAutomatically converted to markdown by **toMarkdown** _(by Bogdan Trigubov)_";
                string = string + watermark;
            }else if(indexOf_updateIndexList < updateIndexList.size() && i == updateIndexList.get(indexOf_updateIndexList)){
                if(indexOf_updateIndexList%2 == 0){ //even
                    string = convertPython(linesList.get(i), language);
                }else{ //odd
                    string = convert(linesList.get(i));
                }
                indexOf_updateIndexList++;
            }else{
                string = linesList.get(i);
            }
            // System.out.println(string);
            this.outputList.add(string);
        }
        // System.out.println("```");
        this.outputList.add("```");
    }

    public ArrayList<Integer> processIndex(){ //returns indices of strings that must be updated
        String doc  = "\"\"\"";
        int docCount = 0;
        ArrayList<Integer> updateIndexList = new ArrayList<>();
        for(int i=0; i<linesList.size(); i++){
            String line = linesList.get(i);
            int index = 0;
            boolean lineAdded = false;
            for(char charachter: line.toCharArray()){
                if(charachter == '\"'){ 
                    try{
                    String test = String.valueOf(line.toCharArray()[index]) +
                    String.valueOf(line.toCharArray()[index + 1]) +
                    String.valueOf(line.toCharArray()[index + 2]);
                        if(test.equals(doc) && !lineAdded){
                            if(docCount > 0){ //convert """ to ```
                                updateIndexList.add(i);
                                lineAdded = true;
                                }
                            docCount++;
                            lineAdded = true;
                            }
               }catch(ArrayIndexOutOfBoundsException e){
                   break;
               }
           }
           index++;
       }
        }
        return updateIndexList;
    }
    public static String convert(String line){ //convert string with """ component to \n```\n
        String newText = line.replace("\"\"\"", "\n```\n");
        return newText;
    }
    public static String convertPython(String line, String language){ //convert string with """ component to \n``` python
        String newText = line.replace("\"\"\"", "\n```"+language);
        return newText;
    }
    public void print(){
        for(int i=0; i<linesList.size(); i++){
            System.out.println(linesList.get(i));
        }
    }
    public static ArrayList<String> readFile(String fileName){ 
        ArrayList<String> linesList = new ArrayList<>();
        String line;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            while((line = reader.readLine())!= null){
                if(line.contains("\"\"\"")){ //if line contains """
                    int firstIndex = line.indexOf("\"\"\"");
                    int secondIndex = line.indexOf("\"\"\"", firstIndex + 3); 
                    if(firstIndex!=-1 && secondIndex != -1){ // if line contains two """, it must be split into two lines
                        String part1 = line.substring(0, secondIndex); // Part before the second """
                        String part2 = line.substring(secondIndex);    // Part starting from the second """
                        linesList.add(part1);
                        linesList.add(part2);
                    }else{
                        linesList.add(line);
                    }
                }else{
                    linesList.add(line);
                }
            }
            reader.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            return linesList;
        }
        //WRITER
    public void write(String fileName){
         try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            if((linesList.get(linesList.size()-1)).contains("\"\"\"")){ //if last string is a """
            for(int i=0; i<outputList.size()-2; i++){
                writer.write(outputList.get(i));
                writer.newLine();
            }
            writer.close();
            }else{
            for(int i=0; i<outputList.size(); i++){
                writer.write(outputList.get(i));
                writer.newLine();
            }
            writer.close();
        }
        }catch (IOException e){
        e.printStackTrace();
        }
    }
}



