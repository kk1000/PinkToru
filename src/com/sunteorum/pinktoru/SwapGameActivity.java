package com.sunteorum.pinktoru;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunteorum.pinktoru.entity.Piece;
import com.sunteorum.pinktoru.util.ImageUtils;
import com.sunteorum.pinktoru.view.MultiDirectionSlidingDrawer;
import com.sunteorum.pinktoru.view.PieceView;

public class SwapGameActivity extends BaseGameActivity {

	private ArrayList<Point> mPoints;
	private int step = 0;
	private int lastX;
	private int lastY;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int tx, ty;
		PieceView pib = (PieceView) v;
		/*if (tagpib != null) {
        	tagpib.setPadding(0, 0, 0, 0);
        	tagpib.setBackgroundDrawable(null);
        }*/
		
		switch(event.getAction() & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
			pib.bringToFront();
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			
			if (drawer != null && drawer.isOpened()) drawer.animateClose();
			break;
		case MotionEvent.ACTION_MOVE:
			int dx =(int)event.getX() - lastX;
			int dy =(int)event.getY() - lastY;
			
			int left = v.getLeft() + dx;
            int top = v.getTop() + dy;
            //FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)(v.getLayoutParams());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(v.getWidth(), v.getHeight());
            lp.leftMargin = left;
            lp.topMargin = top;
            lp.gravity = Gravity.TOP|Gravity.LEFT;
            v.setLayoutParams(lp);
            
            /*
            tx = (int) event.getRawX() - ((FrameLayout.LayoutParams)space.getLayoutParams()).leftMargin;
            ty = (int) event.getRawY() - ((FrameLayout.LayoutParams)space.getLayoutParams()).topMargin;
            
            tagpib = getPieceBtnInLocal(tx, ty);
            if (tagpib != null && !tagpib.equals(pib)) {
            	tagpib.setPadding(1, 1, 1, 1);
            	tagpib.setBackgroundResource(R.drawable.itemshape_2);
            }
            */
            
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					
				}
				
			});
			
			break;
		case MotionEvent.ACTION_UP:
			LinearLayout play = (LinearLayout) space.getParent();
			FrameLayout.LayoutParams pfp = (FrameLayout.LayoutParams)play.getLayoutParams();
            tx = (int) event.getRawX() - pfp.leftMargin - play.getPaddingLeft();
            ty = (int) event.getRawY() - pfp.topMargin - play.getPaddingTop();
			drop(pib, tx, ty);
			
			//Log.i("drop", "tx: " + tx + " ty: " + ty);
			
			break;        		
		}

		return true;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	@Override
	void init() {
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		puzzle = (FrameLayout) inflater.inflate(R.layout.activity_game_swap, null);
		
		setContentView(puzzle);
		
		space = (FrameLayout) findViewById(R.id.space);
		//layGameStatus = (LinearLayout) this.findViewById(R.id.layGameStatus);
		drawer = (MultiDirectionSlidingDrawer) this.findViewById(R.id.drawer);
		handle = (View) this.findViewById(R.id.handle);
		tvGameLevel = ((TextView) findViewById(R.id.tvGameLevel));
		tvGameTime = ((TextView) findViewById(R.id.tvGameTime));
		tvGameStatus = ((TextView) findViewById(R.id.tvGameStatus));
		
		puzzle.setBackgroundDrawable(background_drawalbe);
		puzzle.setKeepScreenOn(true);
		puzzle.getChildAt(0).setVisibility(4);
		puzzle.getChildAt(1).setVisibility(4);
		
		INACCURACY = 0;
	}


	@Override
	void OnCreatePuzzle(PieceView pib, int i) {
		Point p = mPoints.get(i);
		int autoX = p.x;
		int autoY = p.y;
		
		pib.setLocation(p);
		
		if (distance(pib.getMinp(), p, 12)) pib.setTraverse(true);
		else pib.setTraverse(false);
		
		FrameLayout.LayoutParams autoParams = new FrameLayout.LayoutParams
				(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		autoParams.leftMargin = (autoX);
		autoParams.topMargin = (autoY);
		autoParams.gravity = Gravity.TOP|Gravity.LEFT;
		
		space.addView(pib);
		
	}

	@SuppressWarnings("deprecation")
	@Override
	void onStartGame() {
		Drawable dg = puzzle.getBackground();
		Bitmap blurbg = Bitmap.createBitmap(space.getWidth(), space.getHeight(), Config.RGB_565);
		blurbg = ImageUtils.drawGridInBitmap(blurbg, line, row, Color.LTGRAY);
		if (blurbg != null) dg = ImageUtils.BitmapToDrawable(this, blurbg);
		if (dg != null) {
			space.setBackgroundDrawable(dg);
			space.getBackground().setAlpha(120);
		}
		
		puzzle.setBackgroundColor(Color.LTGRAY);
		puzzle.getChildAt(1).setVisibility(0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	void onNewGame(Vector<Piece> pieces) {
		mPoints = new ArrayList<Point>();
		Point p = null;
		
		for (int i=0; i<allPieces.size(); i++) {
			p = allPieces.get(i).getMinp();
			//p.x = p.x+1;
			//p.y = p.y+1;
			mPoints.add(p);
		}
		
		Collections.sort(mPoints, new Comparator() {
		      @Override
		      public int compare(Object o1, Object o2) {
		    	  int rnd = ((int)(Math.random()*10)%2 ==0) ? 1 : -1;
		    	  return rnd * ((int)(Math.random()*mPoints.size()));
		      }
		});
		
	}


    public void drop(PieceView pib, int x, int y) {
		Point p = new Point(x, y);
		
		int left, top, left2, top2;
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		left = pib.getLocation().x;
		top = pib.getLocation().y;
		
		PieceView pib2 = getPieceBtnInLocal(x, y);
		if (pib2 == null || pib2.equals(pib)) {
			params.leftMargin = pib.getLocation().x;
			params.topMargin = pib.getLocation().y;
			params.gravity = Gravity.TOP|Gravity.LEFT;
			pib.setLayoutParams(params);
			return;
		}
		
		FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		left2 = pib2.getLocation().x;
		top2 = pib2.getLocation().y;
		
		params2.leftMargin = left;
		params2.topMargin = top;
		
		params.leftMargin = left2;
		params.topMargin = top2;
		
		params.gravity = Gravity.TOP|Gravity.LEFT;
		pib.setLayoutParams(params);
		
		params2.gravity = Gravity.TOP|Gravity.LEFT;
		pib2.setLayoutParams(params2);
		
		p = new Point(left, top);
		pib2.setLocation(p);
		if (distance(pib2.getMinp(), p, 12)) pib2.setTraverse(true);
		else pib2.setTraverse(false);
		
		p = new Point(left2, top2);
		pib.setLocation(p);
		if (distance(pib.getMinp(), p, 12)) pib.setTraverse(true);
		else pib.setTraverse(false);
		
		step++;
		game_status = String.valueOf(step);
    	setGameStatus();
		hasComplete();
		
	}

    private PieceView getPieceBtnInLocal(int x, int y) {
    	PieceView pib = null;
    	int piececount = allPieces.size();
		
		
		for(int i=0; i<piececount; i++){
			pib = allPieces.get(i);
			Point op = pib.getLocation();
			if (x > (op.x + INACCURACY) && x < (op.x + pib.getWidth() - INACCURACY)) {
				if (y > (op.y + INACCURACY) && y < (op.y + pib.getHeight() - INACCURACY)) {
					//Log.i("getPieceBtnInLocal", i + ":(" + x + " - " + y + ") " + pib.hashCode());
					return pib;
				}
			}
			
			
		}
    	
    	return null;
    }
    
}