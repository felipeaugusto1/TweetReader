package tweetreader.list;

import java.util.List;
import tweetreader.models.Tweet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import felipe.tweetreader.R;

/**
 * Classe responsável pelo tratamento da listview.
 * 
 * @author Felipe Augusto
 * 
 */
public class AdapterListView extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Tweet> itens;

	public AdapterListView(Context context, List<Tweet> itens) {
		this.itens = itens;
		this.inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return this.itens.size();
	}

	public Tweet getItem(int position) {
		return this.itens.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		ItemSuporte itemHolder;
		if (view == null) {
			view = this.inflater.inflate(R.layout.item_list, null);

			itemHolder = new ItemSuporte();
			itemHolder.txtNomeUsuario = ((TextView) view
					.findViewById(R.id.txtNomeUsuario));
			itemHolder.txtData = ((TextView) view.findViewById(R.id.txtData));
			itemHolder.txtMsg = ((TextView) view.findViewById(R.id.txtTexto));
		} else {
			itemHolder = (ItemSuporte) view.getTag();
		}
		Tweet item = this.itens.get(position);

		try {
			itemHolder.txtNomeUsuario.setText(item.getUsuario());
			itemHolder.txtData.setText(item.getDataEnvio());
			itemHolder.txtMsg.setText(item.getMensagem());
		} catch (Exception exception) {
		}

		return view;
	}

	private class ItemSuporte {
		TextView txtNomeUsuario;
		TextView txtData;
		TextView txtMsg;
	}
}