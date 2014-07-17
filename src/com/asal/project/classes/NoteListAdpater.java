package com.asal.project.classes;

import java.util.List;



import com.asal.project.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//The Adapter provides access to the note items.
//The Adapter is also responsible for making a View for each item in the notes set.

//The Inflater instantiates the layout XML file ("list_item" in our case) into its
//corresponding View objects. (Linking between the design and data objects).

public class NoteListAdpater extends ArrayAdapter<NoteItem>{

	private Activity activity ; 
	private LayoutInflater infalter ; 
	
	public NoteListAdpater(Context context, int textViewResourceId,List<NoteItem> objects) {
	   super(context, textViewResourceId, objects);
	   this.activity = (Activity) context; 
	   infalter = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	   if(convertView ==  null)
	   {
		convertView =  infalter.inflate(R.layout.list_item,	null) ; 
	   }		
	   
	   NoteItem item = getItem(position);
	   
	   TextView title = (TextView) convertView.findViewById(R.id.note_title) ; 
	   title.setText(item.getNoteTitle()) ; 
	   TextView desc = (TextView) convertView.findViewById(R.id.note_desc) ; 
	   desc.setText(item.getNoteDesc()) ; 
   
	   return convertView;
	}

}
