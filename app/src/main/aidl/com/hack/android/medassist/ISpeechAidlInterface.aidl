// ISpeechAidlInterface.aidl
package com.hack.android.medassist;

// Declare any non-default types here with import statements

import com.hack.android.medassist.ISpeechCallbackAidlInterface;
interface ISpeechAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    String testFunction(in String testInput);

    void registerCallback(ISpeechCallbackAidlInterface cb);
//
    void unregisterCallback(ISpeechCallbackAidlInterface cb);

}
