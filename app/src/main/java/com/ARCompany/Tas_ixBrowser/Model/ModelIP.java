package com.ARCompany.Tas_ixBrowser.Model;

import android.content.ContentProvider;
import android.content.Context;

import com.ARCompany.Tas_ixBrowser.R;


public class ModelIP {
    private Context context;

    public ModelIP(Context context){
        this.context = context;
    }
    public boolean IsInTasix(String ip){

        String fullIpInBase2=ipToBase2(ip);

        String[] tasixIpAddresses = context.getResources().getStringArray( R.array.TasixIpAddresses);

        for (String item: tasixIpAddresses) {
            String[] itemParts = item.split("/");
            String itemIpInBase2=ipToBase2(itemParts[0]);
            int itemIpLength=Integer.parseInt(itemParts[1]);
            if(fullIpInBase2.substring(0,itemIpLength)
                    .equals(itemIpInBase2.substring(0,itemIpLength))){
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
