package com.example.officerbumble.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;

import com.example.officerbumble.engine.LeaderboardData.LeaderboardEntry;
import com.example.officerbumble.R;

public class LeaderboardAdapter extends BaseAdapter {
		
	private LayoutInflater m_inflater;
	private List<LeaderboardEntry> m_items = new ArrayList();
	 
	 public LeaderboardAdapter(Context _context, List<LeaderboardEntry> _items) {
	    super();
	    m_inflater = LayoutInflater.from(_context);
        m_items = _items;
	 }
	 	 	  	
	@Override
	public int getCount() {
		return m_items.size();		
	}

	@Override
	public Object getItem(int position) {
		return m_items.get(position);		
	}

	@Override
	public long getItemId(int position) {
		return 0;		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
	    LeaderboardEntry lb = m_items.get(position);
	        
		if (convertView == null)
        {
            convertView = m_inflater.inflate(R.layout.leaderboardrow, null);                       
            if(lb.IsYou()) {
            	convertView.setBackgroundColor(Color.parseColor("#00CED1"));	// Yellow
            }
            
            holder = new ViewHolder();

            holder.txtRank = (TextView) convertView.findViewById(R.id.RANK_CELL);
            holder.txtName = (TextView) convertView.findViewById(R.id.NAME_CELL);
            holder.txtHighscore = (TextView) convertView.findViewById(R.id.HIGHSCORE_CELL);
            holder.txtCriminalsCaught = (TextView) convertView.findViewById(R.id.CRIMINALSCAUGHT_CELL);
            holder.imgPic = (ImageView) convertView.findViewById(R.id.PIC_CELL);
            convertView.setTag(holder);                        
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
		
		holder.txtRank.setText(lb.GetRank());
		holder.txtName.setText(lb.GetName());
		holder.txtHighscore.setText(String.valueOf(lb.GetHighScore()));
		holder.txtCriminalsCaught.setText(String.valueOf(lb.GetCriminalsCaught()));
        
		if (lb.getImage() != null) {
            holder.imgPic.setImageBitmap(lb.getImage());
        } else {
            // MY DEFAULT IMAGE
            //holder.imgPic.setImageResource(R.drawable.generic_profile_man);
        }		

        return convertView;
	}		
	
    private class ViewHolder {
        TextView txtRank;
        ImageView imgPic;
        TextView txtName;
        TextView txtHighscore;
        TextView txtCriminalsCaught;        
    }
    
	
}
