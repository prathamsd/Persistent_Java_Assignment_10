import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Contact implements Comparable<Contact>,Serializable
{
    private int contactID;
    private String contactName;
    private String Email;
    private ArrayList<String> contactNumber;

    public Contact(int contactID, String contactName, String Email, ArrayList<String> contactNumber) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.Email = Email;
        this.contactNumber = contactNumber;
    }

    public Contact() 
    {
        //default
    }
    
    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public ArrayList<String> getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(ArrayList<String> contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return "contactID=" + contactID + ", contactName=" + contactName + ", Email=" + Email + ", contactNumber=" + contactNumber;
    }

    @Override
    public int compareTo(Contact o) 
    {
        return (this.contactID-o.contactID);
    }
    
    
}
