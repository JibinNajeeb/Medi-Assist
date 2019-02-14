package com.hack.android.alexa.interfaces.playbackcontrol;

import com.hack.android.alexa.interfaces.AvsItem;

/**
 * {@link com.hack.android.alexa.data.Directive} to send a pause command to any app playing media
 *
 * This directive doesn't seem applicable to mobile applications
 *
 * @author will on 5/31/2016.
 */

public class AvsMediaPauseCommandItem extends AvsItem {
    public AvsMediaPauseCommandItem(String token) {
        super(token);
    }
}
