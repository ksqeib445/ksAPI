package fun.ksmc.test;

import com.ksqeib.ksapi.mysql.KDatabase;
import com.ksqeib.ksapi.mysql.Kmysqldatabase;
import com.ksqeib.ksapi.mysql.serializer.KSeri;

import java.util.ArrayList;
import java.util.List;
//不太会写test 这只是单纯试一下
public class DatabaseTest {
    public static void main(String[] args) {
        KDatabase<SaveObj> kd = new Kmysqldatabase<SaveObj>("localhost", "test", "test", "root", "ksqeib", SaveObj.class, true
                ,null);
        SaveObj saveObj=new SaveObj();
        saveObj.putData("FFF!");
        saveObj.putData("FFF@");
        saveObj.putData("FFF#");
        kd.save("test",saveObj);
        for(String get:kd.load("test",new SaveObj()).list){
            System.out.println(get);
        }
    }

    static class SaveObj {
        @KSeri("save")
        private List<String> list=new ArrayList<>();
        void putData(String data){
            list.add(data);
        }
    }
}
