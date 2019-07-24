package com.ksqeib.ksapi.mysql.serializer;

import com.google.common.io.Files;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.Charset;

/**
 * UTF-8格式的配置文件（就是多了几句话而已）
 */
public class Utf8YamlConfiguration extends YamlConfiguration {

    public static Charset UTF8_CHARSET = Charset.forName("UTF-8");

    @Override
    public void load(File file) throws IOException, InvalidConfigurationException {
        Validate.notNull(file, "File cannot be null");

        StringBuilder builder = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader reader = new InputStreamReader(fis, UTF8_CHARSET);
             BufferedReader input = new BufferedReader(reader)) {

            String line;
            while ((line = input.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        }

        loadFromString(builder.toString());
    }

    @Override
    public void save(File file) throws IOException {
        Validate.notNull(file, "File cannot be null");

        Files.createParentDirs(file);

        String data = saveToString();

        FileOutputStream stream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(stream, UTF8_CHARSET);

        try {
            writer.write(data);
        } finally {
            writer.close();
        }
    }
}
