package mei.ricardo.pessoa.app.couchdb.modal;

/**
 * Created by rpessoa on 13/05/14.
 */
public class CouchDocument {
    private String _id;
    private String _rev;

    public CouchDocument() {

    }

    public CouchDocument(String _id) {
        this._id = _id;
    }

    public CouchDocument(String _id, String _rev) {
        this._id = _id;
        this._rev = _rev;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
