import java.util.Hashtable;

public class Actor {
    private String stagename;
    private int birthYear;
    private String DBID;
    static Hashtable<String, Boolean> namesTable = new Hashtable<String, Boolean>();
    public Actor() {
    }
    public Actor(String stagename, int birthYear) {
        this.stagename = stagename;
        this.birthYear = birthYear;
    }
    public String getStagename() {
        return stagename;
    }
    public void setStagename(String name) {
         this.stagename = name;
    }

    public int getDob() {
        return birthYear;
    }
//    public void setDBID(String id) {
//        this.DBID = id;
//    }
    public String getDBID() {
        return DBID;
    }

    public void setDBID(String newDBID) {
        // check if the new DBID is already taken
        if (namesTable.containsKey(newDBID)) {
            throw new IllegalArgumentException("DBID already exists!");
        }

        // remove the old DBID from the namesTable if it existed
        if (DBID != null) {
            namesTable.remove(DBID);
        }

        // update the DBID field and add the new DBID to the namesTable
        DBID = newDBID;
        namesTable.put(newDBID, true);
    }

}
