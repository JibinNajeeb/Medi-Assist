package com.hack.android.alexa.interfaces.speaker;

import com.hack.android.alexa.interfaces.AvsItem;

/**
 * Directive to adjust the device volume
 *
 * {@link com.hack.android.alexa.data.Directive} response item type parsed so we can properly
 * deal with the incoming commands from the Alexa server.
 *
 * @author will on 5/21/2016.
 */
public class AvsAdjustVolumeItem extends AvsItem{
    private long adjustment;

    /**
     * Create a new AdjustVolume {@link com.hack.android.alexa.data.Directive}
     * @param adjustment the direction and amount of adjustment (1, -1).
     */
    public AvsAdjustVolumeItem(String token, long adjustment){
        super(token);
        this.adjustment = adjustment;
    }

    public long getAdjustment() {
        return adjustment;
    }
}
