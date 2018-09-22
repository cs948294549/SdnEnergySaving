package cn.upc.entity;

import java.io.Serializable;

import cn.upc.res.Util;

/**
 * ��Ҫ�·��������¼,�����ӦFloodlightΪ0.91�����£�1.0�汾�����ı䣬URL·��Ҳ�иı�
 * 
 * @author ����չ
 *
 */
public class Flow implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * switch(xx:xx:xx:xx:xx:xx:xx:xx)
	 */
	private String sw;
	/**
	 * name(flow-mod-1)
	 */
	private String name;
	/**
	 * actions(output=3)
	 */
	private String actions;
	/**
	 * priority(32767)
	 */
	private String priority = "32767";
	/**
	 * active(true)
	 */
	private String active = "true";
	/**
	 * ingress-port(3)
	 */
	private String ingressPort;
	/**
	 * src-mac(xx:xx:xx:xx:xx:xx)
	 */
	private String srcMac;
	/**
	 * dst-mac(xx:xx:xx:xx:xx:xx)
	 */
	private String dstMac;
	/**
	 * vlan-id
	 */
	private String vlanId;
	/**
	 * vlan-priority
	 */
	private String vlanPriority;
	/**
	 * ether-type
	 */
	private String etherType;
	/**
	 * tos-bits
	 */
	private String tosBits;
	/**
	 * protocol
	 */
	private String protocol;
	/**
	 * src-ip(xx.xx.xx.xx)
	 */
	private String srcIp;
	/**
	 * dst-ip(xx.xx.xx.xx)
	 */
	private String dstIp;
	/**
	 * src-port(3)UDP/TCP�Ķ˿ڣ��������ϵ�������Ʒ�����ǽ�������
	 */
	private String srcPort;
	/**
	 * dst-port(2)UDP/TCP�Ķ˿ڣ��������ϵ�������Ʒ�����ǽ�������
	 */
	private String dstPort;

	/**
	 * @param index���кţ�������·���е�λ��
	 * @param sw������
	 * @param ingressPortƥ����
	 * @param outPort������
	 * @param src����ԴIP��ַ
	 * @param dst����IPĿ�ĵ�ַ
	 * 
	 */
	public Flow(int index, String sw, int ingressPort, int outPort, Device src, Device dst) {
		this.name = src.macs[0] + "--" + dst.macs[0] + ":" + index;
		this.sw = sw;
		this.ingressPort = String.valueOf(ingressPort);
		this.actions = "output=" + outPort;
		// this.srcMac = src.macs[0];
		this.dstMac = dst.macs[0];
		// this.srcIp = src.ipv4s[0];
		// this.dstIp = dst.ipv4s[0];
	}

	public String toPushString() {
		return new FlowBuilder().append("switch", sw).append("name", name).append("actions", actions)
				.append("priority", priority).append("active", active).append("ingress-port", ingressPort)
				.append("src-mac", srcMac).append("dst-mac", dstMac).append("vlan-id", vlanId)
				.append("vlan-priority", vlanPriority).append("ether-type", etherType).append("tos-bits", tosBits)
				.append("protocol", protocol).append("src-ip", srcIp).append("dst-ip", dstIp)
				.append("srd-port", srcPort).append("dst-port", dstPort).appendBrace().toString();
	}

	public String toDeleteString() {
		return new FlowBuilder("name", name).appendBrace().toString();
	}

	@Override
	public String toString() {
		return ingressPort + "->" + Util.rename(sw) + ":" + actions;
	}

	/**
	 * ��������
	 * 
	 * @author ����չ
	 *
	 */
	public class FlowBuilder {
		public StringBuilder sb;

		public FlowBuilder() {
			sb = new StringBuilder();
		}

		public FlowBuilder(String key, String value) {
			if (isNotVoid(value)) {
				sb = new StringBuilder("\"" + key + "\":\"" + value + "\"");
			} else {
				sb = new StringBuilder();
			}
		}

		/**
		 * ָ�벻Ϊ�գ�����Ҳ��Ϊ��
		 * 
		 * @param value
		 * @return
		 */
		private boolean isNotVoid(String value) {
			return value != null && !value.equals("");
		}

		public FlowBuilder append(FlowBuilder fb) {
			this.sb.append(fb.sb);
			return this;
		}

		public FlowBuilder append(String key, String value) {
			if (isNotVoid(value)) {
				if (sb.length() == 0) {
					sb.append("\"" + key + "\":\"" + value + "\"");
				} else {
					sb.append(",\"" + key + "\":\"" + value + "\"");
				}
			}
			return this;
		}

		@Override
		public String toString() {
			return sb.toString();
		}

		/**
		 * ������Ӵ�����
		 * 
		 * @return
		 */
		public FlowBuilder appendBrace() {
			StringBuilder sb = new StringBuilder("{").append(this.sb).append("}");
			this.sb = sb;
			return this;
		}
	}
}
