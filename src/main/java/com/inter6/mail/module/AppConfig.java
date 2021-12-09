package com.inter6.mail.module;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class AppConfig extends PropertiesConfiguration {

    @Autowired
    public void init() {
        this.setAutoSave(false);
        this.setDelimiterParsingDisabled(true);
    }

    public void load(File file, String encoding) throws IOException, ConfigurationException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            this.load(in, encoding);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public void save(File file, String encoding) throws IOException, ConfigurationException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            this.save(out, encoding);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}
