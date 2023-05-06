package com.jelly.myapp2.aidl;

import android.os.RemoteException;

import com.jelly.myapp2.IBindService;

public class BindServiceImpl extends IBindService.Stub {

    @Override
    public String getTextByInput(int input) throws RemoteException {
        return "hello :" + input;
    }
}
