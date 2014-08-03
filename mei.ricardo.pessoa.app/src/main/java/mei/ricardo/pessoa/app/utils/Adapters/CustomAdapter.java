package mei.ricardo.pessoa.app.utils.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TreeSet;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_PanicButton;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.ActivityMonitorSensorGPS;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.ActivityListMonitorSensorPBTempBatt;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.ActivityMonitorSensorDetail;
import mei.ricardo.pessoa.app.utils.InterfaceItem;
import mei.ricardo.pessoa.app.utils.SectionItem;
import mei.ricardo.pessoa.app.utils.Utils;

/**
 * Created by rpessoa on 30/07/14.
 * reference http://javatechig.com/android/listview-with-section-header-in-android
 */

public class CustomAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<InterfaceItem> mData = new ArrayList<InterfaceItem>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    public CustomAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final MonitorSensor item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addItems(final ArrayList<MonitorSensor> items) {
        mData.addAll(items);
        notifyDataSetChanged();

    }

    public void addSectionHeaderItem(final SectionItem item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public InterfaceItem getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.list_item_entry, null);
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.list_item_section, null);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mData.get(position).isSection()) {
            final SectionItem sectionItem = (SectionItem) mData.get(position);
            holder.imageButton = (ImageButton) convertView.findViewById(R.id.buttonMore);
            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Application.getmContext(), "click on more:" + sectionItem.getTitle(), Toast.LENGTH_SHORT).show();
                    if (sectionItem.getType().equals(Device.DEVICESTYPE.GPS.toString())) {
                        //show Safezones
                        Intent intent = new Intent(Application.getmContext(), ActivityMonitorSensorGPS.class);
                        intent.putExtra(ActivityMonitorSensorGPS.passVariableIDOfDevice, sectionItem.getDeviceMacAddress());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Application.getmContext().startActivity(intent);
                    } else {
                        //show List PB, Temperature, Battery
                        Intent intent = new Intent(Application.getmContext(), ActivityListMonitorSensorPBTempBatt.class);
                        intent.putExtra(ActivityListMonitorSensorPBTempBatt.passVariableIDOfDevice, sectionItem.getDeviceMacAddress());
                        intent.putExtra(ActivityListMonitorSensorPBTempBatt.passVariableTypeSensor, sectionItem.getType());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Application.getmContext().startActivity(intent);
                    }
                }
            });
            convertView.setOnClickListener(null);
            convertView.setOnLongClickListener(null);
            convertView.setLongClickable(false);
            holder.textViewTitle = (TextView) convertView.findViewById(R.id.list_item_section_text);
            holder.textViewTitle.setText(sectionItem.getTitle());
        } else {
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.textViewTitle = (TextView) convertView.findViewById(R.id.deviceName);
            holder.textViewDetail = (TextView) convertView.findViewById(R.id.deviceDescription);

            final MonitorSensor monitorSensor = (MonitorSensor) mData.get(position);
            if (holder.imageView != null)
                holder.imageView.setImageDrawable(monitorSensor.getImage());
            if (holder.textViewTitle != null)
                holder.textViewTitle.setText(monitorSensor.getTitle());
            if (holder.textViewDetail != null)
                holder.textViewDetail.setText(Utils.ConvertTimestampToDateFormat(monitorSensor.getTimestamp()));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //send mac address, subtype and timestamp to show detail
                    Intent intent = new Intent(Application.getmContext(), ActivityMonitorSensorDetail.class);
                    intent.putExtra(ActivityMonitorSensorDetail.passVariableMacAddress, monitorSensor.getMac_address());
                    intent.putExtra(ActivityMonitorSensorDetail.passVariableTimestamp, monitorSensor.getTimestamp());
                    intent.putExtra(ActivityMonitorSensorDetail.passVariableSubtypeSensor, monitorSensor.getSubtype());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Application.getmContext().startActivity(intent);
                }
            });
        }
        return convertView;
    }

    public static class ViewHolder {
        public TextView textViewTitle;
        public ImageView imageView;
        public TextView textViewDetail;
        public ImageButton imageButton;
    }

}
