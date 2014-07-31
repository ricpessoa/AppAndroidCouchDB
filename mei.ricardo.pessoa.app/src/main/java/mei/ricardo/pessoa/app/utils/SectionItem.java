package mei.ricardo.pessoa.app.utils;

public class SectionItem implements InterfaceItem {

    private final String title;
    private final String type;
    private final String deviceMacAddress;

    public SectionItem(String title, String type, String macAddress) {
        this.title = title;
        this.type = type;
        this.deviceMacAddress = macAddress;
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
}
