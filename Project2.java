import java.util.Random;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Project2
{
    static final int SIZE = 18;
    static final int POPULATION_SIZE = 100;
    static int[] board = new int[SIZE];
    
    public static void main(String[] args)
    {
        long start, end;
        int steps;
        Scanner kb = new Scanner(System.in);        
        while(true) {
            System.out.print("**18-Queen Puzzle**\n" +
                             "(1) Display Hill-Climbing result\n" +
                             "(2) Display Genetic Algorithm result\n" +
                             "(3) Compare search algorithms\n" +
                             "(4) Quit\n==> Select option (1-4): ");
            String choice = kb.nextLine();
            switch(choice) {
                case "1":
                    start = System.currentTimeMillis();
                    steps = hillClimbingSearch();
                    end = System.currentTimeMillis();
                    printBoard(board);
                    System.out.print("Number of attacking pairs: " + 
                                       findNumberOfAttackingPairs(board));
                    System.out.println(" (a solution was " + 
                                       (findNumberOfAttackingPairs(board) == 
                                        0 ? "" : "not ") + "found)");
                    System.out.println("Number of steps taken: " + steps);
                    System.out.println("Time taken (ms): " + (end-start));
                    System.out.println("\nPress <Enter> to continue.");
                    kb.nextLine();
                    break;
                case "2":
                    start = System.currentTimeMillis();
                    steps = geneticAlgorithm(POPULATION_SIZE);
                    end = System.currentTimeMillis();
                    printBoard(board);
                    System.out.println("Number of attacking pairs: " + 
                                       findNumberOfAttackingPairs(board));
                    System.out.println("Number of steps taken: " + steps);
                    System.out.println("Time taken (ms): " + (end-start));
                    System.out.println("\nPress <Enter> to continue.");
                    kb.nextLine();
                    break;
                case "3":
                    System.out.print("Enter the number of iterations to " +
                                     "perform: ");
                    int iterations = kb.nextInt();

                    int totalHillSteps = 0, totalGeneticSteps = 0, 
                        totalHillWins = 0, totalGeneticWins = 0;
                    long totalHillTime = 0, totalGeneticTime = 0;
                    for(int i = 0; i < iterations; i++) {
                        start = System.currentTimeMillis();
                        totalHillSteps += hillClimbingSearch();
                        end = System.currentTimeMillis();
                        totalHillWins += (findNumberOfAttackingPairs(board) == 
                                          0 ? 1 : 0);
                        totalHillTime += end - start;

                        start = System.currentTimeMillis();
                        totalGeneticSteps += geneticAlgorithm(POPULATION_SIZE);
                        end = System.currentTimeMillis();
                        totalGeneticWins +=
                        (findNumberOfAttackingPairs(board) == 0 ? 1 : 0); 
                        totalGeneticTime += end - start;
                    }
                    System.out.printf("\t\t\t%s\t%s\t%s\n", 
                                      "Average steps:", 
                                      "Percentage of solved problems:", 
                                      "Average time taken (ms):");
                    System.out.printf("Hill-Climbing:\t\t%f\t%f\t\t\t%f\n", 
                                      (totalHillSteps*1.0)/(iterations*1.0), 
                                      (totalHillWins*100.0)/(iterations*1.0),
                                      (totalHillTime*1.0)/(iterations*1.0));
                    System.out.printf("Genetic Algorithm:\t%f\t%f\t\t\t%f\n", 
                                      (totalGeneticSteps*1.0)/(iterations*1.0), 
                                      (totalGeneticWins*100.0)/(iterations*1.0),
                                      (totalGeneticTime*1.0)/(iterations*1.0));
                    System.out.println("\nPress <Enter> to continue.");
                    kb.nextLine();
                    kb.nextLine();
                    break;
                case "4":
                    System.exit(0);
                    break;              
            }
        }
    }
    
    /**
     * This method returns a randomly generated board.
     * @return A randomly generated board
     */
    public static int[] generateRandomBoard() {
        Random rdm = new Random();
        int[] result = new int[SIZE];
        for(int i = 0; i < SIZE; i++)
            result[i] = rdm.nextInt(SIZE);
        return result;
    }
    
    /**
     * This method performs the steepest-ascent hill climbing search to find a
     * solution to the n-Queen problem. It starts by generating a random board,
     * and finding the heuristic board (generateHeuristicBoard). Once 
     * determining the lowest number on that board (at row m, column n), the 
     * queen of column n is moved to row m, and the process is repeated until
     * no spot on the heuristic board is lower than the current number of 
     * attacking queens. At this point, the number of repetitions is returned.
     * @return The number of repetitions (or steps)
     */
    public static int hillClimbingSearch() {
        int numberOfSteps = 0;
        board = generateRandomBoard();
        while (true) {
            int[][] heuristicBoard = generateHeuristicBoard(board);
            int[] minimumHeuristic = findMinimum(heuristicBoard);
            if(findNumberOfAttackingPairs(board) > 
               heuristicBoard[minimumHeuristic[0]][minimumHeuristic[1]]) {
                board[minimumHeuristic[1]] = minimumHeuristic[0];
                numberOfSteps++;
            }
            else
                return numberOfSteps;
        }
    }
    
    /**
     * This method, given a board, generates a SIZE*SIZE grid of integers. The
     * integer at row m and column n represents the number of attacking pairs
     * if the queen on the board at column n moved to row m. This is used in 
     * finding the next step in the steepest-ascent hill climbing algorithm.
     * @param board A board
     * @return A SIZE*SIZE double array of integers
     */
    public static int[][] generateHeuristicBoard(int[] board) {
        int[][] result = new int[SIZE][SIZE];
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                int[] tempBoard = copy(board);
                tempBoard[j] = i;
                result[i][j] = findNumberOfAttackingPairs(tempBoard);
            }
        }
        return result;
    }
    
    /**
     * This method, given a board, determines the number of pairs of queens on
     * that board that can attack each other, and returns that number.
     * @param board A board
     * @return The number of attacking pairs
     */
    public static int findNumberOfAttackingPairs(int[] board) {
        int result = 0;
        for(int i = 0; i < SIZE; i++) {
            for(int j = i+1; j < SIZE; j++)
                result += (isAttackingPair(board[i], board[j], i, j) ? 1 : 0);
        }
        return result;
    }
    
    /**
     * This method returns true if two given queens are attacking each other.
     * The method takes the row and column of both queens and determines if
     * either one can attack each other horizontally, vertically (although not
     * needed since each queen will always have a separate column), and
     * diagonally. If they can attack each other, the method returns true, and
     * false otherwise.
     * @param row1 The row of queen 1
     * @param row2 The row of queen 2
     * @param column1 The column of queen 1
     * @param column2 The column of queen 2
     * @return True if queen 1 and 2 can attack each other; false otherwise
     */
    public static boolean isAttackingPair(int row1, int row2, int column1, 
                                                              int column2) {
        if((Math.abs(row1 - row2) == Math.abs(column1 - column2)) || 
           (row1 == row2) || (column1 == column2))
            return true;
        return false;
    }
    
    /**
     * This method makes a hard copy of an integer array.
     * @param a The array to be copied
     * @return The copied array
     */
    public static int[] copy(int[] a) {
        int[] result = new int[a.length];
        for(int i = 0; i < a.length; i++)
            result[i] = a[i];
        return result;
    }
    
    /**
     * This method finds the minimum number in a grid of integers, and returns
     * the position of that number. This is used in finding the next step in the
     * steepest-ascent hill climbing algorithm.
     * @param heuristicBoard A grid of integers
     * @return The minimum number's location on the board
     */
    public static int[] findMinimum(int[][] heuristicBoard) {
        int min = heuristicBoard[0][0];
        int minRow = 0, minColumn = 0;
        for(int i = 0; i < heuristicBoard.length; i++) {
            for(int j = 0; j < heuristicBoard.length; j++) {
                if(heuristicBoard[i][j] < min) {
                    min = heuristicBoard[i][j];
                    minRow = i;
                    minColumn = j;
                }
            }
        }       
        int[] result = {minRow, minColumn};
        return result;
    }
    
    /**
     * This method prints a board, given a board that is represented by an
     * array of integers. The queens are represented by X's, and the other
     * spots of the board are represented by 0's.
     * @param board A board.
     */
    public static void printBoard(int[] board) {
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                System.out.print(board[j] == i ? "X " : "0 ");
            }
            System.out.println();
        }
    }
    
    
    /**
     * This is the second method of finding a solution to the n-queens problem.
     * This is a genetic algorithm that starts with a certain population size.
     * The population is reduced to 10% its original size, keeping the boards
     * with the least number of attacking pairs. Then 18 children are generated
     * by each couple, which inherits properties from both parents, along with 
     * a mutation. The children and parents are placed back in the 
     * population and the process is repeated until a solution is found.
     * @param populationSize The size of the population
     * @return The number of generations that were generated to find a solution
     */
    public static int geneticAlgorithm(int populationSize) {
        if((populationSize % 10 != 0) || ((populationSize / 10) % 2 != 0))
            return -1;
        
        int childrenPerCouple = 18;
        int numberOfSteps = 0;
        PriorityQueue<State> population = new PriorityQueue<>();
        PriorityQueue<State> newPopulation = new PriorityQueue<>();
        
        while(true) {
            if(numberOfSteps == 0) {
                //Initializes population
                for(int i = 0; i < populationSize; i++) {
                    board = generateRandomBoard();
                    if(findNumberOfAttackingPairs(board) == 0)
                        return numberOfSteps;
                    population.add(new State(board, 
                                             findNumberOfAttackingPairs(board)));
                }
            }
            numberOfSteps++;
            //Finds (populationSize / 10) best solutions in the population
            for(int i = 0; i < populationSize - (populationSize/10); i++)
                population.remove();        
            int numberOfParents = population.size();
            //Each pair of parents create 18 offspring. All the offspring, and
            //the parents, are added in the new population.
            for(int i = 0; i < numberOfParents; i += 2) {
                int[] a = population.remove().getBoard();
                newPopulation.add(new State(a, findNumberOfAttackingPairs(a)));
                int[] b = population.remove().getBoard();            
                newPopulation.add(new State(b, findNumberOfAttackingPairs(b)));
                for(int j = 0; j < childrenPerCouple; j++) {
                    int[] child = generateOffspring(a, b);
                    newPopulation.add(new State(child, 
                                                findNumberOfAttackingPairs(child)));
                }
            }
            //Repopulates the population. If any of the states have solved the
            //n-queens problem, the method ends.
            for(int i = 0; i < populationSize; i++) {
                if(newPopulation.peek().getFitness() == 0) {
                    board = newPopulation.peek().getBoard();
                    return numberOfSteps;
                }
                else
                    population.add(newPopulation.remove());               
            }
            newPopulation.clear();
            if(numberOfSteps >= 20000) {
                population.clear();
                numberOfSteps = 0;
            }
        }       
    }
    
    /**
     * This method generates a child board, given two parent boards a and b.
     * A crossover point is randomly generated (between 0 and SIZE-1), and
     * the child inherits 0-crossover of a's board, and (crossover+1)-SIZE of 
     * b's board. The child is then mutated (one row number in its board is 
     * altered), and the child is returned.
     * @param a First parent board
     * @param b Second parent board
     * @return Child board
     */
    public static int[] generateOffspring(int[] a, int[] b) {
        Random rdm1 = new Random();
        Random rdm2 = new Random();
        int[] child = new int[SIZE];
        int crossover = rdm1.nextInt(SIZE);
        
        for(int i = 0; i < crossover; i++)
            child[i] = a[i];
        for(int i = crossover; i < SIZE; i++)
            child[i] = b[i];
        
        //if(rdm1.nextBoolean())
        child[rdm1.nextInt(SIZE)] = rdm2.nextInt(SIZE);
        
        return child;
    }
}
