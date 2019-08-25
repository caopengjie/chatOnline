package sounds;

/*
 * TranAndRec.java	21/06/07
 * author: Max
 * MSN: zengfc@21cn.com
 * QQ: 22291911
 * Email: zengfc@21cn.com
 *
 */

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.Vector;

import javax.media.*;
import javax.media.control.QualityControl;
import javax.media.control.TrackControl;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.rtp.*;
import javax.media.rtp.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TraAndRec implements ReceiveStreamListener, SessionListener,
        ControllerListener {

    String ipAddress;
    String serverAddr;
    String name;
    int serverPort;
    int localPort;
    int destCtrlPort;
    int destAudioDataPort;
    int destAudioCtrlPort;
    int destVideoDataPort;
    int destVideoCtrlPort;
    int ttl;
    boolean isMulticastAddress = false;
    boolean hasServer = false;

    MediaLocator audioCapDevLoc;
    MediaLocator videoCapDevLoc;
    Processor audioProcessor = null;
    Processor videoProcessor = null;
    DataSource audioDataOutput = null;
    DataSource videoDataOutput = null;
    RTPManager audioRTPMgr;
    RTPManager videoRTPMgr;
    TrackControl audioTracks[] = null;
    TrackControl videoTracks[] = null;
    boolean initAudioProcessorOK = false;
    boolean initVideoProcessorOK = false;

    DatagramSocket managerSock;
    DatagramSocket audioDataSock;
    DatagramSocket audioCtrlSock;
    DatagramSocket videoDataSock;
    DatagramSocket videoCtrlSock;

    ChatFrame chatFrame;
    Vector playerWindows = null;

    boolean dataReceived = false;
    Object dataSync = new Object();

    public TraAndRec(MediaLocator audioCapDevLoc, MediaLocator videoCapDevLoc,
                     String name, boolean hasServer, String serverAddr,
                     int serverPort,
                     ChatFrame chatFrame) throws SocketException {

        this.audioCapDevLoc = audioCapDevLoc;
        this.videoCapDevLoc = videoCapDevLoc;
        this.name = name;
        this.hasServer = hasServer;
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
        this.localPort = 23432;
        this.chatFrame = chatFrame;

        playerWindows = new Vector();
        UDPConnect();
    }

    public void setDestAddressAndPort(String ipAddress, int destCtrlPort,
                                      int destAudioDataPort,
                                      int destAudioCtrlPort,
                                      int destVideoDataPort,
                                      int destVideoCtrlPort) {
        this.ipAddress = ipAddress;
        this.destCtrlPort = destCtrlPort;
        this.destAudioDataPort = destAudioDataPort;
        this.destAudioCtrlPort = destAudioCtrlPort;
        this.destVideoDataPort = destVideoDataPort;
        this.destVideoCtrlPort = destVideoCtrlPort;
    }

    public void setMulticastAndTTL(int ttl) {
        this.isMulticastAddress = true;
        this.ttl = ttl;
    }

    public void UDPConnect() throws SocketException {

        managerSock = new DatagramSocket(localPort);
        audioDataSock = new DatagramSocket();
        audioCtrlSock = new DatagramSocket();
        videoDataSock = new DatagramSocket();
        videoCtrlSock = new DatagramSocket();

        if (hasServer) {//如果需要服务器
            new UDPTransmit(managerSock, "1 " + name, serverAddr, serverPort, false).
                    start();
            new UDPTransmit(audioDataSock, "2 " + name, serverAddr, serverPort, false).
                    start();
            new UDPTransmit(audioCtrlSock, "3 " + name, serverAddr, serverPort, false).
                    start();
            new UDPTransmit(videoDataSock, "4 " + name, serverAddr, serverPort, false).
                    start();
            new UDPTransmit(videoCtrlSock, "5 " + name, serverAddr, serverPort, false).
                    start();

            new UDPTransmit(managerSock, "7 " + name, serverAddr, serverPort, true).
                    start();
            new UDPTransmit(audioDataSock, "7 " + name, serverAddr, serverPort, true).
                    start();
            new UDPTransmit(audioCtrlSock, "7 " + name, serverAddr, serverPort, true).
                    start();
            new UDPTransmit(videoDataSock, "7 " + name, serverAddr, serverPort, true).
                    start();
            new UDPTransmit(videoCtrlSock, "7 " + name, serverAddr, serverPort, true).
                    start();

            new UDPReceive(managerSock, chatFrame).start();

        } else {//如果不需要服务器
            String msg = name + ":" + getLocalAddress() + ":" +
                         managerSock.getLocalPort() + ":" +
                         audioDataSock.getLocalPort() + ":" +
                         audioCtrlSock.getLocalPort() + ":" +
                         videoDataSock.getLocalPort() + ":" +
                         videoCtrlSock.getLocalPort();
            new UDPTransmit(managerSock, "0 " + msg, "255.255.255.255",
                            managerSock.getLocalPort(), false).start();
            new UDPReceive(managerSock, chatFrame, msg).start();
        }
    }

    public void needConnect(String msg) {
        new UDPTransmit(managerSock, "2 " + msg, ipAddress, destCtrlPort, false).
                start();//启动线程
    }

    public void sayBye() {
        if (hasServer) {
            new UDPTransmit(managerSock, "6 " + name, serverAddr, serverPort, false).
                    start();
        } else {
            new UDPTransmit(managerSock, "6 " + name + ":" + getLocalAddress(),
                            "255.255.255.255", localPort, false).start();
        }
    }

    public synchronized String startAudioTransmit() {
        System.err.println("  - Open audio RTP session for: addr: " + ipAddress +
                           " localPort: " + audioDataSock.getLocalPort() +
                           " destPort: " + destAudioDataPort);

        String result;
        result = createAudioDataOutput();
        if (result != null) {
            System.out.println(result);
        }

        result = createAudioRTPMgrs();
        if (result != null) {
            return result;
        }
        if (audioProcessor != null) {

            audioProcessor.start();
        }
        return null;
    }

    public synchronized String startVideoTransmit() {
        System.err.println("  - Open video RTP session for: addr: " + ipAddress +
                           " localPort: " + videoDataSock.getLocalPort() +
                           " destPort: " + destVideoDataPort);

        String result;
        result = createVideoDataOutput();
        if (result != null) {
            System.out.println(result);
        }

        result = createVideoRTPMgrs();
        if (result != null) {
            return result;
        }
        if (videoProcessor != null) {

            videoProcessor.start();
        }
        return null;
    }

    public void closeTransmit() {
        synchronized (this) {
            if (audioProcessor != null) {
                audioProcessor.stop();
                audioRTPMgr.removeSessionListener(this);
                audioRTPMgr.removeReceiveStreamListener(this);
                audioRTPMgr.removeTargets("Session ended.");
            }
            if (videoProcessor != null) {
                videoProcessor.stop();
                if (videoRTPMgr != null) {
                    videoRTPMgr.removeSessionListener(this);
                    videoRTPMgr.removeReceiveStreamListener(this);
                    videoRTPMgr.removeTargets("Session ended.");
                }
            }
            for (int i = 0; i < playerWindows.size(); i++) {
                try {
                    ((PlayerWindow) playerWindows.elementAt(i)).close();
                } catch (Exception e) {}
            }
            playerWindows.removeAllElements();
        }
    }

    public void closeVideoTransmit() {
        synchronized (this) {
            if (videoProcessor != null) {
                videoProcessor.stop();
                videoRTPMgr.removeSessionListener(this);
                videoRTPMgr.removeReceiveStreamListener(this);
                videoRTPMgr.removeTargets("Session ended.");
            }
        }
    }

    public String initAudioProcessor() {
        if (audioCapDevLoc == null) {
            return "Audio capture device locator is null";
        }

        DataSource ds;
        try {
            ds = Manager.createDataSource(audioCapDevLoc);
        } catch (Exception e) {
            return "Couldn't create DataSource";
        }

        try {
            audioProcessor = Manager.createProcessor(ds);
        } catch (NoProcessorException npe) {
            return "Couldn't create processor";
        } catch (IOException ioe) {
            return "IOException creating processor";
        }

        boolean result = waitForState(audioProcessor, Processor.Configured);
        if (result == false) {
            return "Couldn't configure processor";
        }

        audioProcessor.setContentDescriptor(new ContentDescriptor(
                ContentDescriptor.RAW_RTP));

        audioTracks = audioProcessor.getTrackControls();
        if (audioTracks == null || audioTracks.length < 1) {
            return "Couldn't find tracks in processor";
        }

        Format supported[];

        if (audioTracks[0].isEnabled()) {

            supported = audioTracks[0].getSupportedFormats();

            for (int j = 0; j < supported.length; j++) {
                chatFrame.audioQualityComboBox.addItem(supported[j]);
            }

            if (supported.length > 0) {
                audioTracks[0].setFormat(supported[0]); //默认选择第一个格式
            } else {
                audioTracks[0].setEnabled(false);
            }

        } else {
            audioTracks[0].setEnabled(false);
        }

        initAudioProcessorOK = true;

        return null;
    }

    public String initVideoProcessor() {

        if (videoCapDevLoc == null) {
            return "Video captue device locator is null";
        }

        DataSource ds;
        try {
            ds = Manager.createDataSource(videoCapDevLoc);
        } catch (Exception e) {
            return "Couldn't create DataSource";
        }

        try {
            videoProcessor = Manager.createProcessor(ds);
        } catch (NoProcessorException npe) {
            return "Couldn't create processor";
        } catch (IOException ioe) {
            return "IOException creating processor";
        }

        boolean result = waitForState(videoProcessor, Processor.Configured);
        if (result == false) {
            return "Couldn't configure processor";
        }

        videoTracks = videoProcessor.getTrackControls();
        if (videoTracks == null || videoTracks.length < 1) {
            return "Couldn't find tracks in processor";
        }

        videoProcessor.setContentDescriptor(new ContentDescriptor(
                ContentDescriptor.RAW_RTP));

        Format supported[];
        Format chosen;
        boolean atLeastOneTrack = false;

        if (videoTracks[0].isEnabled()) {

            supported = videoTracks[0].getSupportedFormats();

            for (int j = 0; j < supported.length; j++) {
                chatFrame.videoQualityComboBox.addItem(supported[j]);
            }

            if (supported.length > 0) {
                chosen = checkForVideoSizes(videoTracks[0].getFormat(),
                                            supported[0]); //默认选择第一个格式
                videoTracks[0].setFormat(chosen);
                atLeastOneTrack = true;
            } else {
                videoTracks[0].setEnabled(false);
            }
        } else {
            videoTracks[0].setEnabled(false);
        }

        if (!atLeastOneTrack) {
            return "Couldn't set any of the tracks to a valid RTP format";
        }

        initVideoProcessorOK = true;

        return null;
    }

    public void setAudioFormat(int index) {
        audioTracks[0].setFormat(audioTracks[0].getSupportedFormats()[index]);//数据格式
    }

    public void setVideoFormat(int index) {
        Format fmt = videoTracks[0].getSupportedFormats()[index];
        Format fmt2 = checkForVideoSizes(videoTracks[0].getFormat(), fmt);
        videoTracks[0].setFormat(fmt2);
    }

    public String createAudioDataOutput() {
        if (initAudioProcessorOK != true) {
            return "Couldn't init audio processor";
        }

        boolean result = waitForState(audioProcessor, Controller.Realized);
        if (result == false) {
            return "Couldn't realize processor";
        }

        audioDataOutput = audioProcessor.getDataOutput();

        return null;
    }

    public String createVideoDataOutput() {
        if (initVideoProcessorOK != true) {
            return "Couldn't init video processor";
        }

        boolean result = waitForState(videoProcessor, Controller.Realized);
        if (result == false) {
            return "Couldn't realize processor";
        }

        // Set the JPEG quality to .5.
        setJPEGQuality(videoProcessor, 0.5f);

        videoDataOutput = videoProcessor.getDataOutput();

        return null;
    }

    public String createAudioRTPMgrs() {

        InetAddress ipAddr;
        SendStream sendStream;

        try {
            if(audioRTPMgr == null){
                audioRTPMgr = RTPManager.newInstance();
            }
            audioRTPMgr.addSessionListener(this);
            audioRTPMgr.addReceiveStreamListener(this);


            ipAddr = InetAddress.getByName(ipAddress);

            if (isMulticastAddress == false) {
                audioRTPMgr.initialize(new RTPSocketAdapter(audioDataSock,
                        audioCtrlSock,
                        ipAddr, destAudioDataPort,
                        destAudioCtrlPort));
            } else {
                audioRTPMgr.initialize(new RTPSocketAdapter(ipAddr,
                        destAudioDataPort, ttl));
            }

            if (audioDataOutput != null) {
                sendStream = audioRTPMgr.createSendStream(audioDataOutput, 0);
                sendStream.start();
                System.err.println("  - Start audio transmit ");
            }
        } catch (Exception e) {
            return e.getMessage();
        }

        return null;
    }

    public String createVideoRTPMgrs() {

        InetAddress ipAddr;
        SendStream sendStream;

        try {
            if(videoRTPMgr == null){
                videoRTPMgr = RTPManager.newInstance();
            }
            videoRTPMgr.addSessionListener(this);
            videoRTPMgr.addReceiveStreamListener(this);

            ipAddr = InetAddress.getByName(ipAddress);

            if (isMulticastAddress == false) {
                videoRTPMgr.initialize(new RTPSocketAdapter(videoDataSock,
                        videoCtrlSock, ipAddr, destVideoDataPort,
                        destVideoCtrlPort));
            } else {
                videoRTPMgr.initialize(new RTPSocketAdapter(ipAddr,
                        destVideoDataPort, ttl));
            }

            if (videoDataOutput != null) {
                sendStream = videoRTPMgr.createSendStream(videoDataOutput, 0);
                sendStream.start();
                System.err.println("  - Start video transmit ");
            }
        } catch (Exception e) {
            return e.getMessage();
        }

        return null;
    }

    public synchronized void update(SessionEvent evt) {
        if (evt instanceof NewParticipantEvent) {
            Participant p = ((NewParticipantEvent) evt).getParticipant();
            System.err.println("  - A new participant had just joined: " +
                               p.getCNAME());
        }
    }

    public synchronized void update(ReceiveStreamEvent evt) {

        RTPManager mgr = (RTPManager) evt.getSource();
        Participant participant = evt.getParticipant(); // could be null.
        ReceiveStream stream = evt.getReceiveStream(); // could be null.

        if (evt instanceof RemotePayloadChangeEvent) {
            System.err.println("  - Received an RTP PayloadChangeEvent.");
            System.err.println("Sorry, cannot handle payload change.");
            System.exit(0);
        }

        else if (evt instanceof NewReceiveStreamEvent) {
            try {
                stream = ((NewReceiveStreamEvent) evt).getReceiveStream();
                DataSource ds = stream.getDataSource();

                // Find out the formats.
                RTPControl ctl = (RTPControl) ds.getControl(
                        "javax.media.rtp.RTPControl");
                if (ctl != null) {
                    System.err.println("  - Recevied new RTP stream: " +
                                       ctl.getFormat());
                } else {
                    System.err.println("  - Recevied new RTP stream");
                }

                if (participant == null) {
                    System.err.println(
                            "      The sender of this stream had yet to be identified.");
                } else {
                    System.err.println("      The stream comes from: " +
                                       participant.getCNAME());
                }

                Player p = Manager.createPlayer(ds);
                if (p == null) {
                    return;
                }

                p.addControllerListener(this);
                p.realize();
                if (isMulticastAddress &&
                    (ctl.getFormat() instanceof VideoFormat)) {
                    PlayerWindow pw = new PlayerWindow(p, stream); //建新窗口
                    playerWindows.addElement(pw);
                }

                // Notify intialize() that a new stream had arrived.
                synchronized (dataSync) {
                    dataReceived = true;
                    dataSync.notifyAll();
                }

            } catch (Exception e) {
                System.err.println("NewReceiveStreamEvent exception " +
                                   e.getMessage());
                return;
            }
        }

        else if (evt instanceof StreamMappedEvent) {
            if (stream != null && stream.getDataSource() != null) {
                DataSource ds = stream.getDataSource();
                // Find out the formats.
                RTPControl ctl = (RTPControl) ds.getControl(
                        "javax.media.rtp.RTPControl");
                System.err.println("  - The previously unidentified stream ");
                if (ctl != null) {
                    System.err.println("      " + ctl.getFormat());
                }
                System.err.println("      had now been identified as sent by: " +
                                   participant.getCNAME());
            }
        }

        else if (evt instanceof ByeEvent) {

            System.err.println("  - Got \"bye\" from: " + participant.getCNAME());
            PlayerWindow pw = find(stream);
            if (pw != null) {
                pw.close();
                playerWindows.removeElement(pw);
            }
        }
    }

    public synchronized void controllerUpdate(ControllerEvent ce) {

        Player p = (Player) ce.getSourceController();

        if (p == null) {
            return;
        }

        if (ce instanceof RealizeCompleteEvent) {
            if (isMulticastAddress) {
                PlayerWindow pw = find(p);
                if (pw != null) {
                    pw.initialize();
                    pw.setVisible(true);
                    pw.setResizable(false);
                }
            } else {
                if (p.getVisualComponent() != null) {
                    chatFrame.videoPanel.add(new PlayerPanel(p));
                }
            }
            p.start();
        }

        if (ce instanceof ControllerErrorEvent) {
            p.removeControllerListener(this);
            PlayerWindow pw = find(p);
            if (pw != null) {
                pw.close();
                playerWindows.removeElement(pw);
            }
            System.err.println("Receiver internal error: " + ce);
        }

    }

    public boolean isDone() {
        return playerWindows.size() == 0; //判断窗口数目是否为零
    }

    PlayerWindow find(Player p) {
        for (int i = 0; i < playerWindows.size(); i++) {
            PlayerWindow pw = (PlayerWindow) playerWindows.elementAt(i);
            if (pw.player == p) {
                return pw;
            }
        }
        return null;
    }

    PlayerWindow find(ReceiveStream strm) {
        for (int i = 0; i < playerWindows.size(); i++) {
            PlayerWindow pw = (PlayerWindow) playerWindows.elementAt(i);
            if (pw.stream == strm) {
                return pw;
            }
        }
        return null;
    }

    class PlayerPanel extends JPanel {

        Component vc, cc;

        PlayerPanel(Player p) {
            setLayout(new BorderLayout());
            if ((vc = p.getVisualComponent()) != null) {
                add("Center", vc);
            }
        }

        public Dimension getPreferredSize() {
            int w = 0, h = 0;
            if (isMulticastAddress) {
                if (vc != null) {
                    Dimension size = vc.getPreferredSize();
                    w = size.width;
                    h = size.height;
                }
                if (cc != null) {
                    Dimension size = cc.getPreferredSize();
                    if (w == 0) {
                        w = size.width;
                    }
                    h += size.height;
                }
                if (w < 160) {
                    w = 160;
                }
                return new Dimension(w, h);
            } else {
                return new Dimension(227, 186);
            }
        }
    }


    class PlayerWindow extends JFrame {

        Player player;
        ReceiveStream stream;

        PlayerWindow(Player p, ReceiveStream strm) {
            player = p;
            stream = strm;
        }

        public void initialize() {
            add(new PlayerPanel(player));
        }

        public void close() {
            player.close();
            setVisible(false);
            dispose();
        }

        public void addNotify() {
            super.addNotify();
            pack();
        }
    }


    Format checkForVideoSizes(Format original, Format supported) {

        int width, height;
        Dimension size = ((VideoFormat) original).getSize();
        Format jpegFmt = new Format(VideoFormat.JPEG_RTP);
        Format h263Fmt = new Format(VideoFormat.H263_RTP);

        if (supported.matches(jpegFmt)) {
            // For JPEG, make sure width and height are divisible by 8.
            width = (size.width % 8 == 0 ? size.width :
                     (int) (size.width / 8) * 8);
            height = (size.height % 8 == 0 ? size.height :
                      (int) (size.height / 8) * 8);
        } else if (supported.matches(h263Fmt)) {
            // For H.263, we only support some specific sizes.
            if (size.width < 128) {
                width = 128;
                height = 96;
            } else if (size.width < 176) {
                width = 176;
                height = 144;
            } else {
                width = 352;
                height = 288;
            }
        } else {
            // We don't know this particular format.  We'll just
            // leave it alone then.
            return supported;
        }

        return (new VideoFormat(null,
                                new Dimension(width, height),
                                Format.NOT_SPECIFIED,
                                null,
                                Format.NOT_SPECIFIED)).intersects(supported);
    }

    // Setting the encoding quality to the specified value on the JPEG encoder.
    // 0.5 is a good default.
    void setJPEGQuality(Player p, float val) {

        Control cs[] = p.getControls();
        QualityControl qc = null;
        VideoFormat jpegFmt = new VideoFormat(VideoFormat.JPEG);

        // Loop through the controls to find the Quality control for
        // the JPEG encoder.
        for (int i = 0; i < cs.length; i++) {

            if (cs[i] instanceof QualityControl && cs[i] instanceof Owned) {
                Object owner = ((Owned) cs[i]).getOwner();

                // Check to see if the owner is a Codec.
                // Then check for the output format.
                if (owner instanceof Codec) {
                    Format fmts[] = ((Codec) owner).getSupportedOutputFormats(null);
                    for (int j = 0; j < fmts.length; j++) {
                        if (fmts[j].matches(jpegFmt)) {
                            qc = (QualityControl) cs[i];
                            qc.setQuality(val);
                            System.err.println("- Setting quality to " +
                                               val + " on " + qc);
                            break;
                        }
                    }
                }
                if (qc != null) {
                    break;
                }
            }
        }
    }

    public Integer stateLock = new Integer(0);
    public boolean failed = false;

    Integer getStateLock() {
        return stateLock;
    }

    void setFailed() {
        failed = true;
    }

    public synchronized boolean waitForState(Processor p, int state) {
        p.addControllerListener(new StateListener());
        failed = false;

        // Call the required method on the processor
        if (state == Processor.Configured) {
            p.configure();
        } else if (state == Processor.Realized) {
            p.realize();
        }

        while (p.getState() < state && !failed) {
            synchronized (getStateLock()) {
                try {
                    getStateLock().wait();
                } catch (InterruptedException ie) {
                    return false;
                }
            }
        }

        if (failed) {
            return false;
        } else {
            return true;
        }
    }


    class StateListener implements ControllerListener {

        public void controllerUpdate(ControllerEvent ce) {

            // If there was an error during configure or
            // realize, the processor will be closed
            if (ce instanceof ControllerClosedEvent) {
                setFailed();
            }

            // All controller events, send a notification
            // to the waiting thread in waitForState method.
            if (ce instanceof ControllerEvent) {
                synchronized (getStateLock()) {
                    getStateLock().notifyAll();
                }
            }
        }
    }


    public String getLocalAddress() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
        }
        return addr.getHostAddress();
    }

}

