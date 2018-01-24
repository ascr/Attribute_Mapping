import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Fiftypercent {

    public static void validmap(HSSFWorkbook work, String name, int rowi, HashMap<String, ArrayList<String>> allowed, int start, String split)
    {
        HSSFSheet fvalid = work.getSheet(name);
        HSSFRow vrowf = fvalid.getRow(rowi);

        for(Cell c:vrowf)
        {
            String s="";
            if(split!=null)
            {
                s=c.getStringCellValue();
                if(s.contains(split))
                {
                    s= s.substring(0,s.indexOf(split));
                }
            }
            else
                s=c.getStringCellValue();
            // System.out.println(s);
            ArrayList<String> add = new ArrayList<String>();
            int column = c.getColumnIndex();

            for(int st = start; st<= fvalid.getLastRowNum();st++)
            {
                if(fvalid.getRow(st).getCell(column)!=null)
                {
                    String value="";
                    try
                    {
                        value = fvalid.getRow(st).getCell(column).getStringCellValue();
                    }
                    catch (Exception e)
                    {
                        try {
                            value = String.valueOf(fvalid.getRow(st).getCell(column).getNumericCellValue());
                        }
                        catch (Exception y)
                        {
                            value = String.valueOf(fvalid.getRow(st).getCell(column).getBooleanCellValue());
                        }
                    }
                    //System.out.println(value);
                    String[] r = value.split(" ");
                    String fin = "";
                    for(String h:r)
                    {
                        if(!h.toLowerCase().equals(s.toLowerCase()))
                        {
                            fin+=h;
                            fin+=" ";
                        }

                    }
                    //if()
                    add.add(fin);
                    allowed.put(s,add);
                }
                else
                {
                    //allowed.put(s,add);
                    break;
                }
            }
        }
        //System.out.println(allowed);
    }

    public static String reduce(String s)
    {
        String ret = "";
        char[] array = s.toCharArray();
        for(char c: array)
        {
            if((c>=65 && c<=90) || (c>=97 && c<=122))
                ret+=c;
        }
        return ret.toLowerCase();
    }

    public static int map(String a, String f,ArrayList<String> alloweda, ArrayList<String> allowedf)
    {
        int count=0;
       // System.out.println(a+" "+f+" both non null");
        for(String s: alloweda)
        {
            for(String h: allowedf)
            {
                String t1 = reduce(h);
                String t2 = reduce(s);
                if((!t1.isEmpty() && !t2.isEmpty()) && (t1.equals(t2)))
                {
                    count++;
                    break;
                }
            }
        }
        if(count>=1)
            return 1;
        return 0;
    }

    public static int rank(String a, String f, ArrayList<String> alloweda, ArrayList<String> allowedf)
    {
        //System.out.println(a+" "+f);
        String amazon = reduce(a);
        String flipkart = reduce(f);

        if(amazon.equals(flipkart))
            return 1;
        else if(amazon.contains(flipkart))
            return 1;
        else if(flipkart.contains(amazon))
            return 1;
        else if(alloweda==null || allowedf==null)
        {
            if(alloweda==null && allowedf==null) {
               // System.out.println(a+" "+f+" both null");
                if (amazon.length() > flipkart.length()) {
                    String temp = amazon;
                    amazon = flipkart;
                    flipkart = temp;
                }
                int count = flipkart.length() - amazon.length();

                for (int u = 0; u < amazon.length(); u++) {
                    if (amazon.charAt(u) != flipkart.charAt(u))
                        count++;
                }
                if (count >= 5)
                    return 0;
                else
                    return 1;
            }
            else
            {
               // System.out.println(a+" "+f+" one null");
                return 0;
            }
        }
        else if(map(a,f,alloweda,allowedf)==1)
        {
            return 1;
        }
        return 0;
    }

    public static void main(String[] args) throws IOException {
        String file1 = "/Users/priyanka.patel/Downloads/Shirt.xls";
        String file2 = "/Users/priyanka.patel/Downloads/FormalShirts.xls";

        FileInputStream excelFile1 = new FileInputStream(file1);
        HSSFWorkbook work1 = new HSSFWorkbook(excelFile1);
        HSSFSheet f = work1.getSheet("shirt");

        FileInputStream excelFile2 = new FileInputStream(file2);
        HSSFWorkbook work2 = new HSSFWorkbook(excelFile2);
        HSSFSheet a = work2.getSheet("Template");

        ArrayList<String> fattributes = new ArrayList<String>();
        ArrayList<String> aattributes = new ArrayList<String>();
        HashMap<String, ArrayList<String>> allowedf = new HashMap<String, ArrayList<String>>();
        HashMap<String, ArrayList<String>> alloweda = new HashMap<String, ArrayList<String>>();

        HSSFRow row1 = f.getRow(0);
        for(Cell c: row1)
        {
            if(c!=null)
            {
                fattributes.add(c.getStringCellValue());
            }
        }

        HSSFRow row2 = a.getRow(1);
        for(Cell c: row2)
        {
            if(c!=null)
            {
                aattributes.add(c.getStringCellValue());
            }
        }

        validmap(work1, "Index",1,allowedf,2,null);
        validmap(work2, "Valid Values",0,alloweda,2," - [ shirt ]");

        for(String s: aattributes)
        {
            for(String d: fattributes)
            {
                int rank = rank(s,d,alloweda.get(s),allowedf.get(d));
                if(rank==1)
                    System.out.println(s+" "+d);
            }
        }

    }
}
