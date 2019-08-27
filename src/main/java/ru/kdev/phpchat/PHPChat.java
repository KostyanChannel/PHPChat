package ru.kdev.phpchat;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class PHPChat extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        File confFile = new File(getDataFolder() + File.separator + "config.yml");
        if(!confFile.exists())
        {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) throws IOException {
        StringBuffer sb = null;
        String buildedMsg = e.getPlayer().getName() + ": " + e.getMessage();
        String data = URLEncoder.encode("message", "UTF-8") + "="
                + URLEncoder.encode(buildedMsg, "UTF-8");
        URL url = new URL(getConfig().getString("url"));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        OutputStreamWriter osw = new OutputStreamWriter(
                conn.getOutputStream());
        osw.write(data);
        osw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));

        String in = "";
        sb = new StringBuffer();

        while ((in = br.readLine()) != null) {
            sb.append(in).append("\n");
        }

        osw.close();
        br.close();
    }
}
