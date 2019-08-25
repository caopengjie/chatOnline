package sounds;

/*
 * ChatCapture.java	21/06/07
 * author: Max
 * MSN: zengfc@21cn.com
 * QQ: 22291911
 * Email: zengfc@21cn.com
 *
 */


import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.MediaLocator;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;


public class ChatCapture {

    MediaLocator audioCapDevLoc = null;
    MediaLocator videoCapDevLoc = null;
    CaptureDeviceInfo audioCapDevInfo = null;
    CaptureDeviceInfo videoCapDevInfo = null;
    Vector audioCapDevList = null;
    Vector videoCapDevList = null;

    public ChatCapture() {
        audioCapDevList = CaptureDeviceManager.getDeviceList(new AudioFormat(
                AudioFormat.LINEAR));
        videoCapDevList = CaptureDeviceManager.getDeviceList(new VideoFormat(
                VideoFormat.YUV));
        if ((audioCapDevList.size() > 0)) {
            audioCapDevInfo = (CaptureDeviceInfo) audioCapDevList.elementAt(0);
            audioCapDevLoc = audioCapDevInfo.getLocator();
        }
        if ((videoCapDevList.size() > 0)) {
            videoCapDevInfo = (CaptureDeviceInfo) videoCapDevList.elementAt(0);
            videoCapDevLoc = videoCapDevInfo.getLocator();
        }
    }

    public String[] getAudioCapDevName() {
        CaptureDeviceInfo cdi = null;
        String capDevName[] = new String[audioCapDevList.size()];
        if (audioCapDevList.size() > 0) {
            for (int i = 0; i < audioCapDevList.size(); i++) {
                cdi = (CaptureDeviceInfo) audioCapDevList.elementAt(i);
                capDevName[i] = cdi.getName();
            }
        } else {
            return null;
        }
        return capDevName;
    }

    public String[] getVideoCapDevName() {
        CaptureDeviceInfo cdi = null;
        String capDevName[] = new String[videoCapDevList.size()];
        if (videoCapDevList.size() > 0) {
            for (int i = 0; i < videoCapDevList.size(); i++) {
                cdi = (CaptureDeviceInfo) videoCapDevList.elementAt(i);
                capDevName[i] = cdi.getName();
            }
        } else {
            return null;
        }
        return capDevName;
    }

}
