import java.io.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class DbConnectivity
{
    public static java.sql.Connection myConnection() throws ClassNotFoundException, SQLException 
    {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        
        String url , user , pass;
        url = "jdbc:oracle:thin:@localhost:1521:orcl";
        user = "persistent_sqlplus";
        pass = "persi";
        
        java.sql.Connection con =  DriverManager.getConnection(url, user, pass);
        return con;    
    }
}

public class ContactService 
{
    static ArrayList<Contact> contactlist = new ArrayList<>();
    static Set<Contact> contactset = new HashSet<>();
    static Scanner sc = new Scanner(System.in);
    //method to display data from file
    public void displayList(List<Contact> l)
    {
        for(Contact e:l)
        {
            System.out.println(e);
        }
    }
    
    public void displaySet(Set<Contact> s)
    {
        for(Contact e:s)
        {
            System.out.println(e);
        }
    }
    //method to add new data in contactlist
    public void addContact(Contact contact,List<Contact> contacts)
    {
        System.out.println("Enter ContactID");
        int cid = sc.nextInt();
        System.out.println("Enter Contact Name");
        String name = sc.next();
        System.out.println("Enter Email");
        String email = sc.next();
        System.out.println("Enter how many contact numbers you want to enter");
        int n = sc.nextInt();
        ArrayList<String> plist = new ArrayList<>();
        for(int i=1;i<=n;i++)
        {
            System.out.println("Enter Contact Number "+i+" : ");
            String ph = sc.next();
            plist.add(ph);
        }
        contactlist.add( new Contact(cid,name,email,plist));
        System.out.println("New Contact Added Successfully!");
    }
    
    public void removeContact(Contact contact, ArrayList<Contact> contacts)
    {
        System.out.println("Enter ContactID:");
        int id = sc.nextInt();
        for(Contact i: contacts)
        {
            if(i.getContactID()==id)
            {
                contacts.remove(i);
            }
        }
    }
    public Contact searchContactByName(ArrayList<Contact> contact)
    {
        System.out.println("Enter name :");
        String name = sc.next();
        for(Contact i : contact)
        {
            if(i.getContactName().equals(name))
            {
                return i;
            }
        }
        return null;
    }
    public List<Contact> SearchContactByNumber(String number, ArrayList<Contact> contact) 
    {
        List<Contact> result = new ArrayList<>();
        for(Contact i: contact)
        {
            //System.out.println(i.getContactNumber());
            for(int k=0;k<i.getContactNumber().size();k++)
            {
                if(i.getContactNumber().get(k).contains(number))
                {
                    result.add(i);
                }
            }
        }
        
        return result;
    }
    public void sortContactsByName(List<Contact> contacts)
    {
        Collections.sort(contacts, new Comparator<Contact>()
        {
            @Override
            public int compare(Contact o1, Contact o2) 
            {
                return o1.getContactName().compareTo(o2.getContactName());
            }
            
        });
        for(Contact i: contacts)
        {
            System.out.println(i);
        }
    }
    public void readContactsFromFile(List<Contact> contacts, String fname) throws FileNotFoundException, IOException
    {
        FileInputStream fis = new FileInputStream(fname);
        DataInputStream dis = new DataInputStream(fis);
        BufferedReader br = new BufferedReader(new InputStreamReader(dis));
        String strline;
        while((strline = br.readLine()) != null)
        {
            ArrayList<String> phlist = new ArrayList<>();
            //System.out.println(strline);
            String data[] = strline.split(",");
            if(data.length==3)
            {
                phlist.add("null");
            }
            for(int i=3;i<data.length;i++)
            {
                phlist.add(data[i]);
            }
            //System.out.println(phlist);
            contactlist.add( new Contact(Integer.parseInt(data[0]), data[1], data[2], phlist));
        }
    }
    
    public void addContactNumber(int contactId, String contactNo, ArrayList<Contact> contacts)
    {
        for(Contact i: contacts)
        {
            if(i.getContactID()==contactId)
            {
                i.getContactNumber().add(contactNo);
                ArrayList<String> s = (i.getContactNumber());
                i.setContactNumber(s);
            }
        }
        
    }
    
    public void populateContactFromDb()
    {
        try 
        {
            Connection con = DbConnectivity.myConnection();
            if(con!=null)
            {
                String query = "Select * from contact_tbl";
                PreparedStatement st = con.prepareStatement(query);
                ResultSet rs = st.executeQuery();
                while(rs.next())
                {
                    int id = rs.getInt(1);
                    String name = rs.getString(2);
                    String email = rs.getString(3);
                    ArrayList<String> cl = new ArrayList<>();
                    try {
                        String contactarr[] = rs.getString(4).split(",");
                        for (int i = 0; i < contactarr.length; i++) {
                            cl.add(contactarr[0]);
                        }
                    } catch (Exception e) 
                    {
                        //do nothing
                    }
                    contactset.add(new Contact(id, name, email,cl));
                }
                
            }
        } 
        catch (ClassNotFoundException |SQLException ex) 
        {
            System.out.println(ex);
        }
    }
    
    public void serializeContactDetails(List<Contact> contacts , String filename) throws FileNotFoundException, IOException
    {
        FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        for(Contact i:contacts)
        {
            oos.writeObject(i);
        }
    }
    public List<Contact> deserializeContact(String filename) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        List<Contact> result = new ArrayList<>();
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        result.add((Contact) ois.readObject());
        return result;
    }
    
    public void addContacts(List<Contact> existingContact,Set<Contact> newContacts)
    {
        for(Contact i: newContacts)
        {
            existingContact.add( new Contact(i.getContactID(), i.getContactName(), i.getEmail(), i.getContactNumber()));   
        }
    }
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException 
    {
        Contact c = new Contact();
        ContactService cs = new ContactService();
        String fname = "E:\\Persistent_Work\\Java_Assignments\\Assignment_10\\Contact.txt";
        File f = new File(fname);
        //file for Serialization & Deserialization
        String sfile = "E:\\Persistent_Work\\Java_Assignments\\SerializedFile.txt";
        
        System.out.println("Reading Contacts from File");
        cs.readContactsFromFile(contactlist, fname);
        System.out.println("------ContactList from File-------");
        cs.displayList(contactlist);
        System.out.println("-----------------------------------");
        System.out.println("1. Remove Contact from List");
        System.out.println("2. Search Contact By Name");
        System.out.println("3. Search Contact By Number");
        System.out.println("4. Add New/Update Contact number");
        System.out.println("5. Sort Contacts By Name");
        System.out.println("6. Add New Contact in List");
        System.out.println("7. Serialize Contact Details");
        System.out.println("8. Deserialize Contact");
        System.out.println("9. Populate Contacts From Db to Set");
        System.out.println("10. Add Contacts from DbSet to List");
        System.out.println("-------------------------------------");
        String choice="Y";
        while(choice.equals("Y"))
        {
            System.out.println("Enter your option :");
            int opt = sc.nextInt();
            switch(opt)
            {
                case 1:
                    System.out.println("Removing Conatct Record from List");
                    cs.removeContact(c, contactlist);
                    break;
                case 2:
                    System.out.println("Searching Contact By Name");
                    Contact result = cs.searchContactByName(contactlist);
                    System.out.println(result);
                    break;
                case 3:
                    System.out.println("Searching Contact By Number");
                    System.out.println("Enter number to search for ContactNumbers");
                    String num = sc.next();
                    List<Contact> ans = cs.SearchContactByNumber(num, contactlist);
                    cs.displayList(ans);
                    break;
                case 4:
                    System.out.println("Enter ContactID");
                    int id = sc.nextInt();
                    System.out.println("Enter ContactNo");
                    String contactNo = sc.next();
                    cs.addContactNumber(id, contactNo, contactlist);
                    break;
                case 5:
                    cs.sortContactsByName(contactlist);
                    cs.displayList(contactlist);
                    break;
                case 6:
                    cs.addContact(c, contactlist);
                    break;
                case 7:
                    cs.serializeContactDetails(contactlist,sfile );
                    break;
                case 8:
                    List<Contact> r = cs.deserializeContact(sfile);
                    cs.displayList(r);
                    break;
                case 9:
                    cs.populateContactFromDb();
                    cs.displaySet(contactset);
                    break;
                case 10:
                    cs.addContacts(contactlist, contactset);
                    System.out.println("Sucessfully added new Set of Contacts!");
                    cs.displayList(contactlist);
                    break;
                default:
                    System.out.println("Enter valid option.");

            }
            System.out.println("Want to Continue?[Y|N]");
            choice = sc.next();
        }
        System.out.println("Done!");
    }
    
}
