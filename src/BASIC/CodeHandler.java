package BASIC;

public class CodeHandler {
    private String document; //holds the document
    private int index = 0; //holds the index of the character we're looking at

    public CodeHandler(String document){
        this.document = document;
    }

    /**
     * peeks ahead in the document and returns a character
     * @param i the amount of characters to peek ahead
     * @return the ith character ahead of the current index
     */
    public char peek(int i){
        int peekI = index + i;
        if(peekI > 0 && peekI <= document.length()){
            return document.charAt(peekI);
        }
        return 0;
    }

    /**
     * returns a string of the next "i" characters - specified in the parameter
     * @param i the amount of characters to be returned in the string
     * @return the string of the next i characters
     */
    public String peekString(int i){
        int peekI = index + i;
        if(peekI > 0 && peekI <= document.length()){
            return document.substring(index, peekI);
        }
        return null;
    }

    /**
     * returns the next character and moves the index
     * @return the next character in the string
     */
    public char getChar(){
        if(index < document.length()) {
            char nextChar = document.charAt(index);
            index++;
            return nextChar;
        }
        return 0;
    }

    /**
     * moves the index ahead i positions
     * @param i amount of positions for the index to be moved ahead
     */
    public void swallow(int i){
        if(i >= 0 && index + i <= document.length()){
            index += i;
        }
    }

    /**
     * returns true if we are at the end of the document
     * @return true or false depending on where you are in the document
     */
    public boolean isDone(){return index >= document.length();}

    /**
     *returns the rest of the document as a string
     * @return the remainder of the document
     */
    public String remainder(){return document.substring(index);}
}
