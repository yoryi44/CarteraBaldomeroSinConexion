package Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import co.com.celuweb.carterabaldomero.R;
import dataobject.ItemListView;


public class ListViewAdapter extends ArrayAdapter<ItemListView> {
	
	int icono;
	int colorTitulo;
	int layout_list_item;

	public int seleccionado = -1;
	
	Activity context;
	ItemListView[] listItems;
	
	public ListViewAdapter(Activity context, ItemListView[] listItems, int icono, int colorTitulo) {
		
		super(context, R.layout.list_item_impresion, listItems);
		
		this.layout_list_item = R.layout.list_item_impresion;
		this.listItems = listItems; 
		this.context = context;
		
		this.icono = icono;
		this.colorTitulo = colorTitulo; 
	}
	
	public ListViewAdapter(Activity context, ItemListView[] listItems, int icono, int colorTitulo, int layout_list_item) {
		
		super(context, layout_list_item, listItems);
		
		this.layout_list_item = layout_list_item;
		this.listItems = listItems; 
		this.context = context;
		
		this.icono = icono;
		this.colorTitulo = colorTitulo; 
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = context.getLayoutInflater();
		View item = inflater.inflate(layout_list_item, null);
		
		TextView lblTitulo = (TextView)item.findViewById(R.id.lblTitulo);
		lblTitulo.setText(listItems[position].titulo);
		
		TextView lblSubtitulo = (TextView)item.findViewById(R.id.lblSubTitulo);
		lblSubtitulo.setText(listItems[position].subTitulo);
		
		if (listItems[position].icono > 0)
			((ImageView)item.findViewById(R.id.iconListView)).setImageResource(listItems[position].icono);
		else if (icono > 0)
			((ImageView)item.findViewById(R.id.iconListView)).setImageResource(icono);
		
		if (item != null) {
			if (position == seleccionado) {
//				item.setBackgroundColor(Color.RED);
				lblTitulo.setTextColor(Color.RED);
			} else {
				lblTitulo.setTextColor(Color.parseColor("#2E65AD"));
//				item.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		
		return item;
    }
	
	@Override
	public ItemListView getItem(int position) {
		
		return listItems[position];
	}
}
