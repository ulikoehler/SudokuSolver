package projects.sudokusolver;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 * <b>Definitions:</b>
 * Alphabet: A collection of characters representing the 
 * @author uli
 */
public final class Sudoku9 {

    private static final char undefined = 'x';
    private String alphabet = "123456789";
    private SudokuField[][] fields = new SudokuField[getNumRows()][]; //Rows first

    public class SudokuField {

        public int getContent() {
            return content;
        }
        private int content = -1; //As index of the alphabet
        private boolean[] possibilities = new boolean[alphabet.length()];

        public void setPossibility(int index, boolean b) {
            possibilities[index] = b;
        }

        public boolean getPossibility(int index) {
            return possibilities[index];
        }

        public void setContent(int content) {
            this.content = content;
            Arrays.fill(possibilities, false);
        }

        /**
         * @return The number of current possibilities begin true
         */
        public int getNumPossibilities() {
            int ctr = 0;
            for (boolean possibility : possibilities) {
                if (possibility) {
                    ctr++;
                }
            }
            return ctr;
        }

        /**
         * @return The first alphabet index for which the possibility is true
         */
        public int getSinglePossibility() {
            for (int i = 0; i < possibilities.length; i++) {
                if (possibilities[i]) {
                    return i;
                }
            }
            throw new IllegalStateException("No possibility found!");
        }

        /**
         * Initializes a Sudoku field with a specific content
         * @param content 
         */
        public SudokuField(int content) {
            this.content = content;
            Arrays.fill(possibilities, false);
        }

        /**
         * Constructs a new SudokuField instance 
         */
        public SudokuField() {
            for (int i = 0; i < possibilities.length; i++) {
                possibilities[i] = true;
            }
        }
        
        /**
         * Constructs a new SudokuField instance 
         */
        public SudokuField(SudokuField other) {
            this.content = other.content;
            this.possibilities = Arrays.copyOf(other.possibilities, other.possibilities.length);
        }
        
        /**
         * 
         * @return All list of possibilities being true
         */
        public List<Integer> getPossibilities() {
            List<Integer> ret = Lists.newArrayListWithCapacity(possibilities.length);
            for (int i = 0; i < possibilities.length; i++) {
                boolean possibility = possibilities[i];
                if(possibility) {
                    ret.add(i);
                }
            }
            return ret;
        }

        @Override
        public String toString() {
            return "SudokuField{" + "content=" + content
                    + ", canContainElement=" + possibilities + '}';
        }
    }
    
    /**
     * Deep-copy (in relation to SudokuFields) constructor
     */
    public Sudoku9(Sudoku9 other) {
        //Initialize the rows
        for (int i = 0; i < getNumRows(); i++) {
            fields[i] = new SudokuField[getNumCols()];
        }
        //Copy all fields
        for (int i = 0; i < getNumRows(); i++) {
            for (int j = 0; j < getNumCols(); j++) {
                fields[i][j] = new SudokuField(other.getField(i, j));
            }
        }
    }

    public Sudoku9(InputStream input) {
        try {
            List<String> lines = IOUtils.readLines(input);
            if (lines.size() != 9) {
                throw new IllegalArgumentException("The given file does not contain 9 lines");
            }
            for (int i = 0; i < lines.size(); i++) {
                String currentLine = lines.get(i).trim();
                //Check constraint
                if (currentLine.length() != 9) {
                    throw new IllegalArgumentException("The line " + currentLine
                            + " does not contain exactly 9 characters");
                }
                //Initialize the current field array
                fields[i] = new SudokuField[9];
                //Iterate over the fields (horizontally)
                for (int j = 0; j < 9; j++) {
                    char c = currentLine.charAt(j);
                    if (c == undefined) {
                        fields[i][j] = new SudokuField();
                    } else {
                        int index = alphabet.indexOf(c);
                        if (index == -1) {
                            throw new IllegalArgumentException("Character not in alphabet or undefined: "
                                    + c);
                        }
                        fields[i][j] = new SudokuField(index);
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    public String getAlphabet() {
        return alphabet;
    }

    public SudokuField[][] getFields() {
        return fields;
    }

    /**
     * Getter for a specific field
     * @param row The zero-based row
     * @param col The zero-based column
     * @return The specific field
     */
    public SudokuField getField(int row, int col) {
        return fields[row][col];
    }

    /**
     * Getter for a specific row
     * @param index The zero-bazed index
     * @return The row as array
     */
    public SudokuField[] getRow(int index) {
        return fields[index];
    }

    /**
     * Getter for a specific column
     * @param index The zero-based index
     * @return The column as array
     */
    public SudokuField[] getColumn(int index) {
        SudokuField[] ret = new SudokuField[9];
        for (int i = 0; i < 9; i++) {
            ret[i] = fields[i][index];
        }
        return ret;
    }

    /**
     * Getter for a specific subfield
     * @param index The zero-based index
     * @return The subfield as array
     */
    public SudokuField[] getSubfield(int index) {
        SudokuField[] ret = new SudokuField[9];
        //Calculate the left uppermost field in the 
        int dividend = index / 3;
        int remainder = index % 3;
        int startRow = 3 * dividend;
        int startCol = 3 * remainder;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                SudokuField field = fields[startRow + i][startCol + j];
                ret[(3 * i) + j] = field;
            }
        }
        return ret;
    }

    public int getNumRows() {
        return 9;
    }

    public int getNumCols() {
        return 9;
    }

    public int getNumFields() {
        return 9;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < getNumRows(); i++) {
            SudokuField[] row = getRow(i);
            for (SudokuField field : row) {
                if (field.getContent() == -1) {
                    builder.append(undefined);
                } else {
                    builder.append(alphabet.charAt(field.getContent()));
                }
            }
            builder.append('\n');
        }
        return builder.toString().trim();
    }
    
    public List<SudokuField> getAllFields() {
        List<SudokuField> ret = Lists.newArrayListWithCapacity(getNumRows()*getNumCols());
        for (int i = 0; i < getNumRows(); i++) {
            SudokuField[] row = getRow(i);
            for (int j = 0; j < row.length; j++) {
                ret.add(row[i]);
            }
        }
        return ret;
    }

    public boolean isSolved() {
        for (int i = 0; i < getNumRows(); i++) {
            SudokuField[] row = getRow(i);
            for (int j = 0; j < row.length; j++) {
                if (row[i].getContent() == -1) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public int getAlphabetSize() {
        return alphabet.length();
    }
}
