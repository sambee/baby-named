package hhf.baby.named;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */
public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        loadAllNames();
        computeName();
    }

    private static void loadAllNames() throws IOException, SQLException {
        File f = new File("named.txt");
        long size = f.length();
        char[] chars = new char[(int)size];
        InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
        isr.read(chars);
        isr.close();
        String s =  new String(chars);
        String[] names = s.split("\n");
        for(int i=0; i<names.length;i++){

            if(Database.getInst().get(names[i])==null){
                Database.getInst().addName(names[i].trim());
                System.out.println(names[i]);
            }

        }
    }

    private static void computeName() throws SQLException, UnsupportedEncodingException {
        List<Named> all = Database.getInst().all();

        Named named;
        for(int i=0; i<all.size(); i++) {


            named = all.get(i);
            named.first = named.fullName.substring(0,1);
            named.second = named.fullName.substring(1,2);
            named.third = named.fullName.substring(2,3);
            if(named.score==0.0) {
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
                named.wx2 = getWuXing(resp.substring(start2, len2));

                String tag3 = "<td align=\"center\"><div class=\"tzg\">";
                int start3 = resp.indexOf(tag3);
                int len3 = resp.indexOf("\n", start2);
                named.wx3 = getWuXing(resp.substring(start3, len3));

                String s = "<td height=\"37\"><div class=pf>得分：";
                int start4 = resp.indexOf(s) + s.length();
                int end4 = resp.indexOf("<", start4);

                String score = resp.substring(start4, end4);
                named.score = Float.valueOf(score);
                Database.getInst().save(named);
            }
        }
    }

    private static  String getWuXing(String str){
        String wuxing= "金木水火土";
        for(int i=0 ;  i<wuxing.length(); i++){
            if(str.contains(wuxing.substring(i,i+1))){
                return wuxing.substring(i, i+1);
            }
        }
        throw new RuntimeException(str);
    }

    private static  String encode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, "GBK");
    }
}
