package com.ARCompany.Tas_ixBrowser.Model;

import android.content.ContentProvider;
import android.content.Context;

import com.ARCompany.Tas_ixBrowser.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class ModelIP {
    private Context context;

    public ModelIP(Context context){
        this.context = context;
    }
    public boolean IsInTasix(String ip) throws IOException {

        String fullIpInBase2 = ipToBase2(ip);

        StringBuilder sb = new StringBuilder();
        InputStream is = context.getAssets().open("Files/all");
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        List<String> tasixIpAddresses = new ArrayList<>();   //context.getResources().getStringArray( R.array.TasixIpAddresses);
        String str;
        while ((str = br.readLine()) != null) {
            tasixIpAddresses.add(str);
        }
        br.close();

        for (String item : tasixIpAddresses) {
            String[] itemParts = item.split("/");
            String itemIpInBase2 = ipToBase2(itemParts[0]);
            int itemIpLength = Integer.parseInt(itemParts[1]);
            if (fullIpInBase2.substring(0, itemIpLength)
                    .equals(itemIpInBase2.substring(0, itemIpLength))) {
                return true;
            }
        }

        return false;
    }

    private String ipToBase2(String ip){
        String[] ipParts = ip.split("\\.");
        Integer first=Integer.parseInt(ipParts[0]);
        Integer second=Integer.parseInt(ipParts[1]);
        Integer third=Integer.parseInt(ipParts[2]);
        Integer fourth=Integer.parseInt(ipParts[3]);
        return String.format("%8s", Integer.toString(first, 2)).replace(' ','0') +
                String.format("%8s", Integer.toString(second, 2)).replace(' ','0') +
                String.format("%8s", Integer.toString(third, 2)).replace(' ','0') +
                String.format("%8s", Integer.toString(fourth, 2)).replace(' ','0');
    }
}
