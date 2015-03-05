package cuneiform.tablet;

import java.io.Serializable;

public abstract class DatabaseObject implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -273299798224077536L;
	private int id = -1;

    public final int getID() {
        if (this.id == -1) {
            throw new IllegalStateException("id == -1");
        }
        return this.id;
    }

    protected final void setID(int id) {
        if (this.id != -1) {
            throw new IllegalStateException("id != -1");
        }
        this.id = id;
    }

    public final boolean isInserted() {
        return (this.id != -1);
    }
}
