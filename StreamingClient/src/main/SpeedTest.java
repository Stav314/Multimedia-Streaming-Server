package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class SpeedTest {
	SpeedTestSocket s;
	private float downloadSpeed = 0;
	//Boolean isComplete = false;
	float percentage=0;
	static Logger log = LogManager.getLogger(SpeedTest.class);
	

	public SpeedTest()  {
		s = new SpeedTestSocket();
		
		// add a listener to wait for speed test completion and progress
		s.addSpeedTestListener(new ISpeedTestListener()
		{
	            @Override
	            public void onCompletion(SpeedTestReport speedTestReport) {
	            	s.closeSocket();
	            	percentage=100;
	            	downloadSpeed = speedTestReport.getTransferRateBit().floatValue()/ (1024 * 1024); //returns speed in Mbps
	            	log.debug("Speedtest complete! Current speed is " + downloadSpeed + "Mbps");
	            }

	            @Override
	            public void onProgress(float percent, SpeedTestReport speedTestReport) {
	            	percentage=percent;
	            	//log.debug(percentage);
	            }

	            @Override
	            public void onError(SpeedTestError speedTestError, String s) {
	                log.error(s);
	            }
	    });
	}
	
	public void testSpeed(String URL) {
		s.startFixedDownload(URL,5000); //for 5 seconds
    }
	
	public float getSpeed() throws InterruptedException {
        return this.downloadSpeed;
	}
	
	public float getPercent() {
        return this.percentage;
    }
}
