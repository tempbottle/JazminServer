/**
 * 
 */
package jazmin.server.rpc;

import io.netty.channel.Channel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.LongAdder;

import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * @author yama
 * 23 Dec, 2014
 */
public class RPCSession {
	private static Logger logger=LoggerFactory.get(RPCSession.class);
	//
	String remoteHostAddress;
	int remotePort;
	String principal;
	String credential;
	Set<String>topics;
	Channel channel;
	Date createTime;
	boolean disablePushMessage;
	LongAdder sendPackageCount;
	LongAdder receivePackageCount;
	String cluster;
	boolean authed;
	//
	public RPCSession(){
		topics=new TreeSet<String>();
		createTime=new Date();
		sendPackageCount=new LongAdder();
		receivePackageCount=new LongAdder();
		authed=false;
	}
	/**
	 * @return the cluster
	 */
	public String getCluster() {
		return cluster;
	}
	/**
	 * @param cluster the cluster to set
	 */
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	
	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	
	public String getCredential() {
		return credential;
	}
	public void setCredential(String credential) {
		this.credential = credential;
	}
	//
	public void setRemoteHostAddress(String remoteHostAddress) {
		this.remoteHostAddress = remoteHostAddress;
	}
	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}
	//
	public void setChannel(Channel channel){
		this.channel=channel;
		if(channel!=null){
			SocketAddress remoteAddr=channel.remoteAddress();
			if(remoteAddr!=null){
				InetSocketAddress addr=(InetSocketAddress) remoteAddr;
				InetAddress ad=addr.getAddress();
				remoteHostAddress=ad.getHostAddress();
				remotePort=addr.getPort();
			}		
		}
	}
	/**
	 * return remote host address of this connection
	 */
	public String getRemoteHostAddress() {
		return remoteHostAddress;
	}
	/**
	 * return remote port of this connection
	 */
	public int getRemotePort() {
		return remotePort;
	}
	/**
	 * return true if this connection is already connected
	 */
	public boolean isConnected(){
		if(channel==null){
			return false;
		}
		return channel.isActive();
	}
	/**
	 * close this session 
	 */
	public void close(){
		try {
			channel.close().sync();
			channel=null;
		} catch (InterruptedException e) {
			logger.error(e.getMessage(),e);
		}
	}
	//
	public long getReceivePackageCount(){
		return receivePackageCount.longValue();
	}
	//
	public long getSendPackageCount(){
		return sendPackageCount.longValue();
	}
	//
	void receivePackage(){
		receivePackageCount.increment();
	}
	//
	public void subscribe(String topic){
		topics.add(topic);
	}
	//
	public Set<String> getTopics() {
		return topics;
	}
	//
	public Date getCreateTime(){
		return createTime;
	}
	//
	public boolean isDisablePushMessage(){
		return disablePushMessage;
	}
	//
	public void setDisablePushMessage(boolean dpm){
		disablePushMessage=dpm;
	}
	/**
	 * 
	 */
	public void write(RPCMessage message){
		channel.writeAndFlush(message);
		sendPackageCount.increment();
	}
	//
	@Override
	public String toString() {
		return "[RPCSession]"+principal+"/"+remoteHostAddress+":"+remotePort+"/auth:"+authed;
	}
}
