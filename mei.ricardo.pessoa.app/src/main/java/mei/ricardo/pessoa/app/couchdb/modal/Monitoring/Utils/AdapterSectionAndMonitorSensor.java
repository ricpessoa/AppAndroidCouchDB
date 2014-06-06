package mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;
import mei.ricardo.pessoa.app.utils.Utilities;

public class AdapterSectionAndMonitorSensor extends ArrayAdapter<InterfaceItem> {
	private Context context;
	private ArrayList<InterfaceItem> items;
	private LayoutInflater vi;

	public AdapterSectionAndMonitorSensor(Context context, ArrayList<InterfaceItem> items) {
		super(context,0, items);
		this.context = context;
		this.items = items;
		vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		final InterfaceItem i = items.get(position);
		if (i != null) {
			if(i.isSection()){
				SectionItem si = (SectionItem)i;
				v = vi.inflate(R.layout.list_item_section, null);

				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
				final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
				sectionView.setText(si.getTitle());
			}else{
                MonitorSensor monitorSensor = (MonitorSensor)i;
				v = vi.inflate(R.layout.list_item_entry, null);
                final ImageView imageView = (ImageView)v.findViewById(R.id.icon);
				final TextView title = (TextView)v.findViewById(R.id.deviceName);
				final TextView subtitle = (TextView)v.findViewById(R.id.deviceDescription);
				if(imageView!=null)
                    imageView.setImageDrawable(monitorSensor.getImage());
				if (title != null)
					title.setText(monitorSensor.getTitle());
				if(subtitle != null)
					subtitle.setText(Utilities.ConvertTimestampToDateFormat(monitorSensor.getTimestamp()));
			}
		}
		return v;
	}

    public void updateDeviceList(ArrayList<InterfaceItem> results) {

        items = results;
        //Triggers the list update
        notifyDataSetChanged();
    }

}
