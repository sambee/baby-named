package hhf.baby.named;

import java.io.*;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */
public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        loadAllNames();
        computeName();
//        Named n = new Named();
//        n.fullName = "黄靖曦";
//        n.first = "黄";
//        n.second = "靖";
//        n.third = "曦";
//        doIt(n);
        outputAllNames();
    }

    private static void loadAllNames() throws IOException, SQLException {
        File f = new File("named.txt");
        long size = f.length();
        char[] chars = new char[(int)size];
        InputStreamReader isr = new InputStreamReader(new FileInputStream(f),"UTF-8");
        isr.read(chars);
        isr.close();
        String s =  new String(chars).replaceAll("\\r", "");
        String[] names = s.split("\n");
        for(int i=0; i<names.length;i++){

            if(names[i].length()>0 && Database.getInst().get(names[i])==null){
                System.out.println("Name:'" + names[i] + "'");
                Database.getInst().addName(names[i].trim());

            }

        }
    }



    private static void computeName() throws SQLException, UnsupportedEncodingException {
        List<Named> all = Database.getInst().all();

        Named named;
        for(int i=0; i<all.size(); i++) {


            named = all.get(i);
            System.out.println( i + " " + named.fullName);
            if(named.first!=null){
                continue;
            }

            named.first = named.fullName.substring(0,1);
            named.second = named.fullName.substring(1,2);
            if(named.fullName.length()==3) {
                named.third = named.fullName.substring(2, 3);
            }
            else{
                named.third ="";
            }
            if(named.score==0.0) {
                doIt(named);
            }
        }
    }

    private static void doIt(Named named) throws UnsupportedEncodingException, SQLException {
        String url = "http://www.51bazi.com/sm/xm.asp";
        String params = "xing=" + encode(named.first) + "&ming=" + encode(named.second + named.third) + "&xingbie=%C4%D0&xuexing=O&nian=2016&yue=10&ri=28&hh=21&mm=44";
        String resp = HttpRequest.sendPost(url, params);
        System.out.print(resp);
        String tag = "<td height=\"41\" align=\"center\"><div class=\"tzg\">";
        int start1 = resp.indexOf(tag);
        int len1 = resp.indexOf("\n", start1);

        named.wx1 = getWuXing(resp.substring(start1, len1));

        String tag2 = "<td align=\"center\"><div class=\"tzg\">";
        int start2 = resp.indexOf(tag2);
        int len2 = resp.indexOf("\n", start2);
        try {
            named.wx2 = getWuXing(resp.substring(start2, len2));
        }
        catch (Exception ex){
            ex.printStackTrace();
            return;
        }
        if(named.fullName.length()==3) {
            String tag3 = "<td align=\"center\"><div class=\"tzg\">"+named.third;
            int start3 = resp.indexOf(tag3);
            int len3 = resp.indexOf("\n", start3);
            named.wx3 = getWuXing(resp.substring(start3, len3));
        }
        String s = "<td height=\"37\"><div class=pf>得分：";
        int start4 = resp.indexOf(s) + s.length();
        int end4 = resp.indexOf("<", start4);

        String score = resp.substring(start4, end4);
        named.score = Float.valueOf(score);
        Database.getInst().save(named);
    }

    private static  String getWuXing(String str){
        String wuxing= "金木水火土";
        for(int i=0 ;  i<wuxing.length(); i++){
            if(str.contains(wuxing.substring(i,i+1))){
                return wuxing.substring(i, i+1);
            }
        }
        return "";
    }

    private static  String encode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, "GBK");
    }


    public static void outputAllNames() throws SQLException, IOException {
        List<Named> all = Database.getInst().all();
        Collections.sort(all, new Comparator<Named>(){

            @Override
            public int compare(Named o1, Named o2) {
                if (o1.score!=null && o2.score!=null){
                    return o1.score.compareTo(o2.score);
                }
                return 0;
            }
        });
        File f = new File("named.txt");
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");

        for(int i=0; i<all.size();i++) {
            try {
                osw.write(all.get(i).fullName + "\n");
            } catch (Exception ex) {

            }
        }
        osw.close();
    }

}
