package mei.ricardo.pessoa.app.couchdb.modal;

import com.couchbase.lite.Document;

import mei.ricardo.pessoa.app.couchdb.CouchDB;

/**
 * Created by rpessoa on 17-09-2014.
 */
public class Profile {
    private static String TAG = Profile.class.getCanonicalName();
    /**
     * this attributes is to get properties of documents
     */
    public static final String doc_id = "profile";
    private final String doc_name = "name";
    private final String doc_email = "email";
    private final String doc_full_name = "full_name";
    private final String doc_country = "country";
    private final String doc_mobile_phone = "mobile_phone";

    private String name, email, full_name, country, mobile_phone;
    private Integer numberOfDevice;

    private Profile() {
    }

    public Profile(String name, String email, String full_name, String conunty, String mobile_phone) {
        this.name = name;
        this.email = email;
        this.full_name = full_name;
        this.country = conunty;
        this.mobile_phone = mobile_phone;
    }

    public static Profile getProfile() {
        Document document = CouchDB.getmCouchDBinstance().getDatabase().getExistingDocument(doc_id);
        if (document == null)
            return null;

        Profile tempProfile = new Profile();
        tempProfile.name = document.getProperty(tempProfile.doc_name).toString();
        tempProfile.email = document.getProperty(tempProfile.doc_email).toString();
        tempProfile.full_name = document.getProperty(tempProfile.doc_full_name).toString();
        tempProfile.country = document.getProperty(tempProfile.doc_country).toString();
        tempProfile.mobile_phone = document.getProperty(tempProfile.doc_mobile_phone).toString();
        tempProfile.numberOfDevice = Device.getAllDevicesNotDeleted().size();
        return tempProfile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    public Integer getNumberOfDevice() {
        return numberOfDevice;
    }
}
