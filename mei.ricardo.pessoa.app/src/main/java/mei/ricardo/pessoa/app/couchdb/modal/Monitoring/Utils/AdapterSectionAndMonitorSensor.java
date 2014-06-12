package mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.ActivityMonitorSensorGPS;
import mei.ricardo.pessoa.app.utils.Utilities;

public class AdapterSectionAndMonitorSensor extends ArrayAdapter<InterfaceItem> {
	private Context context;
	public ArrayList<InterfaceItem> items;
	private LayoutInflater vi;
    private int color = Color.TRANSPARENT;

	public AdapterSectionAndMonitorSensor(Context context, ArrayList<InterfaceItem> items) {
		super(context,0, items);
		this.context = context;
		this.items = items;
		vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
    public AdapterSectionAndMonitorSensor(Context context, ArrayList<InterfaceItem> items, int color) {
        super(context,0, items);
        this.context = context;
        this.items = items;
        this.color = color;
        vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
try {


        final InterfaceItem i = items.get(position);
		if (i != null) {
			if(i.isSection()){
				final SectionItem si = (SectionItem)i;
				v = vi.inflate(R.layout.list_item_section, null);
                ImageButton imageButton = (ImageButton) v.findViewById(R.id.buttonMore);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "click on more:"+si.getTitle(), Toast.LENGTH_SHORT).show();
                        if(si.getType().equals(Device.DEVICESTYPE.GPS.toString())){
                            //show entire safezones
                            Intent intent = new Intent(Application.getmContext(), ActivityMonitorSensorGPS.class);
                            intent.putExtra(ActivityMonitorSensorGPS.passVariableID,si.getDeviceMacAddress());
                           getContext().startActivity(intent);
                        }
                    }
                });
				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);
				final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
				sectionView.setText(si.getTitle());
			}else{
                MonitorSensor monitorSensor = (MonitorSensor)i;
				v = vi.inflate(R.layout.list_item_entry, null);
                RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.relative_layout_entry);
                relativeLayout.setBackgroundColor(color);

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
}catch (Exception ex){
  Log.d("_________________________________________-","_____________________________________");
}
		return v;
	}

//    public synchronized void updateDeviceList(ArrayList<InterfaceItem> results) {
//        try {
//            items.clear();
//            items.addAll(items);
//            // items = results;
//            //Triggers the list update
//            notifyDataSetChanged();
//        }catch (Exception ex){
//            Log.e("ERRRORRRR", "wtf happeend???");
//        }
//
//    }

    public void updateDeviceList(ArrayList<InterfaceItem> results) {
        try {
            items = results;
            //Triggers the list update
            notifyDataSetChanged();
        }catch (Exception ex){
            Log.e("ERRRORRRR", "wtf happeend???");
        }

    }

}
