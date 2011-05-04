package com.example.sudoku;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Game extends Activity {
	private static final String TAG = "Sudoku";
	
	public static final String KEY_DIFFICULTY = "com.example.sudoku.difficulty";
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM =1;
	public static final int DIFFICULTY_HARD = 2;

	private int puzzle[] = new int[9 * 9];

	private PuzzleView puzzleView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		
		int diff = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
		puzzle = getPuzzle(diff);
		calculateUsedTiles();
		
		puzzleView = new PuzzleView(this);
		setContentView(puzzleView);
		puzzleView.requestFocus();
	}
	
	protected void showKeypadOrError(int x, int y) {
		int tiles[] = getUsedTiles(x, y);
		if (tiles.length == 9) {
			Toast toast = Toast.makeText(this, R.string.no_moves_label, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		} else {
			Log.d(TAG, "showKeypad: used=" + toPuzzleString(tiles));
			Dialog v = new Keypad(this, tiles, puzzleView);
			v.show();
		}
	}

	protected boolean setTileIfValid(int x, int y, int value) {
		int tiles[] = getUsedTiles(x, y);
		if (value != 0) {
			for (int tile : tiles) {
				if (tile == value)
					return false;
			}
		}
		setTile(x, y, value);
		calculateUsedTiles();
		return true;
	}

	private final int used[][][] = new int[9][9][];

	protected int[] getUsedTiles(int x, int y) {
		return used[x][y];
	}

	private void calculateUsedTiles() {
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				used[x][y] = calculateUsedTiles(x, y);
				// Log.d(TAG, "used[" + x + "][" + y + "] = "
				// + toPuzzleString(used[x][y]));
			}
		}
	}

	private int[] calculateUsedTiles(int x, int y) {
		int c[] = new int[9];
		// horizontal
		for (int i = 0; i < 9; i++) {
			if (i == y)
				continue;
			int t = getTile(x, i);
			if (t != 0)
				c[t - 1] = t;
		}
		// vertical
		for (int i = 0; i < 9; i++) {
			if (i == x)
				continue;
			int t = getTile(i, y);
			if (t != 0)
				c[t - 1] = t;
		}
		// same cell block
		int startx = (x / 3) * 3;
		int starty = (y / 3) * 3;
		for (int i = startx; i < startx + 3; i++) {
			for (int j = starty; j < starty + 3; j++) {
				if (i == x && j == y)
					continue;
				int t = getTile(i, j);
				if (t != 0)
					c[t - 1] = t;
			}
		}
		// compress
		int nused = 0;
		for (int t : c) {
			if (t != 0)
				nused++;
		}
		int c1[] = new int[nused];
		nused = 0;
		for (int t : c) {
			if (t != 0)
				c1[nused++] = t;
		}
		return c1;
	}

	private final String easyPuzzle = "360000000004230800000004200" + "070460003820000014500013020" + "001900000007048300000000045" ;
	private final String mediumPuzzle = "650000070000506000014000005" + "007009000002314700000700800" + "500000630000201000030000097" ;
	private final String hardPuzzle = "009000000080605020501078000" + "000000700706040102004000000" + "000720903090301080000000600" ;
	
	private int[] getPuzzle(int diff) {
		String puz;
		// TODO: Continue last game
		switch(diff) {
		case DIFFICULTY_HARD:
			puz = hardPuzzle;
			break;
		case DIFFICULTY_MEDIUM:
			puz = mediumPuzzle;
			break;
		case DIFFICULTY_EASY:
		default:
			puz = easyPuzzle;
			break;
		}
		return fromPuzzleString(puz);
	}

	static private String toPuzzleString(int[] puz) {
		StringBuilder buf = new StringBuilder();
		for (int element : puz) {
			buf.append(element);
		}
		return buf.toString();
	}

	static protected int[] fromPuzzleString(String string) {
		int[] puz = new int[string.length()];
		for (int i = 0; i < puz.length; i++) {
			puz[i] = string.charAt(i) - '0';
		}
		return puz;
	}

	private int getTile(int x, int y) {
		return puzzle[y * 9 + x];
	}

	private void setTile(int x, int y, int value) {
		puzzle[y * 9 + x] = value;
	}

	protected String getTileString(int x, int y) {
		int v = getTile(x, y);
		if (v == 0)
			return "";
		else
			return String.valueOf(v);
	}
}