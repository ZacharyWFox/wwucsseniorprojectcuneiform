package cuneiform;

import java.io.Serializable;

import cuneiform.tablet.DatabaseObject;

public class KnownDate extends DatabaseObject implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2833422862772279326L;
	public final String text;

    public KnownDate(int id, String text) {
        setID(id);
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
