package ru.kdev.phpchat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.*;

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
        Metrics metrics = new Metrics(this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            public void run() {
                if(getConfig().contains("last_chat_url") && getConfig().contains("sended_url"))
                {
                    URL url = null;
                    try {
                        url = new URL(getConfig().getString("last_chat_url"));
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        assert url != null;
                        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null)
                        {
                            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("chat_mess").replace("{MESS}", inputLine)));
                            StringBuffer sb = null;
                            String data = URLEncoder.encode("sended", "UTF-8") + "="
                                    + 1;
                            URL url1 = new URL(getConfig().getString("sended_url"));
                            HttpURLConnection conn = null;
                            conn = (HttpURLConnection) url1.openConnection();
                            conn.setDoOutput(true);
                            conn.setRequestMethod("POST");
                            OutputStreamWriter osw = null;
                            osw = new OutputStreamWriter(
                                    conn.getOutputStream());
                            osw.write(data);
                            osw.flush();
                            BufferedReader br = new BufferedReader(new InputStreamReader(
                                    conn.getInputStream()));
                            String in2 = "";
                            sb = new StringBuffer();
                            if ((in2 = br.readLine()) != null) break;
                            sb.append(in2).append("\n");
                            osw.close();
                            br.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }, 0L, 100L);
    }

    @EventHandler
    public void onChat(final AsyncPlayerChatEvent e) throws IOException {
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            public void run() {
                StringBuffer sb = null;
                String buildedMsg = e.getPlayer().getName() + ": " + e.getMessage();
                String data = null;
                try {
                    data = URLEncoder.encode("message", "UTF-8") + "="
                            + URLEncoder.encode(buildedMsg, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                URL url = null;
                try {
                    url = new URL(getConfig().getString("url"));
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                assert conn != null;
                conn.setDoOutput(true);
                try {
                    conn.setRequestMethod("POST");
                } catch (ProtocolException ex) {
                    ex.printStackTrace();
                }
                OutputStreamWriter osw = null;
                try {
                    osw = new OutputStreamWriter(
                            conn.getOutputStream());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    osw.write(data);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    osw.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                String in = "";
                sb = new StringBuffer();

                while (true) {
                    try {
                        if (!((in = br.readLine()) != null)) break;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    sb.append(in).append("\n");
                }

                try {
                    osw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
