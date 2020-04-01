/* 
 * Copyright 2014 Aydin Rajaei, University of Sussex.
 * The Geo1 Simulator Project. 
 */
package report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Cast;
import core.DTNHost;
import core.GeoDTNHost;
import core.GeoSimScenario;
import core.Message;
import core.GeoMessage;
import core.MessageListener;
import core.GeoMessageListener;
import core.UpdateListener;

/**
 * Report for generating different kind of total statistics about message
 * relaying performance. Messages that were created during the warm up period
 * are ignored.
 * <P><strong>Note:</strong> if some statistics could not be created (e.g.
 * overhead ratio if no messages were delivered) "NaN" is reported for
 * double values and zero for integer median(s).
 */
public class GMStatsReport extends Report implements MessageListener, GeoMessageListener, UpdateListener {
	
	private Map<String, Double> creationTimes;
	private List<Double> latencies;
	private List<Integer> hopCounts;
	private List<Double> msgBufferTime;
	private List<Double> rtt; // round trip times
	
	private int nrofDropped;
	private int nrofRemoved;
	private int nrofStarted;
	private int nrofAborted;
	private int nrofRelayed;
	private int nrofCreated;
	private int nrofResponseReqCreated;
	private int nrofResponseDelivered;
	private int nrofDelivered;
	
	
	//for GeoMessages:
	private Map<String, Double> geoCreationTimes;
	private List<Double> geoLatencies;
	private List<Integer> geoHopCounts;
	private List<Double> geoMsgBufferTime;
	private List<Double> geoRtt; // round trip times
	private Map<String, List<GeoDTNHost>> geoDestination;
	private Map<String, Double> ttlTable;
	private List<GeoMessage> createdGeoMessages;
	private List<GeoMessage> existedGeoMessages;
	
	private int nrofGeoDropped;
	private int nrofGeoRemoved;
	private int nrofGeoStarted;
	private int nrofGeoAborted;
	private int nrofGeoRelayed;
	private int nrofGeoCreated;
	private int nrofGeoResponseReqCreated;
	private int nrofGeoResponseDelivered;
	private int nrofGeoDelivered;
	
	/**
	 * Constructor.
	 */
	public GMStatsReport() {
		init();
	}

	@Override
	protected void init() {
		super.init();
		this.creationTimes = new HashMap<String, Double>();
		this.latencies = new ArrayList<Double>();
		this.msgBufferTime = new ArrayList<Double>();
		this.hopCounts = new ArrayList<Integer>();
		this.rtt = new ArrayList<Double>();
		
		this.nrofDropped = 0;
		this.nrofRemoved = 0;
		this.nrofStarted = 0;
		this.nrofAborted = 0;
		this.nrofRelayed = 0;
		this.nrofCreated = 0;
		this.nrofResponseReqCreated = 0;
		this.nrofResponseDelivered = 0;
		this.nrofDelivered = 0;
		
		this.geoCreationTimes = new HashMap<String, Double>();
		this.geoLatencies = new ArrayList<Double>();
		this.geoMsgBufferTime = new ArrayList<Double>();
		this.geoHopCounts = new ArrayList<Integer>();
		this.geoRtt = new ArrayList<Double>();
		this.geoDestination = new HashMap<String, List<GeoDTNHost>>();
		this.ttlTable = new HashMap<String, Double>();
		this.createdGeoMessages = new ArrayList<GeoMessage>();
		this.existedGeoMessages = new ArrayList<GeoMessage>();
		
		this.nrofGeoDropped = 0;
		this.nrofGeoRemoved = 0;
		this.nrofGeoStarted = 0;
		this.nrofGeoAborted = 0;
		this.nrofGeoRelayed = 0;
		this.nrofGeoCreated = 0;
		this.nrofGeoResponseReqCreated = 0;
		this.nrofGeoResponseDelivered = 0;
		this.nrofGeoDelivered = 0;
	}

	
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {
		if (isWarmupID(m.getId())) {
			return;
		}
		
		if (isCooldownID(m.getId())) {
			return;
		}
		
		if (dropped) {
			this.nrofDropped++;
		}
		else {
			this.nrofRemoved++;
		}
		
		this.msgBufferTime.add(getSimTime() - m.getReceiveTime());
	}

	public void geoMessageDeleted(GeoMessage m, GeoDTNHost where, boolean dropped) {
		if (isWarmupID(m.getId())) {
			return;
		}
		
		if (isCooldownID(m.getId())) {
			return;
		}
		
		if (dropped) {
			this.nrofGeoDropped++;
		}
		else {
			this.nrofGeoRemoved++;
		}
		
		this.geoMsgBufferTime.add(getSimTime() - m.getReceiveTime());
	}
	
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
		if (isWarmupID(m.getId())) {
			return;
		}
		
		if (isCooldownID(m.getId())) {
			return;
		}
		
		this.nrofAborted++;
	}
	
	public void geoMessageTransferAborted(GeoMessage m, GeoDTNHost from, GeoDTNHost to) {
		if (isWarmupID(m.getId())) {
			return;
		}
		
		if (isCooldownID(m.getId())) {
			return;
		}
		
		this.nrofGeoAborted++;
	}

	public void messageTransferred(Message m, DTNHost from, DTNHost to,
			boolean finalTarget) {
		if (isWarmupID(m.getId())) {
			return;
		}
		
		if (isCooldownID(m.getId())) {
			return;
		}

		this.nrofRelayed++;
		if (finalTarget) {
			this.latencies.add(getSimTime() - 
				this.creationTimes.get(m.getId()) );
			this.nrofDelivered++;
			this.hopCounts.add(m.getHops().size() - 1);
			
			if (m.isResponse()) {
				this.rtt.add(getSimTime() -	m.getRequest().getCreationTime());
				this.nrofResponseDelivered++;
			}
		}
	}

	public void geoMessageTransferred(GeoMessage m, GeoDTNHost from, GeoDTNHost to,
			boolean finalTarget) {
		if (isWarmupID(m.getId())) {
			return;
		}

		if (isCooldownID(m.getId())) {
			return;
		}
		
		this.nrofGeoRelayed++;
		if (finalTarget) {
			this.geoLatencies.add(getSimTime() - 
			this.geoCreationTimes.get(m.getId()) );
			this.nrofGeoDelivered++;
			this.geoHopCounts.add(m.getHops().size() - 1);
			
			if (m.isResponse()) {
				this.rtt.add(getSimTime() -	m.getRequest().getCreationTime());
				this.nrofResponseDelivered++;
			}
		}
	}
	
	public void newMessage(Message m) {
		if (isWarmup()) {
			addWarmupID(m.getId());
			return;
		}
		
		if (isCooldown()) {
			addCooldownID(m.getId());
			return;
		}
		
		this.creationTimes.put(m.getId(), getSimTime());
		this.nrofCreated++;
		if (m.getResponseSize() > 0) {
			this.nrofResponseReqCreated++;
		}
	}
	
	public void newGeoMessage(GeoMessage m) {
		if (isWarmup()) {
			addWarmupID(m.getId());
			return;
		}

		if (isCooldown()) {
			addCooldownID(m.getId());
			return;
		}
		
		this.geoCreationTimes.put(m.getId(), getSimTime());
		this.nrofGeoCreated++;
		if (m.getResponseSize() > 0) {
			this.nrofResponseReqCreated++;
		}
		
		String temp = m.getId();
		List <GeoDTNHost> Nodes = new ArrayList<GeoDTNHost>();
		this.geoDestination.put(temp, Nodes);
		this.createdGeoMessages.add(m.replicate());
		this.existedGeoMessages.add(m.replicate());
		
		double time = this.getSimTime() + (60.0 * m.getTtl()); //ttl returns as minutes, but simTime is based on seconds
		this.ttlTable.put(temp, time);
		
	}
	
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
		if (isWarmupID(m.getId())) {
			return;
		}
		
		if (isCooldownID(m.getId())) {
			return;
		}

		this.nrofStarted++;
	}
	
	public void geoMessageTransferStarted(GeoMessage m, GeoDTNHost from, GeoDTNHost to) {
		if (isWarmupID(m.getId())) {
			return;
		}
		
		if (isCooldownID(m.getId())) {
			return;
		}

		this.nrofGeoStarted++;
	}
	
	public void updated(List<? extends DTNHost> hosts){
		dropExpired();
		List<GeoDTNHost> Hosts = GeoSimScenario.getInstance().getHosts();
		for(int i=0; i<Hosts.size(); i++)
		{
			for(int j=0; j<existedGeoMessages.size(); j++)
			{
				for(Cast getTo : existedGeoMessages.get(j).getTo()) {
					Boolean check = getTo.checkThePoint(Hosts.get(i).getLocation());
					if (check)
					{
						Boolean existed = false;
						List<GeoDTNHost> Destinations = this.geoDestination.get(existedGeoMessages.get(j).getId());
						for(int k=0; k<Destinations.size(); k++)
						{
							if(Destinations.get(k).toString() == Hosts.get(i).toString())
							{
								existed = true;
							}
						}
						if (!existed)// && (this.getSimTime() <= (this.ttlTable.get(existedGeoMessages.get(j).toString())-(1200.0)))) //:D
						{
							Destinations.add(Hosts.get(i));
							this.geoDestination.put(existedGeoMessages.get(j).getId(), Destinations);
						}
					}
				}
			}
		}
	}

	private void dropExpired() {
		for (Map.Entry<String, Double> entry : ttlTable.entrySet()) {
		    String key = entry.getKey();
		    double value = entry.getValue();
		    if (value <= this.getSimTime()){
		    	for (int i = 0; i < existedGeoMessages.size(); i++) {
		    		if (key == existedGeoMessages.get(i).getId()){
		    		existedGeoMessages.remove(i);
		    		}
		    	}
		    }
		}
		
	}

	@Override
	public void done() {
		write("Message stats for scenario " + getScenarioName() + 
				"\nsim_time: " + format(getSimTime()));
		double deliveryProb = 0; // delivery probability
		double responseProb = 0; // request-response success probability
		double overHead = Double.NaN;	// overhead ratio
		
		if (this.nrofCreated > 0) {
			deliveryProb = (1.0 * this.nrofDelivered) / this.nrofCreated;
		}
		if (this.nrofDelivered > 0) {
			overHead = (1.0 * (this.nrofRelayed - this.nrofDelivered)) /
				this.nrofDelivered;
		}
		if (this.nrofResponseReqCreated > 0) {
			responseProb = (1.0* this.nrofResponseDelivered) / 
				this.nrofResponseReqCreated;
		}
		
		String statsText = "created: " + this.nrofCreated + 
			"\nstarted: " + this.nrofStarted + 
			"\nrelayed: " + this.nrofRelayed +
			"\naborted: " + this.nrofAborted +
			"\ndropped: " + this.nrofDropped +
			"\nremoved: " + this.nrofRemoved +
			"\ndelivered: " + this.nrofDelivered +
			"\ndelivery_prob: " + format(deliveryProb) +
			"\nresponse_prob: " + format(responseProb) + 
			"\noverhead_ratio: " + format(overHead) + 
			"\nlatency_avg: " + getAverage(this.latencies) +
			"\nlatency_med: " + getMedian(this.latencies) + 
			"\nhopcount_avg: " + getIntAverage(this.hopCounts) +
			"\nhopcount_med: " + getIntMedian(this.hopCounts) + 
			"\nbuffertime_avg: " + getAverage(this.msgBufferTime) +
			"\nbuffertime_med: " + getMedian(this.msgBufferTime) +
			"\nrtt_avg: " + getAverage(this.rtt) +
			"\nrtt_med: " + getMedian(this.rtt)
			;
		
		write(statsText);
		
		write("\n\nGeoMessage stats for scenario " + getScenarioName() + 
				"\nsim_time: " + format(getSimTime()));
		double geoDeliveryProb = 0; // delivery probability
		double geoResponseProb = 0; // request-response success probability
		double geoOverHead = Double.NaN;	// overhead ratio
		
		if (this.nrofGeoCreated > 0) {
			
			List <Double> deliProbs = new ArrayList<Double>();
			double sum = 0;
			
			for(int i=0; i<createdGeoMessages.size(); i++)
			{
				int nrofdeli = 0;
				List<GeoDTNHost> dList = this.geoDestination.get(createdGeoMessages.get(i).getId());
				for(int j=0; j<dList.size(); j++)
				{
					boolean check = dList.get(j).getGeoRouter().isDeliveredGeoMessage(createdGeoMessages.get(i));
					if (check)
					{
						nrofdeli++;
					}
				}
				
				double deliRatio = 0;
				if (nrofdeli != 0 && dList.size() != 0)
				{
				deliRatio = (1.0 * nrofdeli) / dList.size();
				}
				
				deliProbs.add(deliRatio);
				
			}
			
			for (int k=0; k<deliProbs.size(); k++)
			{
				sum += deliProbs.get(k);
			}
			geoDeliveryProb = (1.0 * sum) / deliProbs.size();
			
		}
		if (this.nrofGeoDelivered > 0) {
			geoOverHead = (1.0 * (this.nrofGeoRelayed - this.nrofGeoDelivered)) /
				this.nrofDelivered;
		}
		if (this.nrofGeoResponseReqCreated > 0) {
			geoResponseProb = (1.0* this.nrofGeoResponseDelivered) / 
				this.nrofGeoResponseReqCreated;
		}
		
		String geoStatsText = "created: " + this.nrofGeoCreated + 
			"\nstarted: " + this.nrofGeoStarted + 
			"\nrelayed: " + this.nrofGeoRelayed +
			"\naborted: " + this.nrofGeoAborted +
			"\ndropped: " + this.nrofGeoDropped +
			"\nremoved: " + this.nrofGeoRemoved +
			"\ndelivered: " + this.nrofGeoDelivered +
			"\ndelivery_prob: " + format(geoDeliveryProb) +
			"\nresponse_prob: " + format(geoResponseProb) + 
			"\noverhead_ratio: " + format(geoOverHead) + 
			"\nlatency_avg: " + getAverage(this.geoLatencies) +
			"\nlatency_med: " + getMedian(this.geoLatencies) + 
			"\nhopcount_avg: " + getIntAverage(this.geoHopCounts) +
			"\nhopcount_med: " + getIntMedian(this.geoHopCounts) + 
			"\nbuffertime_avg: " + getAverage(this.geoMsgBufferTime) +
			"\nbuffertime_med: " + getMedian(this.geoMsgBufferTime) +
			"\nrtt_avg: " + getAverage(this.geoRtt) +
			"\nrtt_med: " + getMedian(this.geoRtt)
			;
		
		write(geoStatsText);
		
		//
		for(int i=0; i<createdGeoMessages.size(); i++ )
		{
			int nrofdeli = 0;
			write ("\n" + createdGeoMessages.get(i).toString() + "\n");
			List<GeoDTNHost> dList = this.geoDestination.get(createdGeoMessages.get(i).getId());
			for (int j=0; j<dList.size(); j++)
			{
				
				boolean check = dList.get(j).getGeoRouter().isDeliveredGeoMessage(createdGeoMessages.get(i));
				if (check)
				{
					nrofdeli++;
				}
				write(dList.get(j).toString());
			}
			write(nrofdeli + "\n");
		}
		
		//
		
		super.done();
	}
	
}
