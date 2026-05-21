package Chess.util;
 
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
 
public class MoveHistory {
 
    private Deque<MoveRecord> moves;
    private Deque<MoveRecord> redoStack;
 
    public MoveHistory() {
        moves = new ArrayDeque<>();
        redoStack = new ArrayDeque<>();
    }
 
    public void push(MoveRecord move) {
        moves.push(move);
        redoStack.clear(); // A new move clears the redo stack
    }
 
    public MoveRecord pop() {
        if (moves.isEmpty()) return null;
        MoveRecord record = moves.pop();
        redoStack.push(record);
        return record;
    }
 
    public MoveRecord peek() {
        return moves.isEmpty() ? null : moves.peek();
    }
 
    public boolean isEmpty() {
        return moves.isEmpty();
    }
 
    public void clear() {
        moves.clear();
        redoStack.clear();
    }
 
    public List<String> getMoveList() {
        List<String> list = new ArrayList<>();
        // Iterate from bottom (oldest) to top (newest)
        MoveRecord[] arr = moves.toArray(new MoveRecord[0]);
        for (int i = arr.length - 1; i >= 0; i--) {
            list.add(arr[i].notation);
        }
        return list;
    }
}
