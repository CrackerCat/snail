package com.acgist.snail.net.upnp.bootstrap;

import org.w3c.dom.Element;

import com.acgist.snail.protocol.Protocol;
import com.acgist.snail.system.XML;

/**
 * <p>UPNP请求</p>
 * 
 * @author acgist
 * @since 1.0.0
 */
public final class UpnpRequest {

	/**
	 * <p>SOAP协议：{@value}</p>
	 */
	private static final String NAMESPACE_URI = "http://schemas.xmlsoap.org/soap/envelope/";
	/**
	 * <p>SOAP协议：{@value}</p>
	 */
	private static final String ENCODING_STYLE = "http://schemas.xmlsoap.org/soap/encoding/";
	
	/**
	 * <p>XML工具</p>
	 */
	private XML xml;
	/**
	 * <p>主体内容</p>
	 */
	private Element body;
	/**
	 * <p>服务类型</p>
	 */
	private String serviceType;
	
	private UpnpRequest() {
	}
	
	public static final UpnpRequest newRequest(String serviceType) {
		final UpnpRequest request = new UpnpRequest();
		request.build();
		request.serviceType = serviceType;
		return request;
	}
	
	/**
	 * <p>新建报文</p>
	 */
	private void build() {
		this.xml = XML.build();
		final Element envelope = this.xml.elementNS(xml.document(), "s:Envelope", NAMESPACE_URI);
		envelope.setAttributeNS(NAMESPACE_URI, "encodingStyle", ENCODING_STYLE);
		this.body = this.xml.element(envelope, "s:Body");
	}

	/**
	 * <p>外网IP地址</p>
	 * <pre>
	 * <xmp>
	 * <?xml version="1.0"?>
	 * <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
	 * 	<s:Body>
	 * 		<u:GetExternalIPAddress xmlns:u="urn:schemas-upnp-org:service:WANIPConnection:1"></u:GetExternalIPAddress>
	 * 	</s:Body>
	 * </s:Envelope>
	 * </xmp>
	 * </pre>
	 * 
	 * @return 请求内容
	 */
	public String buildGetExternalIPAddress() {
		this.xml.elementNS(this.body, "u:GetExternalIPAddress", this.serviceType);
		return xml();
	}
	
	/**
	 * <p>端口映射信息</p>
	 * <pre>
	 * <xmp>
	 * <?xml version="1.0"?>
	 * <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
	 * 	<s:Body>
	 * 		<u:GetSpecificPortMappingEntry xmlns:u="urn:schemas-upnp-org:service:WANIPConnection:1">
	 * 			<NewRemoteHost></NewRemoteHost>
	 * 			<NewExternalPort>8080</NewExternalPort><!-- 外网端口 -->
	 * 			<NewProtocol>TCP</NewProtocol>
	 * 		</u:GetSpecificPortMappingEntry>
	 * 	</s:Body>
	 * </s:Envelope>
	 * </xmp>
	 * </pre>
	 * 
	 * @param portExt 外网端口
	 * @param protocol 协议
	 * 
	 * @return 请求内容
	 */
	public String buildGetSpecificPortMappingEntry(int portExt, Protocol.Type protocol) {
		final Element mapping = this.xml.elementNS(this.body, "u:GetSpecificPortMappingEntry", this.serviceType);
		this.xml.element(mapping, "NewRemoteHost", "");
		this.xml.element(mapping, "NewExternalPort", String.valueOf(portExt));
		this.xml.element(mapping, "NewProtocol", protocol.name().toUpperCase());
		return xml();
	}
	
	/**
	 * <p>端口映射</p>
	 * <pre>
	 * <xmp>
	 * <?xml version="1.0"?>
	 * <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
	 * 	<s:Body>
	 * 		<u:AddPortMapping xmlns:u="urn:schemas-upnp-org:service:WANIPConnection:1">
	 * 			<NewRemoteHost></NewRemoteHost>
	 * 			<NewExternalPort>8080</NewExternalPort><!-- 外网端口 -->
	 * 			<NewProtocol>TCP</NewProtocol>
	 * 			<NewInternalPort>8080</NewInternalPort><!-- 内网端口 -->
	 * 			<NewInternalClient>192.168.1.100</NewInternalClient><!-- 内网地址 -->
	 * 			<NewEnabled>1</NewEnabled>
	 * 			<NewPortMappingDescription>描述</NewPortMappingDescription>
	 * 			<NewLeaseDuration>0</NewLeaseDuration>
	 * 		</u:AddPortMapping>
	 * 	</s:Body>
	 * </s:Envelope>
	 * </xmp>
	 * </pre>
	 * 
	 * @param port 内网端口
	 * @param address 内网地址
	 * @param portExt 外网端口
	 * @param protocol 协议
	 * 
	 * @return 请求内容
	 */
	public String buildAddPortMapping(int port, String address, int portExt, Protocol.Type protocol) {
		final Element mapping = this.xml.elementNS(this.body, "u:AddPortMapping", this.serviceType);
		this.xml.element(mapping, "NewRemoteHost", "");
		this.xml.element(mapping, "NewExternalPort", String.valueOf(portExt));
		this.xml.element(mapping, "NewProtocol", protocol.name().toUpperCase());
		this.xml.element(mapping, "NewInternalPort", String.valueOf(port));
		this.xml.element(mapping, "NewInternalClient", address);
		this.xml.element(mapping, "NewEnabled", "1");
		this.xml.element(mapping, "NewPortMappingDescription", "Snail");
		this.xml.element(mapping, "NewLeaseDuration", "0");
		return xml();
	}
	
	/**
	 * <p>删除端口映射</p>
	 * <pre>
	 * <xmp>
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
	 * 	<s:Body>
	 * 		<u:DeletePortMapping xmlns:u="urn:schemas-upnp-org:service:WANIPConnection:1">
	 * 			<NewRemoteHost></NewRemoteHost>
	 * 			<NewExternalPort>8080</NewExternalPort><!-- 外网端口 -->
	 * 			<NewProtocol>TCP</NewProtocol>
	 * 		</u:DeletePortMapping>
	 * 	</s:Body>
	 * </s:Envelope>
	 * </xmp>
	 * </pre>
	 * 
	 * @param portExt 外网端口
	 * @param protocol 协议
	 * 
	 * @return 请求内容
	 */
	public String buildDeletePortMapping(int portExt, Protocol.Type protocol) {
		final Element mapping = this.xml.elementNS(body, "u:DeletePortMapping", this.serviceType);
		this.xml.element(mapping, "NewRemoteHost", "");
		this.xml.element(mapping, "NewExternalPort", String.valueOf(portExt));
		this.xml.element(mapping, "NewProtocol", protocol.name().toUpperCase());
		return xml();
	}

	/**
	 * <p>XML文本输出</p>
	 * 
	 * @return XML文本
	 */
	private String xml() {
		return this.xml.xml(false);
	}

}
