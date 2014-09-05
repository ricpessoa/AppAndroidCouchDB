package mei.ricardo.pessoa.app.utils;

import java.util.ArrayList;

import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_NotHave;

public class SectionItem implements InterfaceItem {

    private final String title;
    private final String type;
    private final String deviceMacAddress;
    private final int numberOFEntriesInThisSection;

    public SectionItem(String title, String type, String macAddress, ArrayList<InterfaceItem> arrayList) {
        this.title = title;
        this.type = type;
        this.deviceMacAddress = macAddress;
        if (arrayList.size() == 1) {
            if (MS_NotHave.class.isAssignableFrom(arrayList.get(0).getClass())) {
                numberOFEntriesInThisSection = 0;
            } else
                numberOFEntriesInThisSection = 1;
        } else {
            this.numberOFEntriesInThisSection = arrayList.size();
        }

    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean isSection() {
        return true;
    }

    public String getType() {
        return type;
    }

    public String getDeviceMacAddress() {
        return deviceMacAddress;
    }

    public int getNumberOFEntriesInThisSection() {
        return numberOFEntriesInThisSection;
    }
}
