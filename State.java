/**
 * This class stores a board and its number of attacking queens (fitness). The
 * class is used for the genetic algorithm search. When put into a priority
 * queue, the states are sorted reversely by fitness. This allows States in the
 * priority queue to be pulled and the ones left in are the best States in terms
 * of fitness (lower fitness is better). 
 */

public class State implements Comparable<State>
{
    private int[] board;
    private int fitness;
    
    public State(int[] b, int f) {
        board = copy(b);
        fitness = f;
    }
    
    public int[] getBoard() {
        return copy(board);
    }
    
    public int getFitness() {
        return fitness;
    }
    
    private int[] copy(int[] a) {
        int[] result = new int[a.length];
        for(int i = 0; i < a.length; i++)
            result[i] = a[i];
        return result;
    }
    
    @Override
    public int compareTo(State s) {
        if(this.fitness < s.fitness)
            return 1;
        if(this.fitness > s.fitness)
            return -1;
        return 0;
    }
}
