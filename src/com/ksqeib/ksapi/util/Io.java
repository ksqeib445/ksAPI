package com.ksqeib.ksapi.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Io {
    //ע文件列
    private Hashtable<String, FileConfiguration> FileList = new Hashtable<>();
    private JavaPlugin plugin;
    public Boolean hasData = false;
    public Random rm = new Random();
    public HashMap<String, FileConfiguration> configs = new HashMap<>();
    public HashMap<String, Boolean> isinconfigs = new HashMap<>();
    //注册需要的参

    private File DataFile;
    private String databasepath;

    // 构造
    public Io(JavaPlugin main, Boolean hasdata) {
        this.plugin = main;
        this.hasData = hasdata;
        init();
    }

    public Io(JavaPlugin main) {
        this.plugin = main;
    }

    public void init() {
        //加载必要的文件
        if (hasData) {
            FileList = new Hashtable();
            FileList.clear();
            databasepath = "data";
            if (plugin.getConfig().getString("datapath") != null)
                databasepath = plugin.getConfig().getString("datapath");
            createDir(databasepath);
            databasepath = DataFile.getAbsolutePath();
        }
    }

    public void reload() {
        saveandcleardata();
        for (Map.Entry<String, FileConfiguration> ac : configs.entrySet()) {
            try {
                ac.setValue(loadYamlFile(ac.getKey() + ".yml", isinconfigs.get(ac.getKey())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void toStringListAndSave(HashMap<String, String> in, String name) {
        Set<String> ins = in.keySet();
        List<String> keys = new ArrayList<>(ins);
        Collections.sort(keys);
        for (String key : keys) {
            loadData(name).set(key, new String[]{in.get(key)});
        }
        saveandcleardata();
    }

    //configs!
    public void loadaConfig(String in, Boolean isin) {
        String name = in.toLowerCase();
        configs.put(name, loadYamlFile(name + ".yml", isin));
        isinconfigs.put(name, isin);

    }

    public FileConfiguration getaConfig(String in) {
        String name = in.toLowerCase();
        return configs.get(name);
    }

    public static void jsonCreate(JsonObject json, Class cl, JsonSerializationContext context, Object value) {
        HashMap<String, Type> table = new HashMap<>();
        try {
            Io.initTables(table, cl);
            for (String keys : table.keySet()) {
                //Load
                Field fi = Io.getObjFielddeep(keys, value, 0);
                if (fi != null) {
                    fi.setAccessible(true);
                    Object obj = fi.get(value);
                    if (obj != null) {
                        json.add(fi.getName(), context.serialize(obj));
                    }
                }

            }
        } catch (Exception e) {
            Bukkit.getLogger().warning(value.getClass().getTypeName());
        }
    }

    public static Field getObjFielddeep(String key, Object value, int i) {
        Field fi = null;
        Class cl = null;
        try {
            cl = Class.forName(value.getClass().getTypeName());
            fi = cl.getDeclaredField(key);
        } catch (NoSuchFieldException nc) {
            i++;
            if (i != 3)
                fi = Io.getFielddeep(key, cl.getSuperclass(), i);
        } catch (ClassNotFoundException e1) {
        }
        return fi;
    }

    public static Field getFielddeep(String key, Class cl, int i) {
        Field fi = null;
        try {
            fi = cl.getDeclaredField(key);
        } catch (NoSuchFieldException nc) {
            i++;
            if (i != 3)
                fi = getFielddeep(key, cl.getSuperclass(), i);
        }
        return fi;
    }

    public static void initTables(HashMap<String, Type> table, Class cl) {
        for (Field fi : cl.getDeclaredFields()) {
            if (!Modifier.isTransient((fi.getModifiers())) && !Modifier.isStatic(fi.getModifiers()))
                table.put(fi.getName(), fi.getGenericType());
        }
        if (cl.getSuperclass() != null) {
            initTables(table, cl.getSuperclass());
        }
    }

    public static void onekeySetField(Class cl, Object result, JsonObject json, JsonDeserializationContext context) {
        try {
            HashMap<String, Type> table = new HashMap<>();
            initTables(table, cl);
            for (String keys : table.keySet()) {
                //LOAD
                Field fi = Io.getFielddeep(keys, cl, 0);
                fi.setAccessible(true);
                JsonElement obj = json.get(keys);
                if (obj != null)
                    fi.set(result, context.deserialize(obj, table.get(keys)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createResouce(String path) {
        //从内部创建
        plugin.saveResource(path, false);
    }

    private boolean createDir(String path) {
        //创建目录的方法
        DataFile = new File(plugin.getDataFolder(), path);
        return DataFile.mkdir();
    }

    public FileConfiguration loadYamlFile(String str, Boolean isin) {
        //加载资源的方法

        File mF;
        if (isin) {
            //防止不存在
            mF = new File(plugin.getDataFolder(), str);
            if (!mF.exists())
                createResouce(str);
        } else {
            mF = new File(str);
            try {
                mF.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(mF);

    }

    public FileConfiguration loadData(String data) {
        //加载数据的方法
        FileConfiguration ym;
        //是否存在对应key
        Boolean have = FileList.containsKey(data);
        if (!have) {
            //若列表里没有
            ym = loadDataYaml(data + ".yml");
            FileList.put(data, ym);
        } else {
            //列表有
            ym = FileList.get(data);
        }
        return ym;
    }

    private FileConfiguration loadDataYaml(String name) {
        //读取YAML
        File fm = new File(DataFile, name);
        return YamlConfiguration.loadConfiguration(fm);
    }

    public List<String> getDataList() {

        File[] fileList = DataFile.listFiles();
        //获取目录表
        List<String> nameList = new ArrayList();
        //新建一个name集合
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isFile()) {
                //判断是否为文件
                String filename = fileList[i].getName().substring(0, fileList[i].getName().length() - 4);
                nameList.add(filename);
            }
        }

        return nameList;
    }

    public void disabled() {
        //保存
        saveandcleardata();
    }

    public void saveandcleardata() {
        if (hasData) {
            Set<String> filekeyset = FileList.keySet();
            for (String key : filekeyset) {
                //遍历开撸
                try {
                    FileList.get(key).save(new File(DataFile, key + ".yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            FileList.clear();
        }
    }

    public static boolean isWindows() {
        Properties ppt = System.getProperties();
        String systemname = ppt.getProperty("os.name");
        return systemname.contains("Windows");
    }

    public static HashMap<String, String> getAll(FileConfiguration file) {
        //读取配置
        Set<String> lis = file.getKeys(false);
        HashMap<String, String> hash = new HashMap<String, String>();
        for (String string : lis) {
            //获取全部样式
            hash.put(string, file.getString(string).replace("&", "§"));
        }
        return hash;
    }

    public static HashMap<String, List<String>> getAlllist(FileConfiguration file) {
        //读取配置
        Set<String> lis = file.getKeys(false);
        HashMap<String, List<String>> hash = new HashMap<String, List<String>>();
        for (String key : lis) {
            //获取全部样式
            hash.put(key, file.getStringList(key));
        }
        return hash;
    }

    //单个读取
    public static void loadintlist(HashMap<String, Integer> change, String list, FileConfiguration config) {
        MemorySection items = (MemorySection) config.get(list);
        Set<String> lis = items.getKeys(false);
        for (String string : lis) {
            change.put(string, config.getInt(list + "." + string));
        }
    }

    public static void loaddoublelist(HashMap<String, Double> change, String list, FileConfiguration config) {
        MemorySection items = (MemorySection) config.get(list);
        Set<String> lis = items.getKeys(false);
        for (String string : lis) {
            change.put(string, config.getDouble(list + "." + string));
        }
    }

    public static void loaditemlist(HashMap<String, ItemStack> change, String list, FileConfiguration config) {
        MemorySection items = (MemorySection) config.get(list);
        Set<String> lis = items.getKeys(false);
        for (String string : lis) {
            change.put(string, config.getItemStack(list + "." + string));
        }
    }

    public static void loadstringlist(HashMap<String, String> change, String list, FileConfiguration config) {
        MemorySection items = (MemorySection) config.get(list);
        Set<String> lis = items.getKeys(false);
        for (String string : lis) {
            change.put(string, config.getString(list + "." + string.replace("&", "§")));
        }
    }

    public static void loadbooleanlist(HashMap<String, Boolean> change, String list, FileConfiguration config) {
        MemorySection items = (MemorySection) config.get(list);
        Set<String> lis = items.getKeys(false);
        for (String string : lis) {
            change.put(string, config.getBoolean(list + "." + string));
        }
    }

    public boolean rand(double persent, double max) {
        double thistime = rm.nextDouble() * max;
        return thistime <= persent;
    }

    public int randInt(int min, int max) {

        int randomNum = rm.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public boolean rand100(int prob) {
        return randInt(0, 100) <= prob;
    }

    public static int getRandom(int min, int max) {
        if (min == max) {
            return 0;
        }
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    //读取
    public static void loadintlist(ConcurrentHashMap<String, Integer> change, String list, FileConfiguration config) {
        MemorySection items = (MemorySection) config.get(list);
        Set<String> lis = items.getKeys(false);
        for (String string : lis) {
            change.put(string, config.getInt(list + "." + string));
        }
    }

    public static void loaddoublelist(ConcurrentHashMap<String, Double> change, String list, FileConfiguration config) {
        MemorySection items = (MemorySection) config.get(list);
        Set<String> lis = items.getKeys(false);
        for (String string : lis) {
            change.put(string, config.getDouble(list + "." + string));
        }
    }

    public static void loaditemlist(ConcurrentHashMap<String, ItemStack> change, String list, FileConfiguration config) {
        MemorySection items = (MemorySection) config.get(list);
        Set<String> lis = items.getKeys(false);
        for (String string : lis) {
            change.put(string, config.getItemStack(list + "." + string));
        }
    }

    public static void loadstringlist(ConcurrentHashMap<String, String> change, String list, FileConfiguration config) {
        MemorySection items = (MemorySection) config.get(list);
        Set<String> lis = items.getKeys(false);
        for (String string : lis) {
            change.put(string, config.getString(list + "." + string.replace("&", "§")));
        }
    }

    public static void loadbooleanlist(ConcurrentHashMap<String, Boolean> change, String list, FileConfiguration config) {
        MemorySection items = (MemorySection) config.get(list);
        Set<String> lis = items.getKeys(false);
        for (String string : lis) {
            change.put(string, config.getBoolean(list + "." + string));
        }
    }
}
