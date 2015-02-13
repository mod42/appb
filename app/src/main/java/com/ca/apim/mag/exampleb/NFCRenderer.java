/*
 * Created by awitrisna on 2013-11-15.
 * Copyright (c) 2013 CA Technologies. All rights reserved.
 */

package com.ca.apim.mag.exampleb;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.view.View;
import com.l7tech.msso.auth.PollingRenderer;

public class NFCRenderer extends PollingRenderer {

    @Override
    public View render() {
        NdefRecord record = NdefRecord.createMime("text/plain", provider.getUrl().getBytes());
        NdefMessage message = new NdefMessage(record);
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(context);
        if (adapter != null) {
            adapter.setNdefPushMessage(message, (Activity) context);
        }
        return null;
    }

    @Override
    public String getId() {
        return "qrcode";
    }
}
