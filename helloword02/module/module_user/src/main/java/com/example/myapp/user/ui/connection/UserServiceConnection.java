package com.example.myapp.user.ui.connection;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.jelly.myapp2.IBindService;

public class UserServiceConnection implements ServiceConnection {
    private IBindService anInterface;

    public IBindService getIBindService() {
        return anInterface;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.e("service", "activity MainServiceConnection onServiceConnected");
        try {
            IBindService anInterface = IBindService.Stub.asInterface(service);
            String basicTypes = anInterface.getTextByInput(50);
            Log.e("service", "activity MainServiceConnection onServiceConnected basicTypes:" + basicTypes);
        } catch (RemoteException e) {
            Log.e("service", "activity MainServiceConnection onServiceConnected RemoteException:" + e);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.e("service", "activity MainServiceConnection onServiceDisconnected");
    }
}